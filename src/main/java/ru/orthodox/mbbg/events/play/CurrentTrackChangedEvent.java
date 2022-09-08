package ru.orthodox.mbbg.events.play;

import org.springframework.context.ApplicationEvent;

public class CurrentTrackChangedEvent extends ApplicationEvent {
    public CurrentTrackChangedEvent(Object source) {
        super(source);
    }
}
