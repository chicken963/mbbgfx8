package ru.orthodox.mbbg.utils;

import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.ui.modelExtensions.newGameScene.AudioTrackGridRow;
import ru.orthodox.mbbg.ui.modelExtensions.newGameScene.AudioTrackUIMapper;

import java.util.List;

public class GridRowUtils {

    public static AudioTrackUIMapper findByAudioTrack(List<AudioTrackUIMapper> gridRows, AudioTrack audioTrack) {
        return gridRows.stream()
                .filter(row -> row.getAudioTrack().equals(audioTrack))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("there is no Audio track in grid"));
    }

}
