package ru.orthodox.mbbg.events.create.gameResave;

import ru.orthodox.mbbg.model.basic.AudioTrack;

public class AudioTrackBoundsWereChanged extends GameOutdatedEvent {
    public AudioTrackBoundsWereChanged(AudioTrack audioTrack) {
        super(audioTrack);
    }
}
