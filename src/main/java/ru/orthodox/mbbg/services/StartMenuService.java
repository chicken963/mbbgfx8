package ru.orthodox.mbbg.services;

import javafx.event.ActionEvent;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.controllers.StartMenuController;
import ru.orthodox.mbbg.model.Game;
import ru.orthodox.mbbg.repositories.GamesRepository;
import ru.orthodox.mbbg.utils.modelExtensions.GridItem;
import ru.orthodox.mbbg.utils.modelExtensions.GridItemService;

import java.util.ArrayList;
import java.util.List;

import static ru.orthodox.mbbg.utils.ui.CustomFontDealer.setDefaultFont;
import static ru.orthodox.mbbg.utils.ui.HierarchyUtils.findParentAnchorPane;
import static ru.orthodox.mbbg.utils.ui.NodeDeepCopyProvider.createDeepCopy;

@Builder
public class StartMenuService {

    private GridPane gamesField;
    private Pagination templatePagination;
    private Label greetingLabel;
    private Label gameLabel;
    private AnchorPane templateGameAnchorPane;
    private AnchorPane newGameAnchorPane;

    private List<Game> existingGames;

    private int columnsNumber;
    private int rowsNumber;

    private int currentRowIndex = 0;
    private int currentColumnIndex = 0;

    @Getter
    private static List<GridItem> availableGames = new ArrayList<>();

    private GamesRepository gamesRepository;

    public void fillGridWithAllGames() {
        existingGames = gamesRepository.findAllGames();
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

    private void fillGamesFieldPage(int pageNumber) {
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
            incrementIndexes();
        }
        if (currentRowIndex != rowsNumber || currentColumnIndex != columnsNumber) {
            if (currentColumnIndex == columnsNumber) {
                currentColumnIndex = 0;
                currentRowIndex++;
            }
            gamesField.add(newGameAnchorPane, currentColumnIndex, currentRowIndex);

        }
    }

    public void invokeGameContextMenu(ActionEvent source) {
        Button sourceButton = (Button) source.getSource();
        AnchorPane gamePane = findParentAnchorPane(sourceButton);
        Game relatedGame = GridItemService.findByEventTarget(availableGames, gamePane);
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editGame = new MenuItem();
        MenuItem deleteGame = new MenuItem();
        editGame.setText("Edit game");
        deleteGame.setText("Delete game");
        deleteGame.setOnAction(event -> {
            gamesRepository.deleteGame(relatedGame);
            fillGridWithAllGames();
        });
        contextMenu.getItems().add(editGame);
        contextMenu.getItems().add(deleteGame);
        sourceButton.setContextMenu(contextMenu);
        contextMenu.show(sourceButton, Side.BOTTOM, 40, -10);
    }

    private void incrementIndexes() {
        if (currentColumnIndex == columnsNumber) {
            currentRowIndex++;
            currentColumnIndex = 0;
        } else {
            currentColumnIndex++;
        }
    }
}
