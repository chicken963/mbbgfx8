package ru.orthodox.mbbg.events;

import org.springframework.context.ApplicationEvent;
import ru.orthodox.mbbg.model.proxy.create.RoundTab;

public class BlankDimensionsChangedEvent extends ApplicationEvent {

    private final RoundTab tab;

    public BlankDimensionsChangedEvent(Object source) {
        super(source);
        this.tab = (RoundTab) source;
    }

    public RoundTab getTab() {
        return tab;
    }
}
