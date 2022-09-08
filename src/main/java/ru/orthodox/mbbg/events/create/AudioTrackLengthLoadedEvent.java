package ru.orthodox.mbbg.events.create;

import org.springframework.context.ApplicationEvent;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.basic.Round;

public class AudioTrackLengthLoadedEvent extends ApplicationEvent {
    private final AudioTrack audioTrack;
    private final Round round;

    public AudioTrackLengthLoadedEvent(final AudioTrack audioTrack, Round round) {
        super(round);
        this.round = round;
        this.audioTrack = audioTrack;
    }

    public AudioTrack getAudioTrack() {
        return this.audioTrack;
    }

    public Round getRound() {
        return round;
    }
}
