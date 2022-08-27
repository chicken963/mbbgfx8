package ru.orthodox.mbbg.events;

import org.springframework.context.ApplicationEvent;
import ru.orthodox.mbbg.enums.EntityUpdateMode;
import ru.orthodox.mbbg.model.proxy.create.AudioTrackEditUIView;
import ru.orthodox.mbbg.model.proxy.create.AudioTrackGridRow;

public class GameAudioTracksListChangedEvent extends ApplicationEvent {
    private final AudioTrackEditUIView row;
    private final EntityUpdateMode mode;

    public GameAudioTracksListChangedEvent(Object source, final AudioTrackEditUIView row, EntityUpdateMode mode) {
        super(source);
        this.row = row;
        this.mode = mode;
    }

    public AudioTrackEditUIView getRow() {
        return this.row;
    }

    public EntityUpdateMode getMode() {
        return this.mode;
    }
}
