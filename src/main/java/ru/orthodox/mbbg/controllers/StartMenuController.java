package ru.orthodox.mbbg.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import ru.orthodox.mbbg.model.Game;
import ru.orthodox.mbbg.repositories.GamesRepository;
import ru.orthodox.mbbg.services.CardService;
import ru.orthodox.mbbg.services.GameService;
import ru.orthodox.mbbg.services.ScreenService;
import ru.orthodox.mbbg.services.StartMenuService;
import ru.orthodox.mbbg.utils.modelExtensions.GridItemService;
import ru.orthodox.mbbg.utils.ui.PopupAlerter;
import ru.orthodox.mbbg.utils.ui.startMenuScene.HoverDealer;

import javax.annotation.PostConstruct;

import static ru.orthodox.mbbg.utils.ui.CustomFontDealer.setDefaultFont;
import static ru.orthodox.mbbg.utils.ui.HierarchyUtils.findParentAnchorPane;

@Slf4j
@Configurable
public class StartMenuController {

    @FXML
    public Button openGameButton;
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
    @FXML
    public Pagination templatePagination;

    @Autowired
    private ScreenService screenService;
    @Autowired
    private GameService gameService;
    @Autowired
    private CardService cardService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private HoverDealer hoverDealer;
    @Autowired
    private PopupAlerter popupAlerter;
    @Autowired
    private GamesRepository gamesRepository;

    private StartMenuService startMenuService;

    @PostConstruct
    private void setUp() {
        setDefaultFont(openGameButton, newGameButton, greetingLabel, gameLabel);
        startMenuService = StartMenuService.builder()
                .gamesField(gamesField)
                .templatePagination(templatePagination)
                .greetingLabel(greetingLabel)
                .gameLabel(gameLabel)
                .templateGameAnchorPane(templateGameAnchorPane)
                .gamesRepository(gamesRepository)
                .newGameAnchorPane(newGameAnchorPane)
                .build();
        startMenuService.fillGridWithAllGames();
    }

    public void openNewGameForm(ActionEvent actionEvent) {
        NewGameController controller = applicationContext.getBean(NewGameController.class);
        controller.render();
        screenService.activate("newgame");
    }

    public void openPlayGameForm(ActionEvent actionEvent) {
        Button source = (Button) actionEvent.getSource();
        AnchorPane gameGridItem = findParentAnchorPane(source);
        Game targetGame = GridItemService.findByEventTarget(StartMenuService.getAvailableGames(), gameGridItem);
        PlayController controller = applicationContext.getBean(PlayController.class);
        controller.setGame(targetGame);
        controller.render();
        screenService.activate("main");
    }

    @FXML
    private void whiteTextColor(MouseEvent mouseEvent) {
        gameLabel.setTextFill(new Color(215.0/255, 235.0/255, 235.0/255, 0.8));
    }

    @FXML
    private void defaultTextColor(MouseEvent mouseEvent) {
        gameLabel.setTextFill(new Color(1, 165.0/255.0, 0, 1));
    }

    @FXML
    private void invokeGameContextMenu(ActionEvent source) {
        startMenuService.invokeGameContextMenu(source);
    }

    public void highlightButton(MouseEvent mouseEvent) {
        Button targetButton = (Button) mouseEvent.getSource();
        hoverDealer.applyStylesOnMouseEnter(targetButton);
    }

    public void defaultStyleButton(MouseEvent mouseEvent) {
        Button targetButton = (Button) mouseEvent.getSource();
        hoverDealer.applyStylesOnMouseLeave(targetButton);
    }

    @FXML
    private void generateTickets(ActionEvent event) {
        Game targetGame = GridItemService.findByRightMenuButton(StartMenuService.getAvailableGames(), event);
        cardService.generateTickets(targetGame);
        popupAlerter.invoke(((Node) event.getSource()).getScene().getWindow(),
                "Success",
                "Tickets are generated successfully");
    }

    public void fillGridWithAllGames() {
        startMenuService.fillGridWithAllGames();
    }

    @FXML
    private void viewTickets(ActionEvent event) {
        Button source = (Button) event.getSource();
        AnchorPane gameGridItem = findParentAnchorPane(source);
        Game targetGame = GridItemService.findByEventTarget(StartMenuService.getAvailableGames(), gameGridItem);
        ViewTicketsController controller = applicationContext.getBean(ViewTicketsController.class);
        controller.setGame(targetGame);
        controller.render(event);
    }
}
