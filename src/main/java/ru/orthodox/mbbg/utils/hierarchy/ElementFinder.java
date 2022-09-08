package ru.orthodox.mbbg.utils.hierarchy;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ElementFinder {

    public static<T> T findElementByTypeAndStyleclass(Parent parent, String styleClass) {
        return findRecursivelyByStyleClass(parent, styleClass)
                .stream()
                .map(node -> (T) node)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Failed to find element with style class '%s'", styleClass)));
    }

    public static<T> List<T> findElementsByTypeAndStyleclass(Parent parent, String styleClass) {
        return findRecursivelyByStyleClass(parent, styleClass)
                .stream()
                .map(node -> (T) node)
                .collect(Collectors.toList());
    }

    public static<T> T findTabElementByTypeAndStyleclass(Tab tab, String styleClass) {
        return findRecursivelyByStyleClass((Parent) tab.getContent(), styleClass)
                .stream()
                .map(node -> (T) node)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Failed to find element with style class '%s'", styleClass)));
    }

    private static Set<Node> findRecursivelyByStyleClass(Parent parent, String styleClass) {
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
