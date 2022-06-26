package ru.orthodox.mbbg.utils.ui.startMenuScene;

import javafx.scene.control.Control;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HoverDealer {
    @Autowired
    private List<ControlHoverStyleInfo> controlHoverStyleInfoList;
    @Autowired
    private StyleConverter styleConverter;
    @Autowired
    private ControlMatcher controlMatcher;

    public void applyStylesOnMouseEnter(Control control) {
        Map<String, String> stylesMap = styleConverter.toMap(control.getStyle());

        ControlHoverStyleInfo styleInfo = controlHoverStyleInfoList.stream()
                .filter(controlHoverStyleInfo -> controlMatcher.apply(control, controlHoverStyleInfo))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Style info container was not found for style class "
                        + control.getStyleClass().get(0)));
        styleInfo.setDefaultStyle(stylesMap);

        Map<String, String> stylesToApply = styleInfo.getHoverStyle();
        Map<String, String> mergerStylesMap = new HashMap<>(stylesMap);

        mergerStylesMap.keySet().removeAll(stylesToApply.keySet());
        mergerStylesMap.putAll(stylesToApply);

        control.setStyle(styleConverter.toString(mergerStylesMap));
    }

    public void applyStylesOnMouseLeave(Control control) {



        Map<String, String> defaultStyle = controlHoverStyleInfoList.stream()
                .filter(controlHoverStyleInfo -> controlMatcher.apply(control, controlHoverStyleInfo))
                .findFirst()
                .map(ControlHoverStyleInfo::getDefaultStyle)
                        .orElseThrow(() -> new RuntimeException("Style info container was not found for style class "
                                + control.getStyleClass().get(0)));

        control.setStyle(styleConverter.toString(defaultStyle));
    }

}
