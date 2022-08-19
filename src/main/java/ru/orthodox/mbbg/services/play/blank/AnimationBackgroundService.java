package ru.orthodox.mbbg.services.play.blank;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.util.Duration;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.Blank;
import ru.orthodox.mbbg.model.proxy.BlinkingColorPair;
import ru.orthodox.mbbg.model.proxy.HorizontalGradient;

import java.util.List;

@Service
public class AnimationBackgroundService {

    public Animation provide(Blank blank, HorizontalGradient gradient) {
        Color filledColor = gradient.getFilledColor();
        Color blinkingColor = gradient.getBlinkingColor();
        Color unFilledColor = gradient.getUnfilledColor();

        return new Transition() {
            {
                setCycleDuration(Duration.millis(1000));
                setCycleCount(Animation.INDEFINITE);
                setInterpolator(Interpolator.LINEAR);

            }

            @Override
            protected void interpolate(double frac) {

                Color animatedColor = generateIntermediateAnimationColor(blinkingColor, unFilledColor, frac);
                Stop[] stops = new Stop[]{
                        new Stop(0, filledColor),
                        new Stop(blank.getProgress(), filledColor),
                        new Stop(blank.getProgress(), animatedColor),
                        new Stop(blank.getNextProgress(), animatedColor),
                        new Stop(blank.getNextProgress(), unFilledColor),
                        new Stop(1, unFilledColor)
                };
                LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
                blank.getMiniatureButton().setBackground(new Background(new BackgroundFill(lg1, new CornerRadii(10.0), Insets.EMPTY)));
            }
        };
    }

    public Animation provide(Blank blank, BlinkingColorPair colorPair) {
        return provide(blank.getMiniatureButton(), colorPair, 10.0);
    }

    public Animation provide(Control uiElement,
                             BlinkingColorPair colorPair,
                             Double cornerRadius) {
        return new Transition() {
            {
                setCycleDuration(Duration.millis(1000));
                setCycleCount(Animation.INDEFINITE);
                setInterpolator(Interpolator.LINEAR);
            }

            @Override
            protected void interpolate(double frac) {
                Color animatedColor = generateIntermediateAnimationColor(
                        colorPair.getBlinkingColor(),
                        colorPair.getBackgroundColor(),
                        frac);
                CornerRadii cornerRadii = provideCornerRadii(uiElement, cornerRadius);
                uiElement.setBackground(new Background(new BackgroundFill(animatedColor, cornerRadii, Insets.EMPTY)));
            }
        };
    }


    private Color generateIntermediateAnimationColor(Color targetColor, Color backgroundColor, double frac) {
        double redBlink = targetColor.getRed() - backgroundColor.getRed();
        double greenBlink = targetColor.getGreen() - backgroundColor.getGreen();
        double blueBlink = targetColor.getBlue() - backgroundColor.getBlue();
        return new Color(
                backgroundColor.getRed() + redBlink * frac,
                backgroundColor.getGreen() + greenBlink * frac,
                backgroundColor.getBlue() + blueBlink * frac,
                1);
    }

    private CornerRadii provideCornerRadii(Control control, double radius) {
        List<String> slyleClasses = control.getStyleClass();
        if (isUpperLeft(slyleClasses)) {
            return new CornerRadii(radius, 0, 0, 0, false);
        } else if (isUpperRight(slyleClasses)) {
            return new CornerRadii(0, radius, 0, 0, false);
        } else if (isLowerRight(slyleClasses)) {
            return new CornerRadii(0, 0, radius, 0, false);
        } else if (isLowerLeft(slyleClasses)) {
            return new CornerRadii(0, 0, 0, radius, false);
        } else if (control instanceof Label) {
            return new CornerRadii(0);
        } else {
            return new CornerRadii(radius);
        }
    }

    private boolean isUpperLeft(List<String> styleClasses) {
        return styleClasses.contains("upper") && styleClasses.contains("left");
    }

    private boolean isUpperRight(List<String> styleClasses) {
        return styleClasses.contains("upper") && styleClasses.contains("right");
    }

    private boolean isLowerRight(List<String> styleClasses) {
        return styleClasses.contains("lower") && styleClasses.contains("right");
    }

    private boolean isLowerLeft(List<String> styleClasses) {
        return styleClasses.contains("lower") && styleClasses.contains("left");
    }
}
