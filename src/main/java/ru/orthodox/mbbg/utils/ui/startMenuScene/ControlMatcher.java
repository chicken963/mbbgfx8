package ru.orthodox.mbbg.utils.ui.startMenuScene;

import javafx.scene.control.Control;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@Component
public class ControlMatcher implements BiFunction<Control, ControlHoverStyleInfo, Boolean> {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Boolean apply(Control control, ControlHoverStyleInfo controlHoverStyleInfo) {

        String styleAsString = control.getStyle();
        String backgroundRadius = applicationContext.getBean(StyleConverter.class)
                .toMap(styleAsString)
                .get("-fx-background-radius");
        return backgroundRadius.matches(controlHoverStyleInfo.getRadiusPattern());
    }

}
