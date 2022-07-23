package ru.orthodox.mbbg.utils;

import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.ui.modelExtensions.newGameScene.AudioTrackGridRow;

import java.util.List;

public class GridRowUtils {

    public static AudioTrackGridRow findByAudioTrack(List<AudioTrackGridRow> gridRows, AudioTrack audioTrack) {
        return gridRows.stream()
                .filter(row -> row.getAudioTrack().equals(audioTrack))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("there is no Audio track in grid"));
    }

}
