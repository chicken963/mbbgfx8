package ru.orthodox.mbbg.model.proxy;

import javafx.scene.paint.Color;
import lombok.Getter;
import ru.orthodox.mbbg.model.proxy.viewblanks.AnimatedColorSet;

import static ru.orthodox.mbbg.configuration.ProgressGradientConstants.*;

@Getter
public class HorizontalGradient implements AnimatedColorSet {
    private Color filledColor;
    private Color blinkingColor;
    private Color unfilledColor;

    public HorizontalGradient(Color filledColor, Color blinkingColor, Color unfilledColor) {
        this.filledColor = filledColor;
        this.blinkingColor = blinkingColor;
        this.unfilledColor = unfilledColor;
    }

    public static HorizontalGradient forNextProgress(double nextProgress) {
        return new HorizontalGradient(
                FILLED_COLOR,
                nextProgress == 1 ? BLINKING_COLOR_WIN : BLINKING_COLOR,
                UNFILLED_COLOR);
    }
}
