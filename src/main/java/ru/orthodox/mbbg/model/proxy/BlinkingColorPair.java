package ru.orthodox.mbbg.model.proxy;

import javafx.scene.paint.Color;
import lombok.Data;
import ru.orthodox.mbbg.model.proxy.viewblanks.AnimatedColorSet;

import static ru.orthodox.mbbg.configuration.ProgressGradientConstants.*;

@Data
public class BlinkingColorPair implements AnimatedColorSet {
    private final Color backgroundColor;
    private final Color blinkingColor;

    public static BlinkingColorPair forNextProgress(double nextProgress) {
        return new BlinkingColorPair(
                UNFILLED_COLOR,
                nextProgress == 1
                        ? BLINKING_COLOR_WIN
                        : BLINKING_COLOR);
    }
}
