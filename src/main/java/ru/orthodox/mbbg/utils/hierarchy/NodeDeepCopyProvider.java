package ru.orthodox.mbbg.utils.hierarchy;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.DisplacementMap;
import javafx.scene.effect.FloatMap;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.controlsfx.control.RangeSlider;
import ru.orthodox.mbbg.utils.common.CustomFontDealer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class NodeDeepCopyProvider {

    public static Parent createDeepCopy(Parent node) {
        Parent nodeCopy;
        List<Node> children = new ArrayList<>();

        if (node instanceof Pane) {
            nodeCopy = createDeepCopy((Pane) node);
            children = ((Pane) node).getChildren();
        } else if (node instanceof Control) {
            nodeCopy = createDeepCopy((Control) node);
        } else {
            throw new ClassCastException(String.format("Class %s was met among the familiar", node.getClass()));
        }

        if (node.getParent() != null && node.getParent() instanceof GridPane) {
            GridPane.setColumnIndex(nodeCopy, GridPane.getColumnIndex(node));
            GridPane.setRowIndex(nodeCopy, GridPane.getRowIndex(node));
        }

        for (Node child : children) {
            Parent childCopy = createDeepCopy((Parent) child);
            ((Pane) nodeCopy).getChildren().add(childCopy);
        }
        return nodeCopy;
    }

    private static Pane createDeepCopy(Pane region) {
        Pane copy;
        if (region instanceof HBox) {
            copy = createDeepCopy((HBox) region);
        } else if (region instanceof VBox) {
            copy = createDeepCopy((VBox) region);
        } else if (region instanceof AnchorPane) {
            copy = createDeepCopy((AnchorPane) region);
        } else if (region instanceof GridPane) {
            copy = createDeepCopy((GridPane) region);
        } else {
            throw new ClassCastException(String.format("Class %s is unable to be processed as a Pane",
                    region.getClass()));
        }
        return copy;
    }

    private static Control createDeepCopy(Control control) {
        Control copy;
        if (control instanceof Label) {
            copy = createDeepCopy((Label) control);
        } else if (control instanceof Button) {
            copy = createDeepCopy((Button) control);
        } else if (control instanceof CheckBox) {
            copy = createDeepCopy((CheckBox) control);
        } else if (control instanceof SplitPane) {
            copy = createDeepCopy((SplitPane) control);
        } else if (control instanceof ChoiceBox) {
            copy = createDeepCopy((ChoiceBox) control);
        } else if (control instanceof TextField) {
            copy = createDeepCopy((TextField) control);
        } else if (control instanceof TableView) {
            copy = createDeepCopy((TableView) control);
        }  else if (control instanceof ScrollPane) {
            copy = createDeepCopy((ScrollPane) control);
        }  else if (control instanceof RangeSlider) {
            copy = createDeepCopy((RangeSlider) control);
        } else {
            throw new ClassCastException(String.format("Class %s is unable to be processed as a Control",
                    control.getClass()));
        }
        return copy;
    }

    private static Pagination createDeepCopy(Pagination pagination) {
        Pagination copy = new Pagination();
        copy.setCursor(pagination.getCursor());
        copy.setLayoutX(pagination.getLayoutX());
        copy.setLayoutY(pagination.getLayoutY());
        copy.setPrefHeight(pagination.getPrefHeight());
        copy.setPrefWidth(pagination.getPrefWidth());

        DisplacementMap displacementMap = new DisplacementMap();
        displacementMap.setMapData(new FloatMap());

        copy.setEffect(displacementMap);
        return copy;
    }

    private static Button createDeepCopy(Button sourceButton) {
        Button copy = new Button();

        HBox.setMargin(copy, createDeepCopy(HBox.getMargin(sourceButton)));
        copy.setOnAction(sourceButton.getOnAction());
        copy.setOnMouseClicked(sourceButton.getOnMouseClicked());
        copy.setOnMouseEntered(sourceButton.getOnMouseEntered());
        copy.setOnMouseExited(sourceButton.getOnMouseExited());
        copy.setGraphic(copyImageView((ImageView) sourceButton.getGraphic()));
        copy.setMnemonicParsing(sourceButton.isMnemonicParsing());

        alignCommonRegionProperties(sourceButton, copy);
        alignCommonLabeledProperties(sourceButton, copy);
        alignTextProperties(sourceButton, copy);
        alignWidthAndHeight(sourceButton, copy);

        return copy;
    }

    private static CheckBox createDeepCopy(CheckBox sourceCheckbox) {
        CheckBox copy = new CheckBox();
        copy.setMnemonicParsing(sourceCheckbox.isMnemonicParsing());
        copy.getStyleClass().setAll(sourceCheckbox.getStyleClass());
        alignCommonRegionProperties(sourceCheckbox, copy);
        alignCommonLabeledProperties(sourceCheckbox, copy);
        alignTextProperties(sourceCheckbox, copy);
        alignWidthAndHeight(sourceCheckbox, copy);
        VBox.setMargin(copy, createDeepCopy(VBox.getMargin(sourceCheckbox)));
        HBox.setMargin(copy, createDeepCopy(HBox.getMargin(sourceCheckbox)));

        return copy;
    }

    private static Label createDeepCopy(Label sourceLabel) {
        Label copy = new Label();

        HBox.setMargin(copy, createDeepCopy(HBox.getMargin(sourceLabel)));
        copy.setPadding(createDeepCopy(sourceLabel.getPadding()));
        alignCommonRegionProperties(sourceLabel, copy);
        alignCommonLabeledProperties(sourceLabel, copy);
        alignTextProperties(sourceLabel, copy);
        alignWidthAndHeight(sourceLabel, copy);
        if (sourceLabel.getGraphic() != null) {
            copy.setGraphic(createDeepCopy((CheckBox) sourceLabel.getGraphic()));
        }

        return copy;
    }

    private static Insets createDeepCopy(Insets sourceInset) {
        if (sourceInset == null) {
            return null;
        }
        return new Insets(
                sourceInset.getTop(),
                sourceInset.getRight(),
                sourceInset.getBottom(),
                sourceInset.getLeft()
        );
    }

    private static HBox createDeepCopy(HBox sourceHbox) {
        HBox copy = new HBox();
        HBox.setHgrow(copy, HBox.getHgrow(sourceHbox));
        VBox.setMargin(copy, createDeepCopy(VBox.getMargin(sourceHbox)));
        HBox.setMargin(copy, createDeepCopy(HBox.getMargin(sourceHbox)));
        copy.setAlignment(sourceHbox.getAlignment());
        copy.setPadding(createDeepCopy(sourceHbox.getPadding()));
        alignWidthAndHeight(sourceHbox, copy);
        alignCommonRegionProperties(sourceHbox, copy);
        alignAnchorInsets(sourceHbox, copy);

        return copy;
    }

    private static VBox createDeepCopy(VBox sourceVbox) {
        VBox copy = new VBox();
        VBox.setMargin(copy, createDeepCopy(VBox.getMargin(sourceVbox)));
        HBox.setMargin(copy, createDeepCopy(HBox.getMargin(sourceVbox)));
        VBox.setVgrow(copy, HBox.getHgrow(sourceVbox));
        copy.setAlignment(sourceVbox.getAlignment());
        copy.setPadding(createDeepCopy(sourceVbox.getPadding()));

        alignWidthAndHeight(sourceVbox, copy);
        alignCommonRegionProperties(sourceVbox, copy);
        alignAnchorInsets(sourceVbox, copy);

        return copy;
    }

    private static AnchorPane createDeepCopy(AnchorPane sourcePane) {
        AnchorPane copy = new AnchorPane();

        alignWidthAndHeight(sourcePane, copy);
        alignCommonRegionProperties(sourcePane, copy);
        alignAnchorInsets(sourcePane, copy);

        GridPane.setColumnIndex(copy, GridPane.getColumnIndex(sourcePane));
        GridPane.setRowIndex(copy, GridPane.getRowIndex(sourcePane));

        GridPane.setHalignment(copy, GridPane.getHalignment(sourcePane));
        GridPane.setValignment(copy, GridPane.getValignment(sourcePane));

        copy.setEffect(sourcePane.getEffect());
        copy.setPadding(createDeepCopy(sourcePane.getPadding()));
        return copy;
    }

    private static GridPane createDeepCopy(GridPane sourcePane) {
        GridPane copy = new GridPane();

        alignWidthAndHeight(sourcePane, copy);
        alignCommonRegionProperties(sourcePane, copy);
        copy.getStyleClass().setAll(sourcePane.getStyleClass());
        copy.getRowConstraints().setAll(sourcePane.getRowConstraints().stream()
                .map(NodeDeepCopyProvider::createDeepCopy)
                .collect(Collectors.toList()));
        copy.getColumnConstraints().setAll(sourcePane.getColumnConstraints().stream()
                .map(NodeDeepCopyProvider::createDeepCopy)
                .collect(Collectors.toList()));
        AnchorPane.setBottomAnchor(copy, AnchorPane.getBottomAnchor(sourcePane));
        AnchorPane.setTopAnchor(copy, AnchorPane.getTopAnchor(sourcePane));
        AnchorPane.setLeftAnchor(copy, AnchorPane.getLeftAnchor(sourcePane));
        AnchorPane.setRightAnchor(copy, AnchorPane.getRightAnchor(sourcePane));

        return copy;
    }

    private static RangeSlider createDeepCopy(RangeSlider origSlider) {
        RangeSlider copy = new RangeSlider();
        copy.setHighValue(origSlider.getHighValue());
        copy.setLowValue(origSlider.getLowValue());
        alignWidthAndHeight(origSlider, copy);
        copy.setShowTickLabels(origSlider.isShowTickLabels());
        copy.setShowTickMarks(origSlider.isShowTickMarks());
        copy.setMajorTickUnit(origSlider.getMajorTickUnit());
        copy.setBlockIncrement(origSlider.getBlockIncrement());
        copy.setDisable(origSlider.isDisable());
        return copy;
    }

    private static ScrollPane createDeepCopy(ScrollPane sourcePane) {
        ScrollPane copy = new ScrollPane();

        alignWidthAndHeight(sourcePane, copy);
        alignCommonRegionProperties(sourcePane, copy);
        alignAnchorInsets(sourcePane, copy);

        copy.setHbarPolicy(sourcePane.getHbarPolicy());
        copy.setVbarPolicy(sourcePane.getVbarPolicy());

        Parent scrollPaneContent = (Parent) sourcePane.getContent();
        copy.setContent(createDeepCopy(scrollPaneContent));

        return copy;
    }

    public static RowConstraints createDeepCopy(RowConstraints sourceRowConstraints) {
        RowConstraints copy = new RowConstraints();

        copy.setMaxHeight(sourceRowConstraints.getMaxHeight());
        copy.setMinHeight(sourceRowConstraints.getMinHeight());
        copy.setPrefHeight(sourceRowConstraints.getPrefHeight());
        copy.setValignment(sourceRowConstraints.getValignment());
        copy.setVgrow(sourceRowConstraints.getVgrow());

        return copy;
    }

    public static ColumnConstraints createDeepCopy(ColumnConstraints sourceRowConstraints) {
        ColumnConstraints copy = new ColumnConstraints();

        copy.setMaxWidth(sourceRowConstraints.getMaxWidth());
        copy.setMinWidth(sourceRowConstraints.getMinWidth());
        copy.setPrefWidth(sourceRowConstraints.getPrefWidth());
        copy.setHalignment(sourceRowConstraints.getHalignment());
        copy.setHgrow(sourceRowConstraints.getHgrow());

        return copy;
    }

    private static SplitPane createDeepCopy(SplitPane sourcePane) {
        SplitPane copy = new SplitPane();

        copy.setDividerPositions(sourcePane.getDividerPositions());
        copy.setPrefHeight(sourcePane.getPrefHeight());
        copy.setPrefWidth(sourcePane.getPrefWidth());
        copy.setStyle(sourcePane.getStyle());

        Parent leftPane = (Parent) sourcePane.getItems().get(0);
        Parent rightPane = (Parent) sourcePane.getItems().get(1);

        copy.getItems().addAll(createDeepCopy(leftPane), createDeepCopy(rightPane));

        return copy;
    }

    public static Tab createDeepCopy(Tab sourceTab) {
        Tab copy = new Tab();
        copy.setContent(createDeepCopy((Parent) sourceTab.getContent()));
        return copy;
    }

    private static TableView createDeepCopy(TableView sourceTable) {
        TableView copy = new TableView();
        copy.setEditable(sourceTable.isEditable());
        copy.setStyle(sourceTable.getStyle());
        alignWidthAndHeight(sourceTable, copy);
        alignAnchorInsets(sourceTable, copy);
        Collection<TableColumn> copiedColumns = new ArrayList<>();
        sourceTable.getColumns()
                .forEach(column -> copiedColumns.add(createDeepCopy((TableColumn) column)));
        copy.getColumns().addAll(copiedColumns);
        copy.setPlaceholder(createDeepCopy((Label) sourceTable.getPlaceholder()));
        return copy;
    }

    private static TableColumn createDeepCopy(TableColumn tableColumn) {
        TableColumn copy = new TableColumn();
        copy.setText(tableColumn.getText());
        copy.setPrefWidth(tableColumn.getPrefWidth());
        copy.setStyle(tableColumn.getStyle());
        return copy;
    }

    private static Tooltip copyToolTip(Tooltip tooltip) {
        if (tooltip == null) return null;
        Tooltip copy = new Tooltip();
        copy.setText(tooltip.getText());
        CustomFontDealer.setDefaultFont(copy);
        return copy;
    }

    private static TextField createDeepCopy(TextField textField) {
        TextField copy = new TextField();
        copy.setAlignment(textField.getAlignment());
        copy.setStyle(textField.getStyle());
        copy.getStyleClass().setAll(textField.getStyleClass());
        copy.setPrefHeight(textField.getPrefHeight());
        copy.setPrefWidth(textField.getPrefWidth());
        copy.setEditable(textField.isEditable());
        copy.setDisable(textField.isDisable());
        return copy;
    }

    private static ChoiceBox createDeepCopy(ChoiceBox choiceBox) {
        ChoiceBox copy = new ChoiceBox();
        copy.setPrefHeight(choiceBox.getPrefHeight());
        copy.setPrefWidth(choiceBox.getPrefWidth());
        copy.setStyle(choiceBox.getStyle());
        copy.getStyleClass().setAll(choiceBox.getStyleClass());
        copy.setOnAction(choiceBox.getOnAction());
        return copy;
    }

    private static ImageView copyImageView(ImageView imageView) {
        if (imageView == null) return null;

        ImageView copy = new ImageView();
        copy.setFitHeight(imageView.getFitHeight());
        copy.setFitWidth(imageView.getFitWidth());
        copy.setPickOnBounds(imageView.isPickOnBounds());
        copy.setPreserveRatio(imageView.isPreserveRatio());
        if (imageView.getImage().impl_getUrl() != null) {
            copy.setImage(new Image(imageView.getImage().impl_getUrl()));
        }
        return copy;
    }

    private static Font createDeepCopy(Font sourceFont) {
        return new Font(sourceFont.getName(), sourceFont.getSize());
    }

    private static void alignWidthAndHeight(Region orig, Region copy) {
        copy.setPrefWidth(orig.getPrefWidth());
        copy.setPrefHeight(orig.getPrefHeight());
        copy.setMinWidth(orig.getMinWidth());
        copy.setMinHeight(orig.getMinHeight());
        copy.setMaxWidth(orig.getMaxWidth());
        copy.setMaxHeight(orig.getMaxHeight());

        copy.setLayoutX(orig.getLayoutX());
        copy.setLayoutY(orig.getLayoutY());
    }

    private static void alignCommonRegionProperties(Region orig, Region copy) {
        copy.setVisible(orig.isVisible());
        copy.setOpaqueInsets(orig.getOpaqueInsets());
        copy.setStyle(orig.getStyle());
        copy.getStyleClass().setAll(orig.getStyleClass());
        copy.setOnMouseEntered(orig.getOnMouseEntered());
        copy.setOnMouseExited(orig.getOnMouseExited());
    }

    private static void alignTextProperties(Labeled orig, Labeled copy) {
        copy.setText(orig.getText());
        copy.setTextFill(orig.getTextFill());
        copy.setFont(createDeepCopy(orig.getFont()));
    }

    private static void alignCommonLabeledProperties(Labeled orig, Labeled copy) {
        copy.setWrapText(orig.isWrapText());
        copy.setAlignment(orig.getAlignment());
        copy.setContentDisplay(orig.getContentDisplay());
        copy.setTooltip(copyToolTip(orig.getTooltip()));

        HBox.setMargin(copy, createDeepCopy(HBox.getMargin(orig)));
        VBox.setMargin(copy, VBox.getMargin(orig));
    }

    private static void alignAnchorInsets(Region orig, Region copy) {
        AnchorPane.setLeftAnchor(copy, AnchorPane.getLeftAnchor(orig));
        AnchorPane.setRightAnchor(copy, AnchorPane.getRightAnchor(orig));
        AnchorPane.setTopAnchor(copy, AnchorPane.getTopAnchor(orig));
        AnchorPane.setBottomAnchor(copy, AnchorPane.getBottomAnchor(orig));
    }
}
