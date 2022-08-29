package ru.orthodox.mbbg.events;

import org.springframework.context.ApplicationEvent;
import ru.orthodox.mbbg.model.proxy.create.RoundTab;

public class TabClosedEvent extends ApplicationEvent {

    private final RoundTab roundTab;

    public TabClosedEvent(Object source) {
        super(source);
        this.roundTab = (RoundTab) source;
    }

    public RoundTab getRoundTab() {
        return this.roundTab;
    }
}
