package ru.orthodox.mbbg.services.start;

import javafx.event.ActionEvent;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.controllers.ViewBlanksController;
import ru.orthodox.mbbg.enums.BlanksStatus;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.model.proxy.start.GamesGridItem;
import ru.orthodox.mbbg.services.create.EditGameService;
import ru.orthodox.mbbg.services.create.NewGameService;
import ru.orthodox.mbbg.services.model.GameService;
import ru.orthodox.mbbg.services.play.PlayGameService;
import ru.orthodox.mbbg.services.popup.PopupAlerter;
import ru.orthodox.mbbg.utils.hierarchy.ElementFinder;
import ru.orthodox.mbbg.utils.screen.ScreenService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findParentAnchorPane;
import static ru.orthodox.mbbg.utils.hierarchy.NodeDeepCopyProvider.createDeepCopy;

@Setter
@Service
public class StartMenuService {

    private GridPane gamesField;
    private Label gameLabel;
    private AnchorPane templateGameAnchorPane;
    private AnchorPane newGameAnchorPane;

    private List<Game> existingGames;
    private Game currentGame;

    private int columnsNumber;
    private int rowsNumber;

    private int currentRowIndex = 0;
    private int currentColumnIndex = 0;

    @Getter
    private static List<GamesGridItem> availableGames = new ArrayList<>();

    @Autowired
    private EditGameService editGameService;
    @Autowired
    private ScreenService screenService;
    @Autowired
    private GameService gameService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private PopupAlerter popupAlerter;
    @Autowired
    private PlayGameService playGameService;
    @Autowired
    private NewGameService newGameService;

    public void fillGridWithAllGames() {
        existingGames = gameService.findAllGames();
        columnsNumber = gamesField.getColumnConstraints().size();
        rowsNumber = existingGames.size() / columnsNumber;

        RowConstraints rowConstraintsTemplate = gamesField.getRowConstraints().get(0);
        gamesField.getRowConstraints().setAll(
                Stream.generate(() -> rowConstraintsTemplate)
                        .limit(rowsNumber + 1)
                        .collect(Collectors.toList()));

        fillGamesGrid();
    }

    private void fillGamesGrid() {
        gamesField.getChildren().clear();
        for (Game game : existingGames) {
            int index = existingGames.indexOf(game);
            currentRowIndex = index / columnsNumber;
            currentColumnIndex = index % columnsNumber;
            gameLabel.setText(game.getName());
            AnchorPane anchorPane = (AnchorPane) createDeepCopy(templateGameAnchorPane);

            bindImageMiniatureAndLabel(anchorPane);

            availableGames.add(new GamesGridItem(anchorPane, game));
            gamesField.add(anchorPane, currentColumnIndex, currentRowIndex);
            incrementIndexes();
        }

        if (currentColumnIndex == columnsNumber) {
            currentColumnIndex = 0;
            currentRowIndex++;
        }

        bindImageMiniatureAndLabel(newGameAnchorPane);
        gamesField.add(newGameAnchorPane, currentColumnIndex, currentRowIndex);
    }

    private void bindImageMiniatureAndLabel(AnchorPane gamesGridCell) {
        Button innerButton = ElementFinder.findElementByTypeAndStyleclass(gamesGridCell, "game-button");
        Label innerLabel = ElementFinder.findElementByTypeAndStyleclass(gamesGridCell, "game-name-label");
        innerLabel.scaleXProperty().bind(innerButton.scaleXProperty());
        (innerLabel.scaleYProperty()).bind(innerButton.scaleYProperty());
    }

    public void invokeGameContextMenu(Button sourceButton) {
        AnchorPane gamePane = findParentAnchorPane(sourceButton);
        currentGame = GridItemService.findByEventTarget(availableGames, gamePane);
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editGame = new MenuItem();
        MenuItem deleteGame = new MenuItem();
        editGame.setText("Edit game");
        editGame.setOnAction(event ->
                editGameService.editGame(currentGame));
        deleteGame.setText("Delete game");
        deleteGame.setOnAction(event -> {
            popupAlerter.invokeOkCancel(sourceButton.getScene().getWindow(),
                    "Game deletion",
                    "Are you sure you want to delete the game " + currentGame.getName() + "? Rounds and Blanks will be permanently deleted. Audiotracks will still be stored in library.",
                    this::deleteGame,
                    e -> ((Stage) ((Button) e.getSource()).getScene().getWindow()).close());
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

    public void openNewGameForm() {
        newGameService.renderGame(Optional.empty());
        screenService.activate("newGame");
    }

    public void openPlayGameForm(Button source) {
        AnchorPane gameGridItem = findParentAnchorPane(source);
        Game targetGame = GridItemService.findByEventTarget(StartMenuService.getAvailableGames(), gameGridItem);
        if (BlanksStatus.ABSENT.equals(targetGame.getBlanksStatus())) {
            popupAlerter.invoke(source.getScene().getWindow(),
                    "No blanks",
                    "No blanks are generated for this game.\nPlease generate them using 'Generate blanks' button, save them to *.png images in a view mode and provide them to participants.");
            return;
        } else if (BlanksStatus.OUTDATED.equals(targetGame.getBlanksStatus())) {
            popupAlerter.invoke(source.getScene().getWindow(),
                    "Blanks are outdated",
                    "You edited the game, so you need to re-generate blanks according to the relevant game data.");
            return;
        }

        playGameService.render(targetGame);
        screenService.activate("play");
    }

    public void generateBlanks(Button source) {
        Game targetGame = GridItemService.findByRightMenuButton(StartMenuService.getAvailableGames(), source);
        gameService.generateBlanks(targetGame);
        popupAlerter.invoke(source.getScene().getWindow(),
                "Success",
                "Blanks are generated successfully");
    }

    public void viewBlanks(Button source) {
        AnchorPane gameGridItem = findParentAnchorPane(source);
        Game targetGame = GridItemService.findByEventTarget(StartMenuService.getAvailableGames(), gameGridItem);
        ViewBlanksController controller = applicationContext.getBean(ViewBlanksController.class);
        controller.render(source, targetGame);
    }

    private void deleteGame(ActionEvent event) {
        gameService.delete(currentGame);
        fillGridWithAllGames();
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
    }
}
