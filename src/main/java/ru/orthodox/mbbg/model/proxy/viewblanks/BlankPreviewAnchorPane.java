package ru.orthodox.mbbg.model.proxy.viewblanks;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import lombok.Getter;
import org.springframework.cache.annotation.Cacheable;
import ru.orthodox.mbbg.model.basic.Blank;
import ru.orthodox.mbbg.utils.common.CustomFontDealer;
import ru.orthodox.mbbg.utils.hierarchy.ElementFinder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.orthodox.mbbg.utils.common.CustomFontDealer.setDefaultFont;
import static ru.orthodox.mbbg.utils.hierarchy.NodeDeepCopyProvider.createDeepCopy;

@Getter
@Cacheable
public class BlankPreviewAnchorPane {

    private final AnchorPane anchorPane;
    private final Blank blank;
    private final GridPane gridPane;
    private final Label templateGridItem;
    private static final double BLANK_GRID_WIDTH = 360.0;
    private static final double BLANK_GRID_HEIGHT = 280.0;
    private final String roundName;

    public BlankPreviewAnchorPane(AnchorPane anchorPane, Blank blank, String roundName, Label blankItem) {
        this.anchorPane = anchorPane;
        this.blank = blank;
        this.gridPane = ElementFinder.findElementById(anchorPane, "gridContent");
        this.roundName = roundName;
        this.templateGridItem = blankItem;
        setDefaultFont();
    }

    public List<Label> addItemsToPreview() {
        Label blankNumberLabel = ElementFinder.findRecursivelyByStyleClass(anchorPane, "blank-number")
                .stream().map(element -> (Label) element)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("No element with style class blankId was found"));
        blankNumberLabel.setText(blank.getNumber());

        Label roundNameLabel = ElementFinder.findRecursivelyByStyleClass(anchorPane, "round-name")
                .stream().map(element -> (Label) element)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("No element with style class round-name was found"));
        roundNameLabel.setText(roundName);

        setDefaultFont(blankNumberLabel, roundNameLabel);

        int blankWidth = blank.getWidth();
        int blankHeight = blank.getHeight();

        double gridItemWidthInPixels = BLANK_GRID_WIDTH / blankWidth;
        double gridItemHeightInPixels = BLANK_GRID_HEIGHT / blankHeight;

        ColumnConstraints columnConstraints = new ColumnConstraints(
                gridItemWidthInPixels,
                gridItemWidthInPixels,
                gridItemWidthInPixels);

        RowConstraints rowConstraints = new RowConstraints(
                gridItemHeightInPixels,
                gridItemHeightInPixels,
                gridItemHeightInPixels);

        gridPane.getColumnConstraints().setAll(
                Stream.generate(() -> columnConstraints)
                        .limit(blankWidth)
                        .collect(Collectors.toList()));

        gridPane.getRowConstraints().setAll(
                Stream.generate(() -> rowConstraints)
                        .limit(blankHeight)
                        .collect(Collectors.toList()));

        List<Label> gridItems = blank.getBlankItems().stream()
                .map(blankItem -> {
                    Label gridItem = (Label) createDeepCopy(templateGridItem);
                    configureGridItemSize(gridItem, gridItemWidthInPixels, gridItemHeightInPixels);
                    GridPane.setColumnIndex(gridItem, blankItem.getXIndex());
                    GridPane.setRowIndex(gridItem, blankItem.getYIndex());
                    gridItem.setText(blankItem.getArtist());
                    gridItem.getStyleClass().add("blank-item");
                    if (blankItem.getXIndex() == 0) {
                        gridItem.getStyleClass().add("left");
                    } else if (blankItem.getXIndex() == blank.getWidth() - 1) {
                        gridItem.getStyleClass().add("right");
                    }
                    if (blankItem.getYIndex() == 0) {
                        gridItem.getStyleClass().add("upper");
                    } else if (blankItem.getYIndex() == blank.getHeight() - 1) {
                        gridItem.getStyleClass().add("lower");
                    }
                    return gridItem;
                }).collect(Collectors.toList());

        gridPane.getChildren().setAll(gridItems);

        return gridItems;
    }

    private void configureGridItemSize(Label gridItem, double gridItemWidthInPixels, double gridItemHeightInPixels) {
        gridItem.setPrefHeight(gridItemHeightInPixels);
        gridItem.setPrefWidth(gridItemWidthInPixels);
        gridItem.setMinHeight(gridItemHeightInPixels);
        gridItem.setMinWidth(gridItemWidthInPixels);
        gridItem.setMaxHeight(gridItemHeightInPixels);
        gridItem.setMaxWidth(gridItemWidthInPixels);
    }
}
