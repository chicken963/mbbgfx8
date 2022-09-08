package ru.orthodox.mbbg.controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import ru.orthodox.mbbg.enums.BlanksStatus;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.services.popup.PopupAlerter;
import ru.orthodox.mbbg.services.viewGameBlanks.ViewBlanksService;

import javax.annotation.PostConstruct;
import java.awt.print.PrinterException;

import static ru.orthodox.mbbg.utils.common.CustomFontDealer.setDefaultFont;
import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findAllLabeledRecursively;

@Slf4j
@Configurable
public class ViewBlanksController {

    @Autowired
    private ViewBlanksService viewBlanksService;
    @Autowired
    private PopupAlerter popupAlerter;

    @FXML
    private AnchorPane loaderScene;
    @FXML
    private TabPane blanksMiniatureTabPane;
    @FXML
    private Tab blanksMiniatureSampleTab;
    @FXML
    private Button blankMiniature;
    @FXML
    private HBox blankTemplateContainer;

    @FXML
    private RowConstraints miniatureGridRowConstraints;
    @FXML
    private ColumnConstraints miniatureGridColumnConstraints;
    @FXML
    private Pane container;
    @FXML
    private Button saveToPng;

    private Scene dialogScene;

    @PostConstruct
    private void init() {
        dialogScene = new Scene(container, 880, 605);
        dialogScene.getStylesheets().addAll("styleSheets/view-blanks.css",
                "styleSheets/scrollable-table.css", "styleSheets/tab-pane.css");
        if (viewBlanksService != null) {
            viewBlanksService.configureUIElements(
                    loaderScene,
                    blanksMiniatureTabPane,
                    blanksMiniatureSampleTab,
                    miniatureGridRowConstraints,
                    miniatureGridColumnConstraints,
                    blankMiniature,
                    blankTemplateContainer
            );
        }

    }

    public void render(Button sourceButton, Game game) {
        if (BlanksStatus.ABSENT.equals(game.getBlanksStatus())) {
            popupAlerter.invoke(sourceButton.getScene().getWindow(), "No blanks yet", "Please generate blanks before watching them.");
            return;
        } else if (BlanksStatus.OUTDATED.equals(game.getBlanksStatus())) {
            popupAlerter.invokeOkCancel(sourceButton.getScene().getWindow(),
                    "Blanks are irrelevant", "Current blanks set is not relevant any more. It's recommended to generate new set before further actions. Would you like to proceed outdated blanks watching?",
                    event -> showBlanks(sourceButton, game),
                    event -> ((Stage) ((Button) event.getSource()).getScene().getWindow()).close());
        } else {
            showBlanks(sourceButton, game);
        }

    }

    private void showBlanks(Button sourceButton, Game game) {
        viewBlanksService.setGame(game);
        viewBlanksService.fillTabPaneWithBlankMiniatures();
        setDefaultFont(findAllLabeledRecursively(blanksMiniatureTabPane).toArray(new Labeled[0]));
        setDefaultFont(saveToPng);

        final Stage popupStage = new Stage();
        popupStage.setTitle("Вот твои бланки для игры " + game.getName());
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(sourceButton.getScene().getWindow());
        popupStage.setScene(dialogScene);
        popupStage.setMinWidth(886);
        popupStage.setMinHeight(540);
        popupStage.show();
    }

    @FXML
    private void showBlankByMiniature(MouseEvent mouseEvent) {
        Button eventSourceButton = (Button) mouseEvent.getSource();
        viewBlanksService.renderBlankByMiniature(eventSourceButton);
    }

    @FXML
    private void emptyBlanksPreview() {
        viewBlanksService.emptyBlanksPreview();
    }

    @FXML
    private void saveToPng() throws PrinterException {
        viewBlanksService.saveToPng();
    }

}
