package ru.orthodox.mbbg.services.common;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.utils.common.NormalizedPathString;

import java.util.List;


@Service
public class PlayMediaService {
    @Getter
    private AudioTrack currentTrack;

    private double volumeCache = 1;
    private boolean muted = false;
    @Getter
    private boolean isPaused = false;
    @Getter
    private boolean isStopped = true;
    private Media media;
    @Getter
    private MediaPlayer mediaPlayer;

    public void play(AudioTrack audioTrack) {
        if (thisTrackIsAlreadyBeingPlayed(audioTrack)) {
            return;
        }
        if (switchingFromAnotherTrack(audioTrack)) {
            mediaPlayer.stop();
        }
        if (audioTrack != currentTrack || !isCurrentStopInPlayableRange()) {
            currentTrack = audioTrack;
            generateMediaPlayer(audioTrack);
        }
        mediaPlayer.setVolume(volumeCache);
        mediaPlayer.play();
        isPaused = false;
        isStopped = false;
    }

    private boolean thisTrackIsAlreadyBeingPlayed(AudioTrack audioTrack) {
        return currentTrack == audioTrack && !isPaused && !isStopped;
    }

    private boolean switchingFromAnotherTrack(AudioTrack audioTrack) {
        return currentTrack != null && currentTrack != audioTrack;
    }

    public void pause(AudioTrack audioTrack) {
        if (audioTrack != null && audioTrack == currentTrack) {
            mediaPlayer.pause();
            isPaused = true;
        }
    }

    public void stop(AudioTrack audioTrack) {
        if (audioTrack != null && audioTrack == currentTrack) {
            mediaPlayer.stop();
            isPaused = false;
            isStopped = true;
        }
    }


    public void switchMute() {
        this.muted = !this.muted;
        mediaPlayer.setVolume(volumeCache);
    }

    public void setVolume(double volume) {
        if (mediaPlayer == null) {
            volumeCache = volume * 0.01;
        } else {
            mediaPlayer.setVolume(volume * 0.01);
        }
    }

    public double getCurrentTime() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentTime().toSeconds();
        }
        return 0;
    }

    public double getCurrentSongLength() {
        if (media != null) {
            return media.getDuration().toSeconds();
        }
        return 0;
    }

    public void setCurrentTrackStartRate() {
        mediaPlayer.setStartTime(Duration.seconds(currentTrack.getStartInSeconds()));
    }

    public boolean isCurrentStopInPlayableRange() {
        double currentRate = mediaPlayer.getCurrentTime().toSeconds();
        return currentRate >= currentTrack.getStartInSeconds() && currentRate <= currentTrack.getFinishInSeconds();
    }

    protected void generateMediaPlayer(AudioTrack audioTrack) {
        if (mediaPlayer != null) {
            volumeCache = mediaPlayer.getVolume();
        }
        this.media = new Media(NormalizedPathString.of(audioTrack.getLocalPath()));
        this.mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setStartTime(Duration.seconds(audioTrack.getStartInSeconds()));
        mediaPlayer.setStopTime(Duration.seconds(audioTrack.getFinishInSeconds()));
        mediaPlayer.setVolume(muted ? 0 : volumeCache);
    }

    public double getFinishInSeconds() {
        return currentTrack.getFinishInSeconds();
    }
}
