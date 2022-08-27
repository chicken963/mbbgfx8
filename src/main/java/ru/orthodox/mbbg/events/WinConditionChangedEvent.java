package ru.orthodox.mbbg.events;

import org.springframework.context.ApplicationEvent;
import ru.orthodox.mbbg.enums.WinCondition;
import ru.orthodox.mbbg.model.proxy.play.RoundTab;

public class WinConditionChangedEvent extends ApplicationEvent {
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
