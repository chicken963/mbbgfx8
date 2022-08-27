package ru.orthodox.mbbg.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.repositories.BlankRepository;
import ru.orthodox.mbbg.repositories.RoundRepository;
import ru.orthodox.mbbg.services.viewGameBlanks.ViewBlanksService;

import javax.annotation.PostConstruct;
import java.awt.print.PrinterException;

import static ru.orthodox.mbbg.utils.common.CustomFontDealer.setDefaultFont;
import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findAllLabeledRecursively;

@Slf4j
@Configurable
public class ViewBlanksController {

    @Autowired
    private BlankRepository blankRepository;
    @Autowired
    private RoundRepository roundRepository;

    private ViewBlanksService viewBlanksService;

    @FXML
    private TabPane blanksMiniatureTabPane;
    @FXML
    private Tab blanksMiniatureSampleTab;
    @FXML
    private Button blankMiniature;

    @FXML
    private AnchorPane blankPreview;
    @FXML
    private Label blankNumber;
    @FXML
    private Label roundName;
    @FXML
    private Label blankItem;
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
    private VBox blankPreviewVbox;
    @FXML
    private Button saveToPng;

    private Scene dialogScene;

    @Setter
    private Game game;

    @PostConstruct
    private void init() {
        dialogScene = new Scene(container, 880, 560);
        dialogScene.getStylesheets().addAll("styleSheets/view-blanks.css",
                "styleSheets/scrollable-table.css", "styleSheets/tab-pane.css");
        blankPreview.setVisible(false);
    }

    public void render(Button sourceButton) {
        this.viewBlanksService = ViewBlanksService.builder()
                .blanksMiniatureTabPane(blanksMiniatureTabPane)
                .blankPreview(blankPreview)
                .game(game)
                .blanksMiniatureSampleTab(blanksMiniatureSampleTab)
                .blankRepository(blankRepository)
                .roundRepository(roundRepository)
                .miniatureGridRowConstraints(miniatureGridRowConstraints)
                .miniatureGridColumnConstraints(miniatureGridColumnConstraints)
                .blankMiniature(blankMiniature)
                .blankItem(blankItem)
                .build();
        viewBlanksService.fillTabPaneWithBlankMiniatures();
        setDefaultFont(findAllLabeledRecursively(blanksMiniatureTabPane).toArray(new Labeled[0]));
        setDefaultFont(roundName, blankNumber, saveToPng);

        final Stage popupStage = new Stage();
        popupStage.setTitle("Вот твои бланки для игры " + game.getName());
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(sourceButton.getScene().getWindow());
        popupStage.setScene(dialogScene);
        popupStage.show();
    }

    @FXML
    private void showBlankByMiniature(MouseEvent mouseEvent) {
        Button eventSourceButton = (Button) mouseEvent.getSource();
        viewBlanksService.renderBlankByMiniature(eventSourceButton);
    }

    @FXML
    private void emptyBlanksPreview() {
        blankPreview.setVisible(false);
    }

    @FXML
    private void saveToPng() throws PrinterException {
        viewBlanksService.saveToPng();
    }

}
