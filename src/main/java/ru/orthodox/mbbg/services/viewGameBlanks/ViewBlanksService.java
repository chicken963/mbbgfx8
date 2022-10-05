package ru.orthodox.mbbg.services.viewGameBlanks;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.Blank;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.model.proxy.viewblanks.BlankPreviewAnchorPane;
import ru.orthodox.mbbg.services.common.EventPublisherService;
import ru.orthodox.mbbg.services.model.RoundService;
import ru.orthodox.mbbg.services.play.blank.BlankService;
import ru.orthodox.mbbg.services.popup.PopupAlerter;
import ru.orthodox.mbbg.utils.screen.ScreenService;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.orthodox.mbbg.utils.common.CustomFontDealer.setDefaultFont;
import static ru.orthodox.mbbg.utils.common.ThreadUtils.runSingleTaskInSeparateThread;
import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.*;
import static ru.orthodox.mbbg.utils.hierarchy.NodeDeepCopyProvider.createDeepCopy;

@Service
public class ViewBlanksService {

    private TabPane blanksMiniatureTabPane;
    private AnchorPane blankPreview;
    @Setter
    private Game game;
    private BlankPreviewAnchorPane blankPreviewAnchorPane;
    private Map<Round, List<Blank>> blanksOfTheGame;
    private Map<Round, Tab> roundsAndTabs;
    private Tab blanksMiniatureSampleTab;

    private RowConstraints miniatureGridRowConstraints;
    private ColumnConstraints miniatureGridColumnConstraints;
    private Button blankMiniature;
    private Label blankItem;
    @Autowired
    private RegionToImageSaverService regionToImageSaverService;
    private Button realMiniatureExample;
    private AnchorPane loaderContainer;
    private File rootDirectory;

    @Autowired
    private BlankService blankService;
    @Autowired
    private RoundService roundService;
    @Autowired
    private ScreenService screenService;
    @Autowired
    private PopupAlerter popupAlerter;
    @Autowired
    private EventPublisherService eventPublisherService;
    private File gameDirectory;


