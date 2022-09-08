package ru.orthodox.mbbg.events.create;

import org.springframework.context.ApplicationEvent;
import ru.orthodox.mbbg.model.basic.AudioTrack;

public class BrokenAudiotrackEvent extends ApplicationEvent {
    private final AudioTrack audioTrack;

    public BrokenAudiotrackEvent(AudioTrack source) {
        super(source);
        this.audioTrack = source;
    }

    public AudioTrack getAudioTrack() {
        return audioTrack;
    }
}
