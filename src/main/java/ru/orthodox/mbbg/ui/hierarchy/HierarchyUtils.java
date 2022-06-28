package ru.orthodox.mbbg.ui.hierarchy;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HierarchyUtils {

    static List<Node> flattenStructure(Parent parent) {
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
        if (parent instanceof ScrollPane) {
            Node child = ((ScrollPane) parent).getContent();
            nodes.add(child);
            if (child instanceof Parent)
                addAllDescendents((Parent) child, nodes);

        }
    }

    public static boolean containsNode(Node childNode, Tab tab) {
        return flattenStructure((Parent) tab.getContent()).contains(childNode);
    }
}
