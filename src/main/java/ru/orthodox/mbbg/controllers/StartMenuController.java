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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import ru.orthodox.mbbg.enums.OpenSceneMode;
import ru.orthodox.mbbg.model.Game;
import ru.orthodox.mbbg.services.ScreenService;
import ru.orthodox.mbbg.services.model.GamesService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static ru.orthodox.mbbg.utils.ui.CustomFontDealer.setDefaultFont;
import static ru.orthodox.mbbg.utils.ui.NodeDeepCopyProvider.createDeepCopy;

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
    private GamesService gamesService;

    @Autowired
    private ApplicationContext applicationContext;

    private int columnsNumber;
    private int rowsNumber;

    private int currentRowIndex = 0;
    private int currentColumnIndex = 0;

    private static List<GridItem> availableGames = new ArrayList<>();
    private List<Game> existingGames = new ArrayList<>();

    @PostConstruct
    private void setUp() {
        setDefaultFont(openGameButton, newGameButton, greetingLabel, gameLabel);
        fillGridWithAllGames();
    }

    public void openNewGameForm(ActionEvent actionEvent) {
        screenService.activate("newgame");
    }

    public void openPlayGameForm(ActionEvent actionEvent) {
        Button node = (Button) actionEvent.getSource();
        Game targetGame = GridItemService.findByEventTarget((AnchorPane) node.getParent().getParent());
        PlayController controller = applicationContext.getBean(PlayController.class);
        controller.setGame(targetGame);
        controller.render();
        screenService.activate("main");
    }

    private void incrementIndexes() {
        if (currentColumnIndex == columnsNumber) {
            currentRowIndex++;
            currentColumnIndex = 0;
        } else {
            currentColumnIndex++;
        }
    }

    @FXML
    private void whiteTextColor(MouseEvent mouseEvent) {
        gameLabel.setTextFill(new Color(215.0/255, 235.0/255, 235.0/255, 0.8));
    }

    @FXML
    private void defaultTextColor(MouseEvent mouseEvent) {
        gameLabel.setTextFill(new Color(1, 165.0/255.0, 0, 1));
    }

    @Data
    private static class GridItem {
        private final AnchorPane anchorPane;
        private final Game game;
    }

    private static class GridItemService {
        static Game findByEventTarget(AnchorPane anchorPane) {
            return availableGames
                    .stream()
                    .filter(gridItem -> gridItem.getAnchorPane().equals(anchorPane))
                    .findFirst()
                    .orElseThrow(() -> new NullPointerException("Grid not found"))
                    .getGame();
        }
    }

    public void fillGridWithAllGames() {
        existingGames = gamesService.findAllGames();
        columnsNumber = gamesField.getColumnConstraints().size();
        rowsNumber = gamesField.getRowConstraints().size();
        int pageSize = rowsNumber * columnsNumber;
        if (existingGames.size() >= pageSize) {
            Pagination pagination = createDeepCopy(templatePagination);
            pagination.setPageCount(existingGames.size() / pageSize + 1);
            pagination.setPageFactory(pageIndex -> {
                fillGamesFieldPage(pageIndex);
                return gamesField;
            });
            ((Pane) greetingLabel.getParent()).getChildren().add(pagination);
        }
        fillGamesFieldPage(0);
    }

    private void fillGamesFieldPage(int pageNumber){
        gamesField.getChildren().clear();
        int pageSize = rowsNumber * columnsNumber;
        List<Game> sublistToRender = existingGames.subList(pageSize * pageNumber, Math.min(pageSize * (pageNumber + 1), existingGames.size()));
        for (Game game : sublistToRender) {
            int index = sublistToRender.indexOf(game);
            currentRowIndex = index / columnsNumber;
            currentColumnIndex = index % columnsNumber;
            gameLabel.setText(game.getName());
            Pane anchorPane = createDeepCopy(templateGameAnchorPane);
            availableGames.add(new GridItem((AnchorPane) anchorPane, game));
            gamesField.add(anchorPane, currentColumnIndex, currentRowIndex);
        }
        incrementIndexes();
        if (currentRowIndex != rowsNumber || currentColumnIndex != columnsNumber) {
            gamesField.add(newGameAnchorPane, currentColumnIndex, currentRowIndex);
        }
    }
}
