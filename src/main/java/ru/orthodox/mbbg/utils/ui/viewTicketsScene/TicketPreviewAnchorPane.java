package ru.orthodox.mbbg.utils.ui.viewTicketsScene;

import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.TextAlignment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import ru.orthodox.mbbg.model.Card;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.orthodox.mbbg.utils.ui.HierarchyUtils.findRecursivelyByStyleClass;
import static ru.orthodox.mbbg.utils.ui.NodeDeepCopyProvider.createDeepCopy;
import static ru.orthodox.mbbg.utils.ui.newGameScene.ElementFinder.findElementById;

@Getter
@Cacheable
public class TicketPreviewAnchorPane {

    private final AnchorPane anchorPane;
    private final Card card;
    private final GridPane gridPane;
    private final Label templateGridItem;
    private static final double TICKET_GRID_WIDTH = 360.0;
    private static final double TICKET_GRID_HEIGHT = 280.0;
    private final String roundName;

    public TicketPreviewAnchorPane(AnchorPane anchorPane, Card card, String roundName, Label cardItem) {
        this.anchorPane = anchorPane;
        this.card = card;
        this.gridPane = findElementById(anchorPane, "gridContent");
        this.roundName = roundName;
        this.templateGridItem = cardItem;
    }

    public void render() {
        Label ticketIdLabel = findRecursivelyByStyleClass(anchorPane, "ticketNumber")
                .stream().map(element -> (Label) element)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("No element with style class cardId was found"));
        ticketIdLabel.setText(card.getNumber());

        Label roundNameLabel = findRecursivelyByStyleClass(anchorPane, "roundName")
                .stream().map(element -> (Label) element)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("No element with style class cardId was found"));
        roundNameLabel.setText(roundName);

        int cardWidth = card.getWidth();
        int cardHeight = card.getHeight();

        double gridItemWidthInPixels = TICKET_GRID_WIDTH / cardWidth;
        double gridItemHeightInPixels = TICKET_GRID_HEIGHT / cardHeight;

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
                        .limit(cardWidth)
                        .collect(Collectors.toList()));

        gridPane.getRowConstraints().setAll(
                Stream.generate(() -> rowConstraints)
                        .limit(cardHeight)
                        .collect(Collectors.toList()));

        List<Label> gridItems = card.getCardItems().stream()
                .map(cardItem -> {
                    Label gridItem = createDeepCopy(templateGridItem);
                    gridItem.setPrefHeight(gridItemHeightInPixels);
                    gridItem.setPrefWidth(gridItemWidthInPixels);
                    gridItem.setMinHeight(gridItemHeightInPixels);
                    gridItem.setMinWidth(gridItemWidthInPixels);
                    gridItem.setMaxHeight(gridItemHeightInPixels);
                    gridItem.setMaxWidth(gridItemWidthInPixels);
                    GridPane.setColumnIndex(gridItem, cardItem.getXIndex());
                    GridPane.setRowIndex(gridItem, cardItem.getYIndex());
                    gridItem.setText(cardItem.getArtist());
                    gridItem.getStyleClass().add("card-item");
                    if (cardItem.getXIndex() == 0) {
                        gridItem.getStyleClass().add("left");
                    }
                    if (cardItem.getYIndex() == card.getHeight() - 1) {
                        gridItem.getStyleClass().add("lower");
                    }
                    return gridItem;
                }).collect(Collectors.toList());

        gridPane.getChildren().setAll(gridItems);


    }
}
