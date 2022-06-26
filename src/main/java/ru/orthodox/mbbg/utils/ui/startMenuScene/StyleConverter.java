package ru.orthodox.mbbg.utils.ui.startMenuScene;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StyleConverter  {
    public Map<String, String> toMap(String s) {
        return Arrays.stream(s.split(";"))
                .map(String::trim)
                .collect(Collectors.toMap(
                        keyValue -> keyValue.split(":")[0].trim(),
                        keyValue -> keyValue.split(":")[1].trim()
                ));
    }

    public String toString(Map<String, String> stylesMap) {
        return stylesMap.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(";"));
    }
}
