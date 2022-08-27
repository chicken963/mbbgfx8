package ru.orthodox.mbbg.services.start;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.model.proxy.start.GamesGridItem;

import java.util.List;

import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findParentAnchorPane;

@Service
public class GridItemService {

    public static Game findByRightMenuButton(List<GamesGridItem> availableGames, Button sourceButton) {
        AnchorPane gamePane = findParentAnchorPane(sourceButton);
        return findByEventTarget(availableGames, gamePane);
    }

    public static Game findByEventTarget(List<GamesGridItem> availableGames, AnchorPane anchorPane) {
        return availableGames
                .stream()
                .filter(gamesGridItem -> gamesGridItem.getAnchorPane().equals(anchorPane))
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Grid not found"))
                .getGame();
    }
}
