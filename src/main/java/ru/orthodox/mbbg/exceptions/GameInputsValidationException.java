package ru.orthodox.mbbg.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GameInputsValidationException extends RuntimeException {
    public GameInputsValidationException(String message) {
        super(message);
    }
}
