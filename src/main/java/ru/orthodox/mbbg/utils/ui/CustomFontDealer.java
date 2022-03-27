package ru.orthodox.mbbg.utils.ui;

import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
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
}
