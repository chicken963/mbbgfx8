package ru.orthodox.mbbg.services.viewGameBlanks;

import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.DirectoryChooser;
import lombok.Builder;
import ru.orthodox.mbbg.model.basic.Blank;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.repositories.BlankRepository;
import ru.orthodox.mbbg.repositories.RoundRepository;
import ru.orthodox.mbbg.model.proxy.viewblanks.BlankPreviewAnchorPane;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.orthodox.mbbg.utils.hierarchy.NodeDeepCopyProvider.createDeepCopy;
import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findRecursivelyByStyleClass;
import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findTabElementByTypeAndStyleclass;

@Builder
public class ViewBlanksService {
    //TODO add interface to extract common logic with progress view blanks
    private final TabPane blanksMiniatureTabPane;
    private final AnchorPane blankPreview;
    private final Game game;
    private BlankPreviewAnchorPane blankPreviewAnchorPane;
    private Map<Round, List<Blank>> blanksOfTheGame;
    private Map<Round, Tab> roundsAndTabs;
    private Tab blanksMiniatureSampleTab;
    private BlankRepository blankRepository;
    private RoundRepository roundRepository;
    private RowConstraints miniatureGridRowConstraints;
    private ColumnConstraints miniatureGridColumnConstraints;
    private Button blankMiniature;
    private Label blankItem;
    private RegionToImageSaverService regionToImageSaverService;

    public void fillTabPaneWithBlankMiniatures() {
        blanksMiniatureTabPane.getTabs().clear();
        List<Round> rounds = roundRepository.findByIds(game.getRoundIds());
        blanksOfTheGame = rounds.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        round -> blankRepository.findByIds(round.getBlanksIds()
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

    private void populateTabWithRoundContent(Tab tab, Round round) {
        GridPane miniaturesGrid = findTabElementByTypeAndStyleclass(tab, "miniaturesGrid");
        miniaturesGrid.getColumnConstraints().addAll(
                Stream.generate(() -> createDeepCopy(miniatureGridColumnConstraints))
                        .limit(3)
                        .collect(Collectors.toList()));
        miniaturesGrid.getRowConstraints().addAll(
                Stream.generate(() -> createDeepCopy(miniatureGridRowConstraints))
                        .limit((int) Math.ceil(round.getBlanksIds().size() / 3.0))
                        .collect(Collectors.toList()));
        AtomicInteger counter = new AtomicInteger(0);
        List<Button> miniatures = blankRepository.findByIds(round.getBlanksIds())
                .stream()
                .map(Blank::getNumber)
                .map(blankNumber -> {
                    Button button = createDeepCopy(blankMiniature);
                    button.setText(blankNumber);
                    GridPane.setColumnIndex(button, counter.get() % 3);
                    GridPane.setRowIndex(button, counter.get() / 3);
                    counter.getAndIncrement();
                    return button;
                })
                .collect(Collectors.toList());
        miniaturesGrid.getChildren().setAll(miniatures);
    }

    public void saveToPng() {
        RegionToImageSaverService regionToImageSaverService = new RegionToImageSaverService();
        File rootDirectory = getRootDirectoryFromDialog();

        File gameDirectory = new File(rootDirectory.getAbsolutePath() + "\\" + game.getName());
        if (!gameDirectory.exists()){
            gameDirectory.mkdir();
        }

        SingleSelectionModel<Tab> selectionModel = blanksMiniatureTabPane.getSelectionModel();

        for (Tab tab: blanksMiniatureTabPane.getTabs()) {
            selectionModel.select(tab);
            File roundDirectory = new File(gameDirectory.getAbsolutePath() + "\\" + tab.getText());
            if (!roundDirectory.exists()){
                roundDirectory.mkdir();
            }
            List<Button> blankMiniatures = findRecursivelyByStyleClass(((Parent) tab.getContent()), "blank-miniature")
                    .stream()
                    .map(node -> (Button) node)
                    .collect(Collectors.toList());
            for (Button blankMiniature: blankMiniatures) {
                this.renderBlankByMiniature(blankMiniature);
                File pictureFile = new File(roundDirectory.getAbsolutePath() + "\\" + blankMiniature.getText() + ".png");
                regionToImageSaverService.saveToImage(blankPreview, pictureFile);
            }
        }
    }

    private File getRootDirectoryFromDialog() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Please select root directory. Folder with the blanks will be created there.");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        return directoryChooser.showDialog(blankPreview.getScene().getWindow());
    }
}
