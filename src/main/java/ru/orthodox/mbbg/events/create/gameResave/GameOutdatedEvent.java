package ru.orthodox.mbbg.events.create.gameResave;

import org.springframework.context.ApplicationEvent;

public abstract class GameOutdatedEvent extends ApplicationEvent {
    public GameOutdatedEvent(Object source) {
        super(source);
    }
}
