package ru.orthodox.mbbg.utils.ui;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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

    public static List<Label> findAllLabelsRecursively(Parent parent) {
        return flattenStructure(parent).stream()
                .filter(node -> node instanceof Label)
                .map(node -> (Label) node)
                .collect(Collectors.toList());
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

    public static AnchorPane findParentAnchorPane(Node sourceNode) {
        Parent p = sourceNode.getParent();
        while (!(p instanceof AnchorPane)) {
            p = p.getParent();
        }
        ;
        return (AnchorPane) p;
    }

}
