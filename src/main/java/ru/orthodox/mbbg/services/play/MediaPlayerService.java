package ru.orthodox.mbbg.services.play;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.events.play.CurrentTrackChangedEvent;
import ru.orthodox.mbbg.events.play.NextTrackChangeRequestedByUserEvent;
import ru.orthodox.mbbg.events.play.NextTrackChangedEvent;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.model.proxy.create.VolumeSlider;
import ru.orthodox.mbbg.services.common.PlayMediaService;
import ru.orthodox.mbbg.services.common.VolumeSliderService;
import ru.orthodox.mbbg.services.model.RoundService;

import java.util.ArrayList;
import java.util.List;

import static ru.orthodox.mbbg.utils.common.ThreadUtils.runTaskInSeparateThread;
import static ru.orthodox.mbbg.utils.common.TimeRepresentationConverter.getSongProgressAsString;

@Service
public class MediaPlayerService implements ApplicationListener<NextTrackChangeRequestedByUserEvent>  {

    @Autowired
    private RoundService roundService;
    @Autowired
    private ProgressBarBackgroundService progressBarBackgroundService;
    @Autowired
    private PlayMediaService playService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private PlaylistTableService playlistTableService;
    @Autowired
    private VolumeSliderService volumeSliderService;

    private List<AudioTrack> roundQueue;
    private List<AudioTrack> roundHistory;

    private Label songTitle;
    private Label songProgressInSeconds;

    private ProgressBar songProgressBar;

    private Button previousTrackInPlayerButton;
    private Button nextTrackInPlayerButton;

    private Round activeRound;



    public void configureUIElements(
            Label songTitle,
            Label songProgressInSeconds,
            HBox volumeSliderContainer,
            ProgressBar songProgressBar,
            Button previousButton,
            Button nextButton) {
        this.songTitle = songTitle;
        this.songProgressInSeconds = songProgressInSeconds;
        VolumeSlider volumeSlider = volumeSliderService.createNewSlider();
        volumeSliderContainer.getChildren().setAll(volumeSlider.getRoot());
        this.previousTrackInPlayerButton = previousButton;
        this.nextTrackInPlayerButton = nextButton;
        this.songProgressBar = songProgressBar;
        progressBarBackgroundService.configureUIElements(songProgressBar);
    }

    public void setActiveRound(Round round) {
        this.activeRound = round;

        this.roundQueue = round.getAudioTracks();
        this.roundHistory = new ArrayList<>();

        updateNextTrack(roundQueue.size() > 0 ? roundQueue.get(0) : null);

        songTitle.setText("Round not started");
        songProgressInSeconds.setText("00:00/00:00");
        previousTrackInPlayerButton.setDisable(activeRound.getPreviousTrack() == null);
        nextTrackInPlayerButton.setDisable(activeRound.getNextTrack() == null);

        runTasksInSeparateThreads();
    }

    private void runTasksInSeparateThreads() {
        runTaskInSeparateThread(this::updateUIElementsState, "update-progress-bar");
        runTaskInSeparateThread(this::checkAudioTrackSwitching, "check-audiotrack-switching");
    }

    private void updateUIElementsState() {
        if (activeRound.getCurrentTrack() != null) {
            double current = playService.getCurrentTime();
            double end = playService.getCurrentSongLength();

            songProgressInSeconds.setText(getSongProgressAsString(current, end));
            songProgressBar.setProgress(current / end);
        }
    }

    private void checkAudioTrackSwitching() {
        if (activeRound.getCurrentTrack() != null) {

            double current = playService.getCurrentTime();
            double end = playService.getFinishInSeconds();

            if (current / end >= 1 && activeRound.getNextTrack() != null) {
                switchToNextTrack();
            }
        }
    }

    public void updateVolume(double newValue) {
        playService.setVolume(newValue);
    }

    public void switchMute() {
        playService.switchMute();
    }

    public void play() {
        if (activeRound.getCurrentTrack() == null && activeRound.getNextTrack() != null) {

            updateCurrentTrack(activeRound.getNextTrack());

            if (playlistTableService.findFirstActiveTrack().isPresent()) {
                updateNextTrack(playlistTableService.findFirstActiveTrack().get());
            } else {
                updateNextTrack(null);
            }

            playService.play(activeRound.getCurrentTrack());
            progressBarBackgroundService.recalculateProgressBarBackgroundRange(activeRound.getCurrentTrack());

        } else if (playService.isPaused()) {
            playService.play(activeRound.getCurrentTrack());
        }
    }

    public void pause() {
        playService.pause(activeRound.getCurrentTrack());
    }

    public void switchToPreviousTrack() {
        playService.stop(activeRound.getCurrentTrack());

        activeRound.getPlayedAudiotracks().remove(activeRound.getCurrentTrack());

        updateNextTrack(activeRound.getCurrentTrack());
        updateCurrentTrack(activeRound.getPreviousTrack());
        updatePreviousTrack(roundHistory.size() > 0 ? roundHistory.get(roundHistory.size() - 1) : null);

        roundHistory.remove(roundHistory.size() - 1);

        playService.play(activeRound.getCurrentTrack());

        progressBarBackgroundService.recalculateProgressBarBackgroundRange(activeRound.getCurrentTrack());
    }

    public void switchToNextTrack() {
        playService.stop(activeRound.getCurrentTrack());

        roundHistory.add(activeRound.getPreviousTrack());

        updatePreviousTrack(activeRound.getCurrentTrack());
        updateCurrentTrack(activeRound.getNextTrack());
        updateNextTrack(playlistTableService.findFirstActiveTrack().isPresent()
                        ? playlistTableService.findFirstActiveTrack().get()
                        : null);

        playService.play(activeRound.getCurrentTrack());

        progressBarBackgroundService.recalculateProgressBarBackgroundRange(activeRound.getCurrentTrack());
    }

    @Override
    public void onApplicationEvent(NextTrackChangeRequestedByUserEvent nextTrackChangeRequestedByUserEvent) {
        AudioTrack newNextTrack = nextTrackChangeRequestedByUserEvent.getAudioTrack();
        updateNextTrack(newNextTrack);
    }

    private void updateNextTrack(AudioTrack audioTrack) {
        activeRound.setNextTrack(audioTrack);
        nextTrackInPlayerButton.setDisable(audioTrack == null);
        eventPublisher.publishEvent(new NextTrackChangedEvent(this));
    }

    private void updateCurrentTrack(AudioTrack audioTrack) {
        activeRound.getPlayedAudiotracks().add(audioTrack);
        activeRound.setCurrentTrack(audioTrack);

        songTitle.setText(audioTrack.getTitle());
        previousTrackInPlayerButton.setDisable(activeRound.getPreviousTrack() == null);

        eventPublisher.publishEvent(new CurrentTrackChangedEvent(this));
    }

    private void updatePreviousTrack(AudioTrack audioTrack) {
        activeRound.setPreviousTrack(audioTrack);
    }

    public void stop() {
        playService.stop(activeRound.getCurrentTrack());
    }
}
