package ru.orthodox.mbbg.model.proxy.create;

import ru.orthodox.mbbg.model.basic.AudioTrack;

import java.util.List;

public interface AudioTracksTable {
    List<AudioTrackEditUIView> getRows();
    List<AudioTrack> getAudioTracks();
}
