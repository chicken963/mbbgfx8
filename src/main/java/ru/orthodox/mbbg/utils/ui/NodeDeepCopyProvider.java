package ru.orthodox.mbbg.utils.ui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DisplacementMap;
import javafx.scene.effect.FloatMap;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Collection;

public class NodeDeepCopyProvider {

    public static Pane createDeepCopy(Pane region) {
        Pane copy;
        if (region instanceof HBox) {
            copy = createDeepCopy((HBox) region);
        } else if (region instanceof VBox) {
            copy = createDeepCopy((VBox) region);
        } else if (region instanceof AnchorPane) {
            copy = createDeepCopy((AnchorPane) region);
        } else {
            throw new ClassCastException(String.format("Class %s is unable to be processed as a Pane",
                    region.getClass()));
        }
        for (Node child : region.getChildren()) {
            Node childCopy;
            if (child instanceof Pane) {
                childCopy = createDeepCopy((Pane) child);
            } else if (child instanceof Control) {
                childCopy = createDeepCopy((Control) child);
            } else {
                throw new ClassCastException(String.format("Class %s was met among familiar node children and is " +
                        "unable to be processed", region.getClass()));
            }
            copy.getChildren().add(childCopy);
        }
        return copy;
    }

    public static Control createDeepCopy(Control control) {
        Control copy;
        if (control instanceof Label) {
            copy = createDeepCopy((Label) control);
        } else if (control instanceof Button) {
            copy = createDeepCopy((Button) control);
        } else if (control instanceof SplitPane) {
            copy = createDeepCopy((SplitPane) control);
        } else if (control instanceof ChoiceBox) {
            copy = createDeepCopy((ChoiceBox) control);
        } else if (control instanceof TextField) {
            copy = createDeepCopy((TextField) control);
        } else if (control instanceof TableView) {
            copy = createDeepCopy((TableView) control);
        } else {
            throw new ClassCastException(String.format("Class %s is unable to be processed as a Control",
                    control.getClass()));
        }
        return copy;
    }

    public static Pagination createDeepCopy(Pagination pagination) {
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
        copy.setGraphic(copyImageView((ImageView) sourceButton.getGraphic()));
        copy.setMnemonicParsing(sourceButton.isMnemonicParsing());
        copy.setStyle(sourceButton.getStyle());

        alignCommonRegionProperties(sourceButton, copy);
        alignCommonLabeledProperties(sourceButton, copy);
        alignTextProperties(sourceButton, copy);
        alignWidthAndHeight(sourceButton, copy);

        return copy;
    }

    private static Label createDeepCopy(Label sourceLabel) {
        Label copy = new Label();

        HBox.setMargin(copy, createDeepCopy(HBox.getMargin(sourceLabel)));
        alignCommonRegionProperties(sourceLabel, copy);
        alignCommonLabeledProperties(sourceLabel, copy);
        alignTextProperties(sourceLabel, copy);
        alignWidthAndHeight(sourceLabel, copy);

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
        VBox.setMargin(copy, VBox.getMargin(sourceHbox));
        HBox.setMargin(copy, createDeepCopy(HBox.getMargin(sourceHbox)));
        copy.setAlignment(sourceHbox.getAlignment());

        alignWidthAndHeight(sourceHbox, copy);
        alignCommonRegionProperties(sourceHbox, copy);
        alignAnchorInsets(sourceHbox, copy);

        return copy;
    }

    private static VBox createDeepCopy(VBox sourceVbox) {
        VBox copy = new VBox();
        VBox.setVgrow(copy, HBox.getHgrow(sourceVbox));
        copy.setAlignment(sourceVbox.getAlignment());

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
        return copy;
    }

    private static SplitPane createDeepCopy(SplitPane sourcePane) {
        SplitPane copy = new SplitPane();

        copy.setDividerPositions(sourcePane.getDividerPositions());
        copy.setPrefHeight(sourcePane.getPrefHeight());
        copy.setPrefWidth(sourcePane.getPrefWidth());

        Pane leftPane = (AnchorPane) sourcePane.getItems().get(0);
        Pane rightPane = (AnchorPane) sourcePane.getItems().get(1);

        copy.getItems().addAll(createDeepCopy(leftPane), createDeepCopy(rightPane));

        return copy;
    }

    public static Tab createDeepCopy(Tab sourceTab) {
        Tab copy = new Tab();
        SplitPane sourceTabContent = (SplitPane) sourceTab.getContent();

        SplitPane tabContentCopy = createDeepCopy(sourceTabContent);
        copy.setContent(tabContentCopy);
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
        copy.setStyle(textField.getStyle());
        copy.setPrefHeight(textField.getPrefHeight());
        copy.setPrefWidth(textField.getPrefWidth());
        return copy;
    }

    private static ChoiceBox createDeepCopy(ChoiceBox choiceBox) {
        ChoiceBox copy = new ChoiceBox();
        copy.setPrefHeight(choiceBox.getPrefHeight());
        copy.setPrefWidth(choiceBox.getPrefWidth());
        copy.setStyle(choiceBox.getStyle());
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
        copy.setOpaqueInsets(orig.getOpaqueInsets());
        copy.setStyle(orig.getStyle());
        copy.setOnMouseEntered(orig.getOnMouseEntered());
        copy.setOnMouseExited(orig.getOnMouseExited());
    }

    private static void alignTextProperties(Labeled orig, Labeled copy) {
        copy.setText(orig.getText());
        copy.setTextFill(orig.getTextFill());
        copy.setFont(orig.getFont());
    }

    private static void alignCommonLabeledProperties(Labeled orig, Labeled copy) {
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
