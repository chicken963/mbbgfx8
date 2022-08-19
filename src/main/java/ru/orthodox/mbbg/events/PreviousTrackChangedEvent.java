package ru.orthodox.mbbg.events;

import org.springframework.context.ApplicationEvent;

public class PreviousTrackChangedEvent extends ApplicationEvent {
    public PreviousTrackChangedEvent(Object source) {
        super(source);
    }
}