    public void fillTabPaneWithBlankMiniatures() {
        blanksMiniatureTabPane.getTabs().clear();
        List<Round> rounds = roundService.findByIds(game.getRoundIds());
        blanksOfTheGame = rounds.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        round -> blankService.findByIds(round.getBlanksIds()
                        )
                ));
        roundsAndTabs = rounds.stream().collect(Collectors.toMap(Function.identity(), round -> {
            Tab tab = createDeepCopy(blanksMiniatureSampleTab);
            tab.setText(round.getName());
            populateTabWithRoundContent(tab, round);
            blanksMiniatureTabPane.getTabs().add(tab);
            return tab;
        }));
    }

    public void renderBlankByMiniature(Button eventSourceButton) {
        String blankName = eventSourceButton.getText();
        String roundName = blanksMiniatureTabPane.getSelectionModel().getSelectedItem().getText();

        Round activeRound = roundsAndTabs.keySet()
                .stream()
                .filter(round -> round.getName().equals(roundName))
                .findFirst()
                .orElseThrow(() -> new NullPointerException(String.format("No round with name %s was found", roundName)));

        Blank blankToRender = blanksOfTheGame.get(activeRound)
                .stream()
                .filter(blank -> blank.getNumber().equals(blankName))
                .findFirst()
                .orElseThrow(() -> new NullPointerException(String.format("No blank with name %s was found", blankName)));

        blankPreviewAnchorPane = new BlankPreviewAnchorPane(blankPreview, blankToRender, roundName, blankItem);
        blankPreview.setVisible(true);
        blankPreviewAnchorPane.addItemsToPreview();
    }

    public void emptyBlanksPreview() {
        blankPreview.setVisible(false);
    }

    private void populateTabWithRoundContent(Tab tab, Round round) {
        GridPane miniaturesGrid = findTabElementByTypeAndStyleclass(tab, "miniatures-grid");
        miniaturesGrid.getColumnConstraints().addAll(
                Stream.generate(() -> createDeepCopy(miniatureGridColumnConstraints))
                        .limit(3)
                        .collect(Collectors.toList()));

        int numberOfRowsToReplicate = (int) Math.ceil(round.getBlanksIds().size() / 3.0);
        miniaturesGrid.getRowConstraints().addAll(
                Stream.generate(() -> createDeepCopy(miniatureGridRowConstraints))
                        .limit(numberOfRowsToReplicate)
                        .collect(Collectors.toList()));
        AtomicInteger counter = new AtomicInteger(0);
        List<Button> miniatures = blankService.findByIds(round.getBlanksIds())
                .stream()
                .map(Blank::getNumber)
                .map(blankNumber -> {
                    Button button = (Button) createDeepCopy(blankMiniature);
                    button.setText(blankNumber);
                    GridPane.setColumnIndex(button, counter.get() % 3);
                    GridPane.setRowIndex(button, counter.get() / 3);
                    counter.getAndIncrement();
                    return button;
                })
                .collect(Collectors.toList());
        realMiniatureExample = miniatures.get(0);
        miniaturesGrid.getChildren().setAll(miniatures);
    }

    public void saveToPng() {
        rootDirectory = getRootDirectoryFromDialog();
        if (rootDirectory != null) {
            Window blanksViewWindow = blanksMiniatureTabPane.getScene().getWindow();
            double windowWidth = blanksViewWindow.getWidth();
            double windowHeight = blanksMiniatureTabPane.getHeight();

            AnchorPane.setTopAnchor(loaderContainer, (windowHeight - loaderContainer.getHeight()) / 2);
            AnchorPane.setLeftAnchor(loaderContainer, (windowWidth - loaderContainer.getWidth()) / 2);

            loaderContainer.setVisible(true);
            blanksMiniatureTabPane.setEffect(new GaussianBlur());

            runSingleTaskInSeparateThread(this::generatePicturesFiles, "generate-pictures");
        }
    }

    private void generatePicturesFiles() {
        File gameDirectory = new File(rootDirectory.getAbsolutePath() + "\\" + game.getName());
        if (!gameDirectory.exists()) {
            gameDirectory.mkdir();
        }

        SingleSelectionModel<Tab> selectionModel = blanksMiniatureTabPane.getSelectionModel();
        for (Tab tab : blanksMiniatureTabPane.getTabs()) {
            selectionModel.select(tab);
            File roundDirectory = new File(gameDirectory.getAbsolutePath() + "\\" + tab.getText());
            if (!roundDirectory.exists()) {
                roundDirectory.mkdir();
            }
            List<Button> blankMiniatures = findElementsByTypeAndStyleclass(((Parent) tab.getContent()), "blank-miniature");
            for (Button blankMiniature : blankMiniatures) {
                renderBlankByMiniature(blankMiniature);
                File pictureFile = new File(roundDirectory.getAbsolutePath() + "\\" + blankMiniature.getText() + ".png");
                regionToImageSaverService.saveToImage(blankPreview, pictureFile);
            }
        }
        loaderContainer.setVisible(false);
        blanksMiniatureTabPane.setEffect(null);
        popupAlerter.invoke(blanksMiniatureTabPane.getScene().getWindow(),
                "Blanks saved to png",
                "Blanks are successfully stored as png files to the folder " + gameDirectory.getAbsolutePath());
    }

    private File getRootDirectoryFromDialog() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Please select root directory. Folder with the blanks will be created there.");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        return directoryChooser.showDialog(blankPreview.getScene().getWindow());
    }

    public void configureUIElements(AnchorPane loaderScene,
                                    TabPane blanksMiniatureTabPane,
                                    Tab blanksMiniatureSampleTab,
                                    RowConstraints miniatureGridRowConstraints,
                                    ColumnConstraints miniatureGridColumnConstraints,
                                    Button blankMiniature,
                                    HBox blankTemplateContainer) {
        this.loaderContainer = loaderScene;
        loaderScene.managedProperty().bind(loaderScene.visibleProperty());

        this.blanksMiniatureTabPane = blanksMiniatureTabPane;
        this.blanksMiniatureSampleTab = blanksMiniatureSampleTab;
        this.miniatureGridRowConstraints = miniatureGridRowConstraints;
        this.miniatureGridColumnConstraints = miniatureGridColumnConstraints;
        this.blankMiniature = blankMiniature;
        injectBlankTemplate(blankTemplateContainer);
    }

    private void injectBlankTemplate(HBox blankTemplateContainer) {
        this.blankPreview = (AnchorPane) createDeepCopy(screenService.getParentNode("blankTemplate"));
        this.blankItem = findElementByTypeAndStyleclass(blankPreview, "blank-item");
        blankPreview.setVisible(false);

        Label blankNumber = findElementByTypeAndStyleclass(blankPreview, "blank-number");
        Label roundName = findElementByTypeAndStyleclass(blankPreview, "round-name");
        setDefaultFont(blankNumber, roundName);

        blankTemplateContainer.getChildren().setAll(blankPreview);
    }
}
