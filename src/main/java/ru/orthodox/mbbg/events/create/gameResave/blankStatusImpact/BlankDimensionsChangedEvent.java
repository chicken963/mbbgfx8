package ru.orthodox.mbbg.events.create.gameResave.blankStatusImpact;

import ru.orthodox.mbbg.model.proxy.create.RoundTab;

public class BlankDimensionsChangedEvent extends BlankOutdatedEvent {

    private final RoundTab tab;

    public BlankDimensionsChangedEvent(Object source) {
        super(source);
        this.tab = (RoundTab) source;
    }

    public RoundTab getTab() {
        return tab;
    }
}
