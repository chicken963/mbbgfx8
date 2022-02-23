package ru.orthodox.mbbg.utils;

import lombok.Getter;

import java.nio.file.Paths;

public class NormalizedPathString {
    @Getter
    private String expression;

    public NormalizedPathString(String expression) {
        this.expression = expression;
    }

    public static NormalizedPathString of(String pathInMusicFolder){
        return new NormalizedPathString(Paths.get("src/main/resources/music/" + pathInMusicFolder).toUri().toString());
    }

}
