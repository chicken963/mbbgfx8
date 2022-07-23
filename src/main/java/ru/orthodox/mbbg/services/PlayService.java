package ru.orthodox.mbbg.services;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import lombok.Getter;
import ru.orthodox.mbbg.enums.Direction;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.utils.NormalizedPathString;

import java.util.List;
import java.util.ListIterator;


public class PlayService {

    @Getter
    private List<AudioTrack> queue;
    private ListIterator<AudioTrack> queueIterator;

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


    public PlayService(List<AudioTrack> queue) {
        this.queue = queue;
        this.queueIterator = queue.listIterator();
        if (queueIterator.hasNext()) {
            this.currentTrack = queueIterator.next();
            generateMediaPlayer(currentTrack);
        }
    }

    public AudioTrack play() {
        if (!isPaused) {
            this.setCurrentTrackStartRate();
        }
        mediaPlayer.play();
        isPaused = false;
        isStopped = false;
        return currentTrack;
    }

    public void pause() {
        mediaPlayer.pause();
        isPaused = true;
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPaused = false;
            isStopped = true;
        }
    }

    public AudioTrack next() {
        mediaPlayer.stop();
        shiftActiveTrack(Direction.FORWARD);
        generateMediaPlayer(currentTrack);
        mediaPlayer.play();
        return this.currentTrack;
    }

    public AudioTrack previous() {
        mediaPlayer.stop();
        shiftActiveTrack(Direction.REVERSED);
        generateMediaPlayer(currentTrack);
        mediaPlayer.play();
        return currentTrack;
    }

    private void shiftActiveTrack(Direction direction) {
        AudioTrack newTrack = currentTrack;
        while (newTrack == currentTrack) {
            if (Direction.FORWARD.equals(direction) && queueIterator.hasNext()) {
                newTrack = queueIterator.next();
            } else if (queueIterator.hasPrevious()) {
                newTrack = queueIterator.previous();
            }
        }
        currentTrack = newTrack;
    }

    public void switchMute() {
        if (muted) {
            this.muted = false;
        } else {
            this.volumeCache = 0;
            this.muted = true;
        }
        mediaPlayer.setVolume(volumeCache);
    }

    public void setVolume(double volume) {
        mediaPlayer.setVolume(volume * 0.01);
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

    public void setCurrentTrackStartRate(){
        mediaPlayer.setStartTime(Duration.seconds(currentTrack.getStartInSeconds()));
    }

    public boolean isFirstTrackActive() {
        return !queueIterator.hasPrevious();
    }

    public boolean isLastTrackActive() {
        return !queueIterator.hasNext();
    }

    public boolean isCurrentStopInPlayableRange(){
        double currentRate = mediaPlayer.getCurrentTime().toSeconds();
        return currentRate >= currentTrack.getStartInSeconds() && currentRate <= currentTrack.getFinishInSeconds();
    }

    private void generateMediaPlayer(AudioTrack audioTrack) {
        if (mediaPlayer != null) {
            volumeCache = mediaPlayer.getVolume();
        }
        this.media = new Media(NormalizedPathString.of(audioTrack.getLocalPath()));
        this.mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setStartTime(Duration.seconds(audioTrack.getStartInSeconds()));
        mediaPlayer.setStopTime(Duration.seconds(audioTrack.getFinishInSeconds()));
        mediaPlayer.setVolume(muted ? 0 : volumeCache);
    }
}
