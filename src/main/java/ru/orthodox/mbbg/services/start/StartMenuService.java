package ru.orthodox.mbbg.services.start;

import javafx.event.ActionEvent;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.controllers.NewGameController;
import ru.orthodox.mbbg.controllers.PlayController;
import ru.orthodox.mbbg.controllers.ViewBlanksController;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.repositories.GamesRepository;
import ru.orthodox.mbbg.model.proxy.start.GamesGridItem;
import ru.orthodox.mbbg.repositories.RoundRepository;
import ru.orthodox.mbbg.services.create.EditGameService;
import ru.orthodox.mbbg.services.create.NewGameService;
import ru.orthodox.mbbg.services.model.GameService;
import ru.orthodox.mbbg.services.play.blank.BlankService;
import ru.orthodox.mbbg.services.popup.PopupAlerter;
import ru.orthodox.mbbg.services.start.GridItemService;
import ru.orthodox.mbbg.utils.hierarchy.ElementFinder;
import ru.orthodox.mbbg.utils.screen.ScreenService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.orthodox.mbbg.utils.hierarchy.NodeDeepCopyProvider.createDeepCopy;
import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findParentAnchorPane;

@Setter
@Service
public class StartMenuService {

    private GridPane gamesField;
    private Label gameLabel;
    private AnchorPane templateGameAnchorPane;
    private AnchorPane newGameAnchorPane;

    private List<Game> existingGames;

    private int columnsNumber;
    private int rowsNumber;

    private int currentRowIndex = 0;
    private int currentColumnIndex = 0;

    @Getter
    private static List<GamesGridItem> availableGames = new ArrayList<>();

    @Autowired
    private GamesRepository gamesRepository;
    @Autowired
    private EditGameService editGameService;
    @Autowired
    private ScreenService screenService;
    @Autowired
    private GameService gameService;
    @Autowired
    private BlankService blankService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private PopupAlerter popupAlerter;

    public void fillGridWithAllGames() {
        existingGames = gamesRepository.findAllGames();
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
            Pane anchorPane = (Pane) createDeepCopy(templateGameAnchorPane);

            Button innerButton = ElementFinder.findElementByTypeAndStyleclass(anchorPane, "game-button");
            Label innerLabel = ElementFinder.findElementByTypeAndStyleclass(anchorPane, "game-name-label");
            innerLabel.scaleXProperty().bind(innerButton.scaleXProperty());
            (innerLabel.scaleYProperty()).bind(innerButton.scaleYProperty());

            availableGames.add(new GamesGridItem((AnchorPane) anchorPane, game));
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

    public void invokeGameContextMenu(Button sourceButton) {
        AnchorPane gamePane = findParentAnchorPane(sourceButton);
        Game relatedGame = GridItemService.findByEventTarget(availableGames, gamePane);
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editGame = new MenuItem();
        MenuItem deleteGame = new MenuItem();
        editGame.setText("Edit game");
        editGame.setOnAction(event ->
                editGameService.editGame(relatedGame));
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

    public void openNewGameForm() {
        NewGameController controller = applicationContext.getBean(NewGameController.class);
        controller.renderNewGameForm();
        screenService.activate("newGame");
    }

    public void openPlayGameForm(Button source) {
        AnchorPane gameGridItem = findParentAnchorPane(source);
        Game targetGame = GridItemService.findByEventTarget(StartMenuService.getAvailableGames(), gameGridItem);
        targetGame.setRounds(roundRepository.findByIds(targetGame.getRoundIds()));
        PlayController controller = applicationContext.getBean(PlayController.class);
        controller.renderNewGame(targetGame);
        screenService.activate("play");
    }

    public void generateBlanks(Button source) {
        Game targetGame = GridItemService.findByRightMenuButton(StartMenuService.getAvailableGames(), source);
        blankService.generateBlanks(targetGame);
        popupAlerter.invoke(source.getScene().getWindow(),
                "Success",
                "Blanks are generated successfully");
    }

    public void viewBlanks(Button source) {
        AnchorPane gameGridItem = findParentAnchorPane(source);
        Game targetGame = GridItemService.findByEventTarget(StartMenuService.getAvailableGames(), gameGridItem);
        ViewBlanksController controller = applicationContext.getBean(ViewBlanksController.class);
        controller.setGame(targetGame);
        controller.render(source);
    }
}
