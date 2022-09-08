package ru.orthodox.mbbg.events.create.gameResave;

import ru.orthodox.mbbg.model.proxy.create.RoundTab;

public class WinConditionChangedEvent extends GameOutdatedEvent {
    private final int numberOfWinLevel;
    private final RoundTab roundTab;

    public WinConditionChangedEvent(Object source, RoundTab roundTab, int numberOfWinLevel) {
        super(source);
        this.roundTab = roundTab;
        this.numberOfWinLevel = numberOfWinLevel;
    }

    public int getNumberOfWinLevel() {
        return this.numberOfWinLevel;
    }

    public RoundTab getRoundTab() {
        return this.roundTab;
    }
}
