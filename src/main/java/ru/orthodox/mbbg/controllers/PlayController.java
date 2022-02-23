package ru.orthodox.mbbg.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.services.PlayService;

import javax.annotation.PostConstruct;

import static ru.orthodox.mbbg.utils.ThreadUtils.runTaskInSeparateThread;

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
    private TableColumn<AudioTrack, String> numberInPlaylist;
    @FXML
    private TableColumn<AudioTrack, String> artistInPlaylist;
    @FXML
    private TableColumn<AudioTrack, String> titleInPlaylist;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
    private AudioTrack currentTrack;
    private int currentTrackNumber = 1;

    @Autowired
    private PlayService playService;

    @PostConstruct
    public void initialize() {
        startTrackingTitle();
        if (playService != null) {
            initializeCurrentSongTitle();
            fillPlaylistTable();
        }
    }

    public void startPlaying() {
        currentTrack = playService.play();
        updateVolume(null);
    }

    public void pausePlaying() {
        playService.pause();
    }

    public void nextTrack() {
        currentTrack = playService.next();
        updateVolume(null);
    }


    public void previousTrack() {
        currentTrack = playService.previous();
        updateVolume(null);
    }

    public void switchMute(ActionEvent actionEvent) {
        playService.switchMute();
    }

    public void updateVolume(MouseEvent actionEvent) {
        playService.setVolume(volumeSlider.getValue());
    }

    private void initializeCurrentSongTitle() {
        songTitle.setText(this.playService.getQueue().get(0).getTitle());
    }

    private void updateQueueProgress() {
        if (currentTrack != null) {
            songTitle.setText(currentTrack.getTitle());
        }
        if (playService != null) {
            double current = playService.getCurrentTime();
            double end = playService.getCurrentSongLength();

            songProgressInSeconds.setText(playService.getSongProgressAsString(current, end));
            songProgressBar.setProgress(current / end);
            if (current / end >= 1) {
                currentTrack = playService.next();
                playService.setVolume(volumeSlider.getValue());
            }
            previousButton.setDisable(playService.isFirstTrackActive());
            nextButton.setDisable(playService.isLastTrackActive());
        }
    }

    private void startTrackingTitle() {
        runTaskInSeparateThread(this::updateQueueProgress);
    }

    private void fillPlaylistTable() {
        numberInPlaylist.setCellValueFactory(
                new PropertyValueFactory<AudioTrack, String>(String.valueOf(++currentTrackNumber)));
        titleInPlaylist.setCellValueFactory(
                new PropertyValueFactory<AudioTrack, String>("title"));
        artistInPlaylist.setCellValueFactory(
                new PropertyValueFactory<AudioTrack, String>("artist"));
        playlistTable.getItems().setAll(playService.findAllTracks());
    }
}