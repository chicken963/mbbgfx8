package ru.orthodox.mbbg.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.repositories.GamesRepository;
import ru.orthodox.mbbg.repositories.RoundRepository;
import ru.orthodox.mbbg.services.play.blank.BlankService;
import ru.orthodox.mbbg.model.proxy.RGBColor;
import ru.orthodox.mbbg.services.model.GameService;
import ru.orthodox.mbbg.services.start.GridItemService;
import ru.orthodox.mbbg.services.popup.PopupAlerter;
import ru.orthodox.mbbg.services.start.StartMenuService;
import ru.orthodox.mbbg.utils.screen.ScreenService;

import javax.annotation.PostConstruct;

import static ru.orthodox.mbbg.utils.common.CustomFontDealer.setDefaultFont;
import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findParentAnchorPane;

@Slf4j
@Configurable
public class StartMenuController {

    @FXML
    public Button newGameButton;
    @FXML
    public Label greetingLabel;
    @FXML
    public GridPane gamesField;
    @FXML
    public AnchorPane newGameAnchorPane;
    @FXML
    public AnchorPane templateGameAnchorPane;
    @FXML
    public Label gameLabel;

    @Autowired
    private StartMenuService startMenuService;

    @PostConstruct
    private void setUp() {
        setDefaultFont(newGameButton, greetingLabel, gameLabel);
        configureUIElements(gamesField, gameLabel, templateGameAnchorPane, newGameAnchorPane);
        startMenuService.fillGridWithAllGames();
    }

    @FXML
    private void openNewGameForm() {
        startMenuService.openNewGameForm();
    }

    @FXML
    private void openPlayGameForm(ActionEvent event) {
        startMenuService.openPlayGameForm((Button) event.getSource());
    }

    @FXML
    private void invokeGameContextMenu(ActionEvent event) {
        startMenuService.invokeGameContextMenu((Button) event.getSource());
    }

    @FXML
    private void generateBlanks(ActionEvent event) {
        startMenuService.generateBlanks((Button) event.getSource());
    }

    @FXML
    private void viewBlanks(ActionEvent event) {
        startMenuService.viewBlanks((Button) event.getSource());
    }

    private void configureUIElements(
            GridPane gamesField,
            Label gameLabel,
            AnchorPane templateGameAnchorPane,
            AnchorPane newGameAnchorPane) {
        startMenuService.setGamesField(gamesField);
        startMenuService.setGameLabel(gameLabel);
        startMenuService.setTemplateGameAnchorPane(templateGameAnchorPane);
        startMenuService.setNewGameAnchorPane(newGameAnchorPane);
    }
}
