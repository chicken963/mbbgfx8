package ru.orthodox.mbbg.events;

import org.springframework.context.ApplicationEvent;

public class NextTrackChangedEvent extends ApplicationEvent {

    public NextTrackChangedEvent(Object source) {
        super(source);
    }
}
