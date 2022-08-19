package ru.orthodox.mbbg.model.proxy;

import javafx.scene.paint.Color;

public class RGBColor{

    public static Color of(int red, int green, int blue) {
        return of(red, green, blue, 1.0);
    }

    public static Color of(int red, int green, int blue, double opacity) {
        return new Color(red / 255.0, green / 255.0, blue / 255.0, opacity);
    }


}
