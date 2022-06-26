package ru.orthodox.mbbg.utils.ui.startMenuScene;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class HoverDealerConfiguration {
    @Bean
    public ControlHoverStyleInfo upperButtonStyleInfo() {
        return ControlHoverStyleInfo.builder()
                .radiusPattern("0 \\d+px 0 0")
                .hoverStyle(new HashMap<String, String>() {{
                                put("-fx-scale-y", "1.05");
                                put("-fx-scale-x", "1.05");
                                put("-fx-background-color", "linear-gradient(rgba(246,132,182,1) 0%, rgba(238,63,112,1) 61%)");
                            }}
                )
                .build();
    }

    @Bean
    public ControlHoverStyleInfo middleButtonStyleInfo() {
        return ControlHoverStyleInfo.builder()
                .radiusPattern("0 0 0 0")
                .hoverStyle(
                        new HashMap<String, String>() {{
                            put("-fx-scale-y", "1.05");
                            put("-fx-scale-x", "1.05");
                            put("-fx-background-color", "linear-gradient(rgba(254,243,172,1) 0%, rgba(237,216,96,1) 61%)");
                        }}
                )
                .build();
    }

    @Bean
    public ControlHoverStyleInfo lowerButtonStyleInfo() {
        return ControlHoverStyleInfo.builder()
                .radiusPattern("0 0 \\d+px 0")
                .hoverStyle(
                        new HashMap<String, String>() {{
                            put("-fx-scale-y", "1.05");
                            put("-fx-scale-x", "1.05");
                            put("-fx-background-color", "linear-gradient(rgba(36,231,45,1) 0%, rgba(29,180,36,1) 91%)");
                        }}
                )
                .build();
    }
}
