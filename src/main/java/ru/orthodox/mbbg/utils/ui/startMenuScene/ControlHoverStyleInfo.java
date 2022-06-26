package ru.orthodox.mbbg.utils.ui.startMenuScene;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Builder
@Getter
public class ControlHoverStyleInfo {

    private String radiusPattern;

    @Setter
    private Map<String, String> hoverStyle;
    @Setter
    private Map<String, String> defaultStyle;
}
