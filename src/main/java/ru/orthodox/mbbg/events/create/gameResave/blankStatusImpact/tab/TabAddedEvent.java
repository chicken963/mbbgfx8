package ru.orthodox.mbbg.events.create.gameResave.blankStatusImpact.tab;

import ru.orthodox.mbbg.events.create.gameResave.blankStatusImpact.BlankOutdatedEvent;
import ru.orthodox.mbbg.model.proxy.create.RoundTab;

public class TabAddedEvent extends BlankOutdatedEvent {
    private final RoundTab roundTab;

    public TabAddedEvent(Object source, RoundTab roundTab) {
        super(source);
        this.roundTab = roundTab;
    }

    public RoundTab getRoundTab() {
        return this.roundTab;
    }
}
