package ru.orthodox.mbbg.enums;

import lombok.Getter;

import java.util.Arrays;

public enum WinCondition {
    ONE_LINE_STRIKE("One line strike", 0),
    TWO_LINES_STRIKE("Two lines strike", 1),
    THREE_LINES_STRIKE("Three lines strike", 2),
    FOUR_LINES_STRIKE("Four lines strike", 3),
    FIVE_LINES_STRIKE("Five lines strike", 4),
    WHOLE_FIELD_STRIKE("Whole field strike", 5);

    @Getter
    private final int hardness;

    @Getter
    private final String message;

    WinCondition(String message, int hardness) {
        this.hardness = hardness;
        this.message = message;
    }

    public static WinCondition findByMessage(String message) {
        return Arrays.stream(WinCondition.values())
                .filter(winCondition -> winCondition.getMessage().equals(message))
                .findFirst()
                .orElse(null);
    }

    public int compare(WinCondition other) {
        return this.hardness - other.hardness;
    }

    @Override
    public String toString() {
        return this.getMessage();
    }
}
