package ru.orthodox.mbbg.events;

import ru.orthodox.mbbg.model.proxy.create.RoundTab;

public class RoundNameChangedEvent extends TextFieldChangeEvent {
    private final RoundTab roundTab;

    public RoundNameChangedEvent(Object source) {
        super(source);
        this.roundTab = (RoundTab) source;
    }

    public RoundTab getRoundTab() {
        return roundTab;
    }
}
