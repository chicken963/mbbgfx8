package ru.orthodox.mbbg.utils.common;

import javafx.scene.control.Labeled;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class CustomFontDealer {

    private static InputStream fontFileInputStream;
    private static final ClassLoader loader  = Thread.currentThread().getContextClassLoader();

    public static void setDefaultFont(Labeled... nodes) {
        for (Labeled node : nodes) {
            fontFileInputStream = loader.getResourceAsStream("fonts/AntykwaTorunskaMed-Regular.ttf");
            node.setFont(Font.loadFont(fontFileInputStream, node.getFont().getSize()));
        }
    }

    public static void setDefaultFont(Tooltip tooltip) {
        fontFileInputStream = loader.getResourceAsStream("fonts/AntykwaTorunskaMed-Regular.ttf");
        tooltip.setFont(Font.loadFont(fontFileInputStream, tooltip.getFont().getSize()));
    }
}

