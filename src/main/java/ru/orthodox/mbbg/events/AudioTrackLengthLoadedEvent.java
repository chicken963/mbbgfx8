package ru.orthodox.mbbg.events;

import org.springframework.context.ApplicationEvent;
import ru.orthodox.mbbg.model.AudioTrack;

public class AudioTrackLengthLoadedEvent extends ApplicationEvent {
    private final AudioTrack audioTrack;

    public AudioTrackLengthLoadedEvent(Object source, final AudioTrack audioTrack) {
        super(source);
        this.audioTrack = audioTrack;
    }

    public AudioTrack getAudioTrack() {
        return this.audioTrack;
    }
}
