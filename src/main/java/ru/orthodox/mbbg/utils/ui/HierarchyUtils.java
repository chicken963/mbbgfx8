package ru.orthodox.mbbg.utils.ui;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HierarchyUtils {

    public static List<Node> findRecursivelyByIdInTemplate(Parent parent, String id) {
        return flattenStructure(parent).stream()
                .filter(node -> id.equals(node.getId()))
                .collect(Collectors.toList());
    }

    public static List<Node> findRecursivelyByStyleClass(Parent parent, String style) {
        return flattenStructure(parent).stream()
                .filter(node -> style.equals(node.getStyle()))
                .collect(Collectors.toList());
    }

    public static Tab findParentTab(Node startNode, TabPane tabPane) {
        return tabPane.getTabs().stream()
                .filter(tab -> containsNode(startNode, tab))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Parent tab for table not found"));
    }

    private static List<Node> flattenStructure(Parent parent) {
        List<Node> nodes = new ArrayList<>();
        addAllDescendents(parent, nodes);
        return nodes;
    }

    private static void addAllDescendents(Parent parent, List<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                addAllDescendents((Parent) node, nodes);
        }
        if (parent instanceof SplitPane) {
            for (Node node : ((SplitPane) parent).getItems()) {
                nodes.add(node);
                if (node instanceof Parent)
                    addAllDescendents((Parent) node, nodes);
            }
        }
    }

    private static boolean containsNode (Node childNode, Tab tab) {
        return flattenStructure((Parent) tab.getContent()).contains(childNode);
    }

}
