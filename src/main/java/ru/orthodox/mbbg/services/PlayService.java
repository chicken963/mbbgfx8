package ru.orthodox.mbbg.services;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.orthodox.mbbg.enums.Direction;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.services.model.AudioTrackService;
import ru.orthodox.mbbg.utils.NormalizedPathString;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.ListIterator;


@Component
public class PlayService {

    @Getter
    private List<AudioTrack> queue;
    ListIterator<AudioTrack> queueIterator;

    @Getter
    private AudioTrack currentTrack;

    private double volumeCacheForSwitching = 1;
    private double volumeCacheForMute;
    private boolean muted = false;
    private Media media;
    private MediaPlayer mediaPlayer;

    @Getter
    private boolean firstTrackActive = true;
    @Getter
    private boolean lastTrackActive = false;

    public AudioTrack play() {
        mediaPlayer.play();
        return currentTrack;
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public AudioTrack next() {
        mediaPlayer.stop();
        switchPlayerToTrack(Direction.FORWARD);
        mediaPlayer.play();
        return this.currentTrack;
    }

    public AudioTrack previous() {
        mediaPlayer.stop();
        switchPlayerToTrack(Direction.REVERSED);
        mediaPlayer.play();
        return currentTrack;
    }

    private void switchPlayerToTrack(Direction direction) {
        AudioTrack newTrack = currentTrack;
        while (newTrack == currentTrack) {
            if (Direction.FORWARD.equals(direction) && queueIterator.hasNext()) {
                newTrack = queueIterator.next();
            } else if (queueIterator.hasPrevious()) {
                newTrack = queueIterator.previous();
            }
        }
        currentTrack = newTrack;
        firstTrackActive = currentTrack == queue.get(0);
        lastTrackActive = currentTrack == queue.get(queue.size() - 1);
        if (mediaPlayer != null) {
            volumeCacheForSwitching = mediaPlayer.getVolume();
        }
        this.media = new Media(NormalizedPathString.of(currentTrack.getLocalPath()));
        this.mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setStartTime(Duration.seconds(currentTrack.getStartInSeconds()));
        mediaPlayer.setStopTime(Duration.seconds(currentTrack.getFinishInSeconds()));
        mediaPlayer.setVolume(volumeCacheForSwitching);
    }

    public void switchMute() {
        if (muted) {
            mediaPlayer.setVolume(volumeCacheForMute);
            this.muted = false;
        } else {
            this.volumeCacheForMute = mediaPlayer.getVolume();
            mediaPlayer.setVolume(0);
            this.muted = true;
        }
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

    public void resetQueue(List<AudioTrack> audioTracks) {
        stop();
        this.queue = audioTracks;
        this.queueIterator = queue.listIterator();
        switchPlayerToTrack(Direction.FORWARD);
    }
}
