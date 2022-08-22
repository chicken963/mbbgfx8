package ru.orthodox.mbbg.events;

import org.springframework.context.ApplicationEvent;

public class TextFieldChangeEvent extends ApplicationEvent {
    public TextFieldChangeEvent(Object source) {
        super(source);
    }
}
