package ru.orthodox.mbbg.events;

import org.springframework.context.ApplicationEvent;
import ru.orthodox.mbbg.model.proxy.play.RoundTab;

public class TabCreatedEvent extends ApplicationEvent {
    private final RoundTab roundTab;

    public TabCreatedEvent(Object source) {
        super(source);
        this.roundTab = (RoundTab) source;
    }

    public RoundTab getRoundTab() {
        return this.roundTab;
    }
}
