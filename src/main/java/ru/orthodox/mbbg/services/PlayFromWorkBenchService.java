package ru.orthodox.mbbg.services;

import ru.orthodox.mbbg.model.AudioTrack;

import java.util.Collections;

public class PlayFromWorkBenchService {

    public static PlayService defineOnPlayLogic(PlayService playService, AudioTrack audioTrack) {
        if (playService != null
                && playService.getCurrentTrack().equals(audioTrack)
                && playService.isPaused()
                && playService.isCurrentStopInPlayableRange()) {
            playService.play();
            return playService;
        } else if (playService != null) {
            playService.stop();
        }
        playService = new PlayService(Collections.singletonList(audioTrack));
        playService.play();
        return playService;
    }


    public static void defineOnStopLogic(PlayService playService, AudioTrack audioTrack) {
        if (playService != null && playService.getCurrentTrack().equals(audioTrack)) {
            playService.stop();
        }
    }

    public static void defineOnPauseLogic(PlayService playService, AudioTrack audioTrack) {
        if (playService != null && playService.getCurrentTrack().equals(audioTrack)) {
            playService.pause();
        }
    }
}
