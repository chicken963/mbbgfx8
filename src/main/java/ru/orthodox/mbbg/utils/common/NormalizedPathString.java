package ru.orthodox.mbbg.utils.common;

import lombok.Getter;

import java.nio.file.Paths;

public class NormalizedPathString {
    @Getter
    private String expression;

    public NormalizedPathString(String expression) {
        this.expression = expression;
    }

    public static String of(String pathInMusicFolder){
        return new NormalizedPathString(Paths.get(pathInMusicFolder).toUri().toString()).getExpression();
    }

}
