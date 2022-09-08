package ru.orthodox.mbbg.events.create;

import org.springframework.context.ApplicationEvent;
import ru.orthodox.mbbg.model.proxy.create.AudioTrackEditUIView;
import ru.orthodox.mbbg.model.proxy.create.AudioTracksTable;

public class ActiveRowChangedEvent extends ApplicationEvent {

    private final AudioTrackEditUIView row;
    private final AudioTracksTable table;

    public ActiveRowChangedEvent(AudioTracksTable table, AudioTrackEditUIView source) {
        super(table);
        this.row = source;
        this.table = table;
    }

    public AudioTrackEditUIView getRow() {
        return row;
    }

    public AudioTracksTable getTable() {
        return table;
    }
}
