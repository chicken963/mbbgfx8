package ru.orthodox.mbbg.utils.ui.newGameScene;

import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ru.orthodox.mbbg.model.AudioTrack;

import static ru.orthodox.mbbg.utils.ui.HierarchyUtils.findRecursivelyByIdInTemplate;
import static ru.orthodox.mbbg.utils.ui.HierarchyUtils.findRecursivelyByStyleClass;

public class ElementFinder {

    public static<T> T findTabElementByTypeAndStyleclass(Tab tab, String styleClass) {
        return findRecursivelyByStyleClass((Parent) tab.getContent(), styleClass)
                .stream()
                .map(node -> (T) node)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Failed to find element with style class '%s'", styleClass)));
    }

    public static<T> T findElementById(Parent parent, String id) {
        return findRecursivelyByIdInTemplate(parent, id)
                .stream()
                .map(node -> (T) node)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Failed to find element with id '%s'", id)));
    }

    public static TableColumn<AudioTrack, String> findTracksTableColumnByNumber(TableView<AudioTrack> table, int index) {
        return table.getColumns()
                .stream()
                .map(column -> (TableColumn<AudioTrack, String>) column)
                .skip(index)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Column at index %d was not found", index)));
    }

}
