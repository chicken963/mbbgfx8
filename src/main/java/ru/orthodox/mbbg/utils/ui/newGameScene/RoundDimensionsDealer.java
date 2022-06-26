package ru.orthodox.mbbg.utils.ui.newGameScene;

import javafx.scene.control.ChoiceBox;

public class RoundDimensionsDealer {

    private ChoiceBox rowsNumber;
    private ChoiceBox columnsNumber;

    public RoundDimensionsDealer(ChoiceBox rowsNumber, ChoiceBox columnsNumber) {
        this.rowsNumber = rowsNumber;
        this.columnsNumber = columnsNumber;

        rowsNumber.getItems().addAll(2, 4, 5, 6, 7, 8);
        columnsNumber.getItems().addAll(2, 4, 5, 6, 7, 8, 9, 10, 11, 12);
    }
}
