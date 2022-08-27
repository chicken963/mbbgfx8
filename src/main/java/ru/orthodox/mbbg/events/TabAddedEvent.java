package ru.orthodox.mbbg.events;

import org.springframework.context.ApplicationEvent;
import ru.orthodox.mbbg.model.proxy.play.RoundTab;

public class TabAddedEvent extends ApplicationEvent {
    private final RoundTab roundTab;

    public TabAddedEvent(Object source, RoundTab roundTab) {
        super(source);
        this.roundTab = roundTab;
    }

    public RoundTab getRoundTab() {
        return this.roundTab;
    }
}
