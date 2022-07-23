package ru.orthodox.mbbg.ui;

import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.utils.NormalizedPathString;

@Service
public class CustomFontDealer {

    private final static String DEFAULT_FONT_SOURCE = NormalizedPathString.of("src\\main\\resources\\fonts\\AntykwaTorunskaMed-Regular.ttf");

    public static void setDefaultFont(Labeled... nodes) {
        for (Labeled node : nodes) {
            node.setFont(Font.loadFont(DEFAULT_FONT_SOURCE, node.getFont().getSize()));
        }
    }

    public static void setDefaultFont(Tooltip tooltip) {
        tooltip.setFont(Font.loadFont(DEFAULT_FONT_SOURCE, tooltip.getFont().getSize()));
    }
}

