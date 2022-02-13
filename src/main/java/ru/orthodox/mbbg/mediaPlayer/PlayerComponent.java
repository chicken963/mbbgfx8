package ru.orthodox.mbbg.mediaPlayer;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.Getter;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.utils.NormalizedPathString;

@Deprecated
public class PlayerComponent {

    private Media media;
    private MediaPlayer mediaPlayer;

    @Getter
    private boolean started;

    public PlayerComponent(AudioTrack audioTrack) {
        this.media = new Media(NormalizedPathString.of(audioTrack.getLocalPath()).getExpression());
        this.mediaPlayer = new MediaPlayer(media);
    }

    public void play() {
        this.started = true;
        mediaPlayer.play();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void resume() {
        mediaPlayer.play();
    }

    public void stop(){
        mediaPlayer.stop();
    }


    public void setVolume(double volume) {
        mediaPlayer.setVolume(volume);
    }

    public double getVolume() {
        return mediaPlayer.getVolume();
    }
}
