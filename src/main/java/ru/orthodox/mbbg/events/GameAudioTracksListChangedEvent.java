package ru.orthodox.mbbg.events;

import org.springframework.context.ApplicationEvent;
import ru.orthodox.mbbg.enums.EntityUpdateMode;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.model.proxy.create.AudioTrackEditUIView;

public class GameAudioTracksListChangedEvent extends ApplicationEvent {
    private final AudioTrackEditUIView row;
    private final EntityUpdateMode mode;
    private final Round round;

    public GameAudioTracksListChangedEvent(Round round, final AudioTrackEditUIView row, EntityUpdateMode mode) {
        super(round);
        this.row = row;
        this.mode = mode;
        this.round = round;
    }

    public AudioTrackEditUIView getRow() {
        return this.row;
    }

    public EntityUpdateMode getMode() {
        return this.mode;
    }


    public Round getRound() {
        return round;
    }
}
