package ru.orthodox.mbbg.services.create;

import javafx.scene.control.ChoiceBox;

public class RoundDimensionsDealer {

    public static void populateDimensionsChoiceBox(ChoiceBox<String> blankDimensions) {
        blankDimensions.getItems().addAll("3 x 3", "4 x 4", "5 x 5", "8 x 8");
    }

    public static int getBlankDimension(ChoiceBox<String> blankDimensions) {
        return Integer.parseInt(blankDimensions.getValue().split("x")[0].trim());
    }
}
