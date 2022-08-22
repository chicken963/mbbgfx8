package ru.orthodox.mbbg.events;

import org.springframework.context.ApplicationEvent;

public class ChoiceBoxChangeEvent extends ApplicationEvent {
    public ChoiceBoxChangeEvent(Object source) {
        super(source);
    }
}
