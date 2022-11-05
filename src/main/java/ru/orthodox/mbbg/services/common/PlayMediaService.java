package ru.orthodox.mbbg.services.common;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.utils.common.NormalizedPathString;

@Slf4j
@Service
public class PlayMediaService {
    @Getter
    private AudioTrack currentTrack;

    private double volumeCache;

    private double volume = 1;

    private boolean muted = false;
    @Getter
    private boolean isPaused = false;
    @Getter
    private boolean isStopped = true;
    private Media media;

    private MediaPlayer mediaPlayer;
    @Setter
    private boolean upperBoundChanged = false;

    public void play(AudioTrack audioTrack) {
        if (thisTrackIsAlreadyBeingPlayed(audioTrack)) {
            return;
        }
        if (switchingFromAnotherTrack(audioTrack) && mediaPlayer != null) {
            mediaPlayer.stop();
        }
        if (audioTrack != currentTrack) {
            currentTrack = audioTrack;
            generateMediaPlayer(audioTrack);
            isStopped = false;
        } else if (boundsWereChangedOutOfRange()) {
            mediaPlayer.stop();
            generateMediaPlayer(audioTrack);
            isStopped = false;
        }
        if (isStopped) {
            generateMediaPlayer(audioTrack);
        } else if (upperBoundChanged) {
            double currentStop = mediaPlayer.getCurrentTime().toSeconds();
            if (currentStop > 0) {
                generateMediaPlayer(audioTrack);
                mediaPlayer.setStartTime(Duration.seconds(currentStop));
            }
        }
        mediaPlayer.play();
        isPaused = false;
        isStopped = false;
    }

    private boolean boundsWereChangedOutOfRange() {
        return isPaused && mediaPlayer != null && !isCurrentStopInPlayableRange();
    }

    private boolean thisTrackIsAlreadyBeingPlayed(AudioTrack audioTrack) {
        return currentTrack == audioTrack && !isPaused && !isStopped;
    }

    private boolean switchingFromAnotherTrack(AudioTrack audioTrack) {
        return currentTrack != null && currentTrack != audioTrack;
    }

    public void pause(AudioTrack audioTrack) {
        if (audioTrack != null && mediaPlayer != null && audioTrack == currentTrack) {
            mediaPlayer.pause();
            isPaused = true;
        }
    }


    public void stop() {
        stop(getCurrentTrack());
    }

    public void stop(AudioTrack audioTrack) {
        if (audioTrack != null && audioTrack == currentTrack) {
            mediaPlayer.stop();
            isPaused = false;
            isStopped = true;
        }
    }


    public void switchMute() {
        if (this.muted) {
            volume = volumeCache;
        } else {
            volumeCache = volume;
            volume = 0;
        }
        this.muted = !this.muted;
        mediaPlayer.setVolume(volume);
    }

    public void setVolume(double newValue) {
        this.volume = newValue * 0.01;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    public int getVolume() {
        return (int) (volume * 100);
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
        return currentRate > currentTrack.getStartInSeconds() && currentRate < currentTrack.getFinishInSeconds();
    }

    protected void generateMediaPlayer(AudioTrack audioTrack) {
        this.media = new Media(NormalizedPathString.of(audioTrack.getLocalPath()));
        try {
            this.mediaPlayer = new MediaPlayer(media);
        } catch (NullPointerException e) {
            log.error("Failed to generate media player for audiotrack {}", audioTrack);
            return;
        }
        mediaPlayer.setStartTime(Duration.seconds(audioTrack.getStartInSeconds()));
        mediaPlayer.setStopTime(Duration.seconds(audioTrack.getFinishInSeconds()));
        mediaPlayer.setVolume(volume);
    }

    public double getFinishInSeconds() {
        return currentTrack.getFinishInSeconds();
    }
}
