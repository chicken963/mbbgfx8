package ru.orthodox.mbbg.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.services.PlayService;

import javax.annotation.PostConstruct;
import java.util.Timer;
import java.util.TimerTask;

@Configurable
public class PlayController {
    @FXML
    private Label songTitle;
    @FXML
    private Label songProgressInSeconds;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ProgressBar songProgressBar;
    @FXML
    private TableView<AudioTrack> playlistTable;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
    private Timer timer;
    private TimerTask progressTask;
    private String currentSongTitle;
    private AudioTrack currentTrack;
    private double currentTimeInSeconds;
    private double trackLengthInSeconds;

    @Autowired
    private PlayService playService;

    @PostConstruct
    public void initialize() {
        startTrackingTitle();
        if(playService != null){
            this.songTitle.setText(this.playService.getQueue().get(0).getTitle());
        }
    }

    public void startPlaying() {
        currentTrack = playService.play();
        updateMediaView();
    }

    public void pausePlaying() {
        playService.pause();
    }

    public void nextTrack() {
        currentTrack = playService.next();
        updateMediaView();
    }


    public void previousTrack() {
        currentTrack = playService.previous();
        updateMediaView();
    }

    public void switchMute(ActionEvent actionEvent) {
        playService.switchMute();
    }

    public void updateVolume(MouseEvent actionEvent) {
        playService.setVolume(volumeSlider.getValue());
    }

    public void beginTimer() {
        timer = new Timer();
        progressTask = new TimerTask() {

            public void run() {
                currentTimeInSeconds = playService.getCurrentTime();
                trackLengthInSeconds = playService.getCurrentSongLength();
                if (currentTimeInSeconds / trackLengthInSeconds == 1) {
                    try{
                        currentTrack = playService.next();
                        playService.setVolume(volumeSlider.getValue());
                    } finally {
                        cancelTimer();
                        currentTimeInSeconds = playService.getCurrentTime();
                        trackLengthInSeconds = playService.getCurrentSongLength();
                    }
                }
            }
        };
        timer.scheduleAtFixedRate(progressTask, 0, 100);
    }

    private void updateUIElemntsInUIStream() {
        if (currentTrack != null) {
            this.songTitle.setText(currentTrack.getTitle());
        }
        if (playService != null){
            this.songProgressInSeconds.setText(playService.getSongProgressAsString(currentTimeInSeconds, trackLengthInSeconds));
            this.previousButton.setDisable(playService.isFirstTrackActive());
            this.nextButton.setDisable(playService.isLastTrackActive());
            double current = playService.getCurrentTime();
            double end = playService.getCurrentSongLength();
            songProgressBar.setProgress(current / end);
        }
    }

    private void updateMediaView() {
        if (playService != null) {
            playService.setVolume(volumeSlider.getValue());
            songProgressBar.setProgress(0);
            beginTimer();
        }
    }

    private void cancelTimer() {
        timer.cancel();
    }

    private void startTrackingTitle(){
        Thread thread = new Thread(() -> {
            Runnable updater = this::updateUIElemntsInUIStream;
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
                Platform.runLater(updater);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}