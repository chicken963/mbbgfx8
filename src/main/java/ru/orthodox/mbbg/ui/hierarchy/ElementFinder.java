package ru.orthodox.mbbg.ui.hierarchy;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import ru.orthodox.mbbg.model.AudioTrack;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ElementFinder {

    public static<T> T findTabElementByTypeAndStyleclass(Tab tab, String styleClass) {
        return findRecursivelyByStyle((Parent) tab.getContent(), styleClass)
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

    public static List<Node> findRecursivelyByIdInTemplate(Parent parent, String id) {
        return HierarchyUtils.flattenStructure(parent).stream()
                .filter(node -> id.equals(node.getId()))
                .collect(Collectors.toList());
    }

    public static List<Node> findRecursivelyByStyle(Parent parent, String style) {
        return HierarchyUtils.flattenStructure(parent).stream()
                .filter(node -> style.equals(node.getStyle()))
                .collect(Collectors.toList());
    }

    public static Set<Node> findRecursivelyByStyleClass(Parent parent, String styleClass) {
        return HierarchyUtils.flattenStructure(parent).stream()
                .filter(node -> node.getStyleClass().contains(styleClass))
                .collect(Collectors.toSet());
    }

    public static List<Label> findAllLabelsRecursively(Parent parent) {
        return HierarchyUtils.flattenStructure(parent).stream()
                .filter(node -> node instanceof Label)
                .map(node -> (Label) node)
                .collect(Collectors.toList());
    }

    public static List<Labeled> findAllLabeledRecursively(TabPane tabPane) {
        return tabPane.getTabs().stream()
                .map(Tab::getContent)
                .map(node -> (Parent) node)
                .map(HierarchyUtils::flattenStructure)
                .flatMap(Collection::stream)
                .filter(node -> node instanceof Labeled)
                .map(node -> (Labeled) node)
                .collect(Collectors.toList());
    }

    public static AnchorPane findParentAnchorPane(Node sourceNode) {
        Parent p = sourceNode.getParent();
        while (!(p instanceof AnchorPane)) {
            p = p.getParent();
        }
        return (AnchorPane) p;
    }

}
