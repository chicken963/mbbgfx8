package ru.orthodox.mbbg.services;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.orthodox.mbbg.enums.Direction;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.utils.NormalizedPathString;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.ListIterator;


@Component
public class PlayService {

    @Autowired
    private FileRecordService fileRecordService;

    @Getter
    private List<AudioTrack> queue;
    ListIterator<AudioTrack> queueIterator;
    @Getter
    private AudioTrack currentTrack;
    private double volumeCache;
    private boolean isMuted = false;
    @Getter
    private boolean firstTrackActive = true;
    @Getter
    private boolean lastTrackActive = false;
    private Media media;
    private MediaPlayer mediaPlayer;
    @Getter
    private boolean started;

    @PostConstruct
    private void postConstruct() {
        queue = findAllTracks();
        queueIterator = queue.listIterator();
        switchPlayerToTrack(Direction.FORWARD);
    }

    public List<AudioTrack> findAllTracks(){
        return fileRecordService.readAudioTracksInfo();
    }

    public void saveTrack(AudioTrack audioTrack){
        fileRecordService.write(audioTrack);
    }

    public AudioTrack play() {
        if (!started) {
            this.started = true;
        }
        mediaPlayer.play();
        return currentTrack;
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void stop() {
        mediaPlayer.stop();
        this.started = false;
    }

    public AudioTrack next() {
        mediaPlayer.stop();
        switchPlayerToTrack(Direction.FORWARD);
        this.started = true;
        mediaPlayer.play();
        return this.currentTrack;
    }

    public AudioTrack previous() {
        mediaPlayer.stop();
        switchPlayerToTrack(Direction.REVERSED);
        this.started = true;
        mediaPlayer.play();
        return currentTrack;
    }

    private void switchPlayerToTrack(Direction direction) {
        AudioTrack newTrack = currentTrack;
        while (newTrack == currentTrack && (queueIterator.hasNext() || queueIterator.hasPrevious())) {
            if (Direction.FORWARD.equals(direction)) {
                newTrack = queueIterator.next();
            } else {
                newTrack = queueIterator.previous();
            }
        }
        currentTrack = newTrack;
        firstTrackActive = currentTrack == queue.get(0);
        lastTrackActive = currentTrack == queue.get(queue.size() - 1);
        this.media = new Media(NormalizedPathString.of(currentTrack.getLocalPath()).getExpression());
        this.mediaPlayer = new MediaPlayer(media);
    }

    public void switchMute() {
        if (isMuted) {
            mediaPlayer.setVolume(volumeCache);
            this.isMuted = false;
        } else {
            this.volumeCache = mediaPlayer.getVolume();
            mediaPlayer.setVolume(0);
            this.isMuted = true;
        }
    }

    public void setVolume(double volume) {
        mediaPlayer.setVolume(volume * 0.01);
    }

    public double getCurrentTime() {
        return mediaPlayer.getCurrentTime().toSeconds();
    }

    public double getCurrentSongLength() {
        return media.getDuration().toSeconds();
    }

    public String getSongProgressAsString(double current, double maximum) {
        int currentMinutes = (int) Math.floor(current / 60);
        int currentSeconds = (int) (Math.floor(current % 60));
        int minutesInCurrentTrack = (int) Math.floor(maximum / 60);
        int secondsInCurrentTrack = (int) Math.floor(maximum % 60);
        StringBuilder sb = new StringBuilder()
                .append(formatTimeSegment(currentMinutes))
                .append(":")
                .append(formatTimeSegment(currentSeconds))
                .append("/")
                .append(formatTimeSegment(minutesInCurrentTrack))
                .append(":")
                .append(formatTimeSegment(secondsInCurrentTrack));
        return sb.toString();
    }

    private String formatTimeSegment(int value) {
        return String.format("%02d", value);
    }
}
