package ru.orthodox.mbbg.events.create.gameResave.blankStatusImpact;

import ru.orthodox.mbbg.events.create.gameResave.GameOutdatedEvent;

public abstract class BlankOutdatedEvent extends GameOutdatedEvent {
    public BlankOutdatedEvent(Object source) {
        super(source);
    }
}
