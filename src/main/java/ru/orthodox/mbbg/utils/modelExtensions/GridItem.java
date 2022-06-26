package ru.orthodox.mbbg.utils.modelExtensions;

import javafx.scene.layout.AnchorPane;
import lombok.Data;
import ru.orthodox.mbbg.model.Game;

@Data
public class GridItem {
    private final AnchorPane anchorPane;
    private final Game game;
}
