package ru.orthodox.mbbg.events.play;

import org.springframework.context.ApplicationEvent;
import ru.orthodox.mbbg.model.basic.AudioTrack;

public class NextTrackChangeRequestedByUserEvent extends ApplicationEvent {

    private final AudioTrack audioTrack;

    public NextTrackChangeRequestedByUserEvent(Object source, final AudioTrack audioTrack) {
        super(source);
        this.audioTrack = audioTrack;
    }

    public AudioTrack getAudioTrack() {
        return this.audioTrack;
    }
}
