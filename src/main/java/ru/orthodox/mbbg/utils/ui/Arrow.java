package ru.orthodox.mbbg.utils.ui;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;

import static ru.orthodox.mbbg.utils.ui.CustomFontDealer.setDefaultFont;

public class Arrow extends Region {

    private static final double ARROW_LENGTH = 12;
    private static final Insets MARGIN = new Insets(1, ARROW_LENGTH, 1, 1);

    private final HBox container;
    private final HLineTo hLineTop;
    private final LineTo tipTop;
    private final LineTo tipBottom;
//    private final LineTo tailBottom;

    public Arrow(Node graphic, String title) {
        Path path = new Path(
                new MoveTo(),
                hLineTop = new HLineTo(),
                tipTop = new LineTo(ARROW_LENGTH, 0),
                tipBottom = new LineTo(-ARROW_LENGTH, 0),
                new HLineTo(),
                new ClosePath());

        tipTop.setAbsolute(false);
        tipBottom.setAbsolute(false);

        path.setManaged(false);
        path.setStrokeType(StrokeType.INSIDE);
        path.getStyleClass().add("arrow-shape");

        Label labelTitle = new Label(title, graphic);
        labelTitle.setFont(new Font(12));
        setDefaultFont(labelTitle);
        container = new HBox(labelTitle);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setMinWidth(90);

        getChildren().addAll(path, container);
//        HBox.setHgrow(labelTitle, Priority.ALWAYS);
        VBox.setMargin(container, new Insets(50, 0, 0, 0));
        labelTitle.setAlignment(Pos.CENTER);
        labelTitle.setMaxWidth(Double.POSITIVE_INFINITY);
    }

    @Override
    protected void layoutChildren() {
        // hbox layout
        Insets insets = getInsets();
        double left = insets.getLeft();
        double top = insets.getTop();
        double width = getWidth();
        double height = 30;
        layoutInArea(container,
                left, top,
                width - left - insets.getRight(), height - top - insets.getBottom(),
                0, MARGIN, true, true, HPos.LEFT, VPos.TOP);

        // adjust arrow shape
        double length = width - ARROW_LENGTH;
        double h2 = height / 2;

        hLineTop.setX(length);

        tipTop.setY(h2);
        tipBottom.setY(h2);
//        tailBottom.setY(h2);
    }

    @Override
    protected double computeMinWidth(double height) {
        Insets insets = getInsets();
        return 2 * ARROW_LENGTH + insets.getLeft() + insets.getRight() + container.minWidth(height);
    }

    @Override
    protected double computeMinHeight(double width) {
        Insets insets = getInsets();
        return 2 + insets.getTop() + insets.getBottom() + container.minHeight(width);
    }

    @Override
    protected double computePrefWidth(double height) {
        Insets insets = getInsets();
        return 2 * ARROW_LENGTH + insets.getLeft() + insets.getRight() + container.prefWidth(height);
    }

    @Override
    protected double computePrefHeight(double width) {
        Insets insets = getInsets();
        return 2 + insets.getTop() + insets.getBottom() + container.prefHeight(width);
    }

    @Override
    protected double computeMaxWidth(double height) {
        Insets insets = getInsets();
        return 2 * ARROW_LENGTH + insets.getLeft() + insets.getRight() + container.maxWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width) {
        Insets insets = getInsets();
        return 2 + insets.getTop() + insets.getBottom() + container.maxHeight(width);
    }

}