package ru.orthodox.mbbg.events.create.gameResave.blankStatusImpact.tab;

import ru.orthodox.mbbg.events.create.gameResave.blankStatusImpact.BlankOutdatedEvent;
import ru.orthodox.mbbg.model.proxy.create.RoundTab;

public class TabClosedEvent extends BlankOutdatedEvent {

    private final RoundTab roundTab;

    public TabClosedEvent(Object source) {
        super(source);
        this.roundTab = (RoundTab) source;
    }

    public RoundTab getRoundTab() {
        return this.roundTab;
    }
}
