package ru.orthodox.mbbg.services.create;

import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.proxy.create.AudioTrackUIView;

import java.util.List;

public class AudiotrackAsGridRowService {

    public static AudioTrackUIView findByAudioTrack(List<AudioTrackUIView> gridRows, AudioTrack audioTrack) {
        return gridRows.stream()
                .filter(row -> row.getAudioTrack().equals(audioTrack))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("there is no Audio track " + audioTrack.getTitle() + " in grid"));
    }

}
