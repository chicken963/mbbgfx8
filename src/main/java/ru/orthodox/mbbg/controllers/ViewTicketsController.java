package ru.orthodox.mbbg.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.robot.Robot;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import ru.orthodox.mbbg.model.Card;
import ru.orthodox.mbbg.model.Game;
import ru.orthodox.mbbg.model.Round;
import ru.orthodox.mbbg.repositories.CardRepository;
import ru.orthodox.mbbg.repositories.GamesRepository;
import ru.orthodox.mbbg.repositories.RoundRepository;
import ru.orthodox.mbbg.utils.ui.viewTicketsScene.TicketPreviewAnchorPane;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.orthodox.mbbg.utils.ui.NodeDeepCopyProvider.createDeepCopy;
import static ru.orthodox.mbbg.utils.ui.newGameScene.ElementFinder.findTabElementByTypeAndStyleclass;

@Slf4j
@Configurable
public class ViewTicketsController {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private GamesRepository gamesRepository;
    @Autowired
    private RoundRepository roundRepository;

    @FXML
    private TabPane ticketsMiniatureTabPane;
    @FXML
    private Tab ticketsMiniatureSampleTab;
    @FXML
    private Button ticketMiniature;

    @FXML
    private AnchorPane ticketPreview;
    @FXML
    private Label ticketNumber;
    @FXML
    private Label roundName;
    @FXML
    private Label cardItem;
    @FXML
    private GridPane gridContent;
    @FXML
    private GridPane miniaturesGrid;
    @FXML
    private RowConstraints miniatureGridRowConstraints;
    @FXML
    private ColumnConstraints miniatureGridColumnConstraints;
    @FXML
    private Pane container;
    @FXML
    private VBox ticketPreviewVbox;

    private Scene dialogScene;

    @Setter
    private Game game;

    private Map<Round, List<Card>> cardsOfTheGame = new HashMap<>();
    private Map<Round, Tab> roundsAndTabs = new HashMap<>();
    private TicketPreviewAnchorPane ticketPreviewAnchorPane;


    @PostConstruct
    private void init() {
        dialogScene = new Scene(container, 880, 560);
        dialogScene.getStylesheets().add("style.css");
        ticketPreview.setVisible(false);
    }

    public void render(ActionEvent event) {
        List<Round> rounds = roundRepository.findByIds(game.getRoundIds());
        cardsOfTheGame = rounds.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        round -> cardRepository.findByIds(round.getCardsIds()
                        )
                ));
        ticketsMiniatureTabPane.getTabs().clear();
        roundsAndTabs = rounds.stream().collect(Collectors.toMap(Function.identity(), round -> {
            Tab tab = createDeepCopy(ticketsMiniatureSampleTab);
            tab.setText(round.getName());
            populateTabWithRoundContent(tab, round);
            ticketsMiniatureTabPane.getTabs().add(tab);
            return tab;
        }));
        final Stage popupStage = new Stage();
        popupStage.setTitle("Вот твои бланки для игры " + game.getName());
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
        popupStage.setScene(dialogScene);
        popupStage.show();
    }


    private void populateTabWithRoundContent(Tab tab, Round round) {
        GridPane miniaturesGrid = findTabElementByTypeAndStyleclass(tab, "miniaturesGrid");
        miniaturesGrid.getColumnConstraints().addAll(
                Stream.generate(() -> createDeepCopy(miniatureGridColumnConstraints))
                .limit(3)
                .collect(Collectors.toList()));
        miniaturesGrid.getRowConstraints().addAll(
                Stream.generate(() -> createDeepCopy(miniatureGridRowConstraints))
                .limit((int) Math.ceil(round.getCardsIds().size() / 3.0))
                .collect(Collectors.toList()));
        AtomicInteger counter = new AtomicInteger(0);
        List<Button> miniatures = cardRepository.findByIds(round.getCardsIds())
                .stream()
                .map(Card::getNumber)
                .map(cardNumber -> {
                    Button button = createDeepCopy(ticketMiniature);
                    button.setText(cardNumber);
                    GridPane.setColumnIndex(button, counter.get() % 3);
                    GridPane.setRowIndex(button, counter.get() / 3);
                    counter.getAndIncrement();
                    return button;
                })
                .collect(Collectors.toList());
        miniaturesGrid.getChildren().setAll(miniatures);
    }

    @FXML
    private void showTicketByMiniature(MouseEvent mouseEvent) {
        Button eventSourceButton = (Button) mouseEvent.getSource();
        String ticketName = eventSourceButton.getText();
        String roundName = ticketsMiniatureTabPane.getSelectionModel().getSelectedItem().getText();

        Round activeRound = roundsAndTabs.keySet()
                .stream()
                .filter(round -> round.getName().equals(roundName))
                .findFirst()
                .orElseThrow(() -> new NullPointerException(String.format("No round with name %s was found", roundName)));

        Card cardToRender = cardsOfTheGame.get(activeRound)
                .stream()
                .filter(card -> card.getNumber().equals(ticketName))
                .findFirst()
                .orElseThrow(() -> new NullPointerException(String.format("No ticket with name %s was found", ticketName)));

        ticketPreviewAnchorPane = new TicketPreviewAnchorPane(ticketPreview, cardToRender, roundName, cardItem);
        ticketPreview.setVisible(true);
        ticketPreviewAnchorPane.render();
    }

    @FXML
    private void emptyTicketsPreview(MouseEvent mouseEvent) {
        ticketPreview.setVisible(false);
    }

    @FXML
    private void saveToPng(ActionEvent event) throws PrinterException {
        DirectoryChooser directoryChooser = new DirectoryChooser();

        directoryChooser.setTitle("Please select root directory. Folder with the blanks will be created there.");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File rootDirectory = directoryChooser.showDialog(ticketPreview.getScene().getWindow());
        File gameDirectory = new File(rootDirectory.getAbsolutePath() + "\\" + game.getName());
        if (!gameDirectory.exists()){
            gameDirectory.mkdir();
        }
        File roundDirectory = new File(gameDirectory.getAbsolutePath() + "\\" + roundName.getText());
        if (!roundDirectory.exists()){
            roundDirectory.mkdir();
        }
        Button sourceTicketMiniature = (Button) event.getSource();
        Robot robot = new Robot();
        Bounds boundsInScene =  sourceTicketMiniature.localToScene(sourceTicketMiniature.getBoundsInLocal());
        double xMiddle = (boundsInScene.getMaxX() + boundsInScene.getMinX()) / 2;
        double yMiddle = (boundsInScene.getMaxY() + boundsInScene.getMinY()) / 2;
        robot.mouseMove(xMiddle, yMiddle);
        WritableImage snapshot = ticketPreview.snapshot(new SnapshotParameters(), null);
        saveImage(snapshot, sourceTicketMiniature.getText());
    }

    private void saveImage(WritableImage snapshot, String ticketNumber) {
        BufferedImage image;
        BufferedImage bufferedImage = new BufferedImage((int) ticketPreview.getWidth(),(int) ticketPreview.getHeight(), BufferedImage.TYPE_INT_ARGB);

        File file = new File(System.getProperty("user.home") + "\\mbbg\\" + ticketNumber + ".png");
        image = javafx.embed.swing.SwingFXUtils.fromFXImage(snapshot, bufferedImage);
        try {
            Graphics2D gd = (Graphics2D) image.getGraphics();
            gd.translate(ticketPreview.getWidth(), ticketPreview.getHeight());
            ImageIO.write(image, "png", file);
        } catch (IOException ex) {
            ex.printStackTrace();
        };
    }
}
