package ru.orthodox.mbbg.ui.modelExtensions.startMenuScene;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.Game;

import java.util.List;

import static ru.orthodox.mbbg.ui.hierarchy.ElementFinder.findParentAnchorPane;

@Service
public class GridItemService {

    public static Game findByRightMenuButton(List<GridItem> availableGames, ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        AnchorPane gamePane = findParentAnchorPane(sourceButton);
        return findByEventTarget(availableGames, gamePane);
    }

    public static Game findByEventTarget(List<GridItem> availableGames, AnchorPane anchorPane) {
        return availableGames
                .stream()
                .filter(gridItem -> gridItem.getAnchorPane().equals(anchorPane))
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Grid not found"))
                .getGame();
    }
}
