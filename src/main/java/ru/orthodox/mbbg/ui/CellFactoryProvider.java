package ru.orthodox.mbbg.ui;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import ru.orthodox.mbbg.enums.ButtonType;
import ru.orthodox.mbbg.model.AudioTrack;

import java.util.function.Consumer;

public interface CellFactoryProvider {
    Callback<TableColumn<AudioTrack, String>,
            TableCell<AudioTrack, String>> provide(String imageUrl, Consumer<AudioTrack> consumer, ButtonType mode);
}
