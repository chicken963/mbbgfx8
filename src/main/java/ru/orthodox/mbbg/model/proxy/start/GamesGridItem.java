package ru.orthodox.mbbg.model.proxy.start;

import javafx.scene.layout.AnchorPane;
import lombok.Data;
import ru.orthodox.mbbg.model.basic.Game;

@Data
public class GamesGridItem {
    private final AnchorPane anchorPane;
    private final Game game;
}
