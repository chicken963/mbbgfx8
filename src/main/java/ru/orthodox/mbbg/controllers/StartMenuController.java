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
    private BlankService blankService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private PopupAlerter popupAlerter;
    @Autowired
    private GamesRepository gamesRepository;
    @Autowired
    private RoundRepository roundRepository;

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

    public void openNewGameForm() {
        NewGameController controller = applicationContext.getBean(NewGameController.class);
        controller.renderNewGameForm();
        screenService.activate("newGame");
    }

    public void openPlayGameForm(ActionEvent actionEvent) {
        Button source = (Button) actionEvent.getSource();
        AnchorPane gameGridItem = findParentAnchorPane(source);
        Game targetGame = GridItemService.findByEventTarget(StartMenuService.getAvailableGames(), gameGridItem);
        targetGame.setRounds(roundRepository.findByIds(targetGame.getRoundIds()));
        PlayController controller = applicationContext.getBean(PlayController.class);
        controller.renderNewGame(targetGame);
        screenService.activate("play");
    }

    @FXML
    private void whiteTextColor(MouseEvent mouseEvent) {
        gameLabel.setTextFill(RGBColor.of(215, 235, 235, 0.8));
    }

    @FXML
    private void defaultTextColor(MouseEvent mouseEvent) {
        gameLabel.setTextFill(RGBColor.of(255, 165, 0));
    }

    @FXML
    private void invokeGameContextMenu(ActionEvent source) {
        startMenuService.invokeGameContextMenu(source);
    }

    @FXML
    private void generateBlanks(ActionEvent event) {
        Game targetGame = GridItemService.findByRightMenuButton(StartMenuService.getAvailableGames(), event);
        blankService.generateBlanks(targetGame);
        popupAlerter.invoke(((Node) event.getSource()).getScene().getWindow(),
                "Success",
                "Blanks are generated successfully");
    }

    public void fillGridWithAllGames() {
        startMenuService.fillGridWithAllGames();
    }

    @FXML
    private void viewBlanks(ActionEvent event) {
        Button source = (Button) event.getSource();
        AnchorPane gameGridItem = findParentAnchorPane(source);
        Game targetGame = GridItemService.findByEventTarget(StartMenuService.getAvailableGames(), gameGridItem);
        ViewBlanksController controller = applicationContext.getBean(ViewBlanksController.class);
        controller.setGame(targetGame);
        controller.render(event);
    }
}
