package ru.orthodox.mbbg.ui.modelExtensions.startMenuScene;

import javafx.scene.layout.AnchorPane;
import lombok.Data;
import ru.orthodox.mbbg.model.Game;

@Data
public class GridItem {
    private final AnchorPane anchorPane;
    private final Game game;
}
