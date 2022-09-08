package ru.orthodox.mbbg.events.create.gameResave.blankStatusImpact;

import ru.orthodox.mbbg.model.proxy.create.AudioTrackEditUIView;

public class AudioTrackTextFieldEditRequestedEvent extends BlankOutdatedEvent {
    private final AudioTrackEditUIView row;
    public AudioTrackTextFieldEditRequestedEvent(AudioTrackEditUIView source) {
        super(source);
        this.row = source;
    }

    public AudioTrackEditUIView getRow() {
        return row;
    }
}
