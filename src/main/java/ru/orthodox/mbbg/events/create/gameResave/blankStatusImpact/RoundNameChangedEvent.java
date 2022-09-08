package ru.orthodox.mbbg.events.create.gameResave.blankStatusImpact;

import ru.orthodox.mbbg.model.proxy.create.RoundTab;

public class RoundNameChangedEvent extends BlankOutdatedEvent {
    private final RoundTab roundTab;

    public RoundNameChangedEvent(Object source) {
        super(source);
        this.roundTab = (RoundTab) source;
    }

    public RoundTab getRoundTab() {
        return roundTab;
    }
}
