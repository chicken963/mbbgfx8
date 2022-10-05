package ru.orthodox.mbbg.enums;

import lombok.Getter;
//import org.springframework.lang.Nullable;

import java.util.Arrays;

public enum BlankSize {
    THREE_TO_THREE("3 x 3", 3),
    FOUR_TO_FOUR("4 x 4", 4),
    FIVE_TO_FIVE("5 x 5", 5);

    @Getter
    private final int capacity;

    @Getter
    private final String label;

    @Getter
    private final int mainSize;

    @Getter
//    @Nullable
    private final Integer additionalSize;

    BlankSize(String label, Integer size) {
        this(label, size, null);
    }


    BlankSize(String label, Integer size, Integer additionalSize) {
        this.capacity = additionalSize != null ? size * additionalSize : size * size;
        this.label = label;
        this.mainSize = size;
        this.additionalSize = additionalSize;
    }

    public static BlankSize withSize(int size) {
        return Arrays.stream(values())
                .filter(blankSize -> blankSize.getMainSize() == size)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("There is no blank size with width " + size));
    }

    @Override
    public String toString() {
        return this.getLabel();
    }
}
