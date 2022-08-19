package ru.orthodox.mbbg.services.play;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.services.model.GameService;
import ru.orthodox.mbbg.services.model.RoundService;
import ru.orthodox.mbbg.services.play.blank.BlankProgressPreviewService;
import ru.orthodox.mbbg.services.play.blank.MiniaturesGridService;

import java.util.ArrayList;
import java.util.List;

@Setter
@Service
public class PlayGameService {
    @Autowired
    private RoundService roundService;
    @Autowired
    private GameService gameService;
    @Autowired
    private MediaPlayerService mediaPlayerService;
    @Autowired
    private MiniaturesGridService miniaturesGridService;
    @Autowired
    private BlankProgressPreviewService blankProgressPreviewService;
    @Autowired
    private PlaylistTableService playlistTableService;

    private List<Round> rounds = new ArrayList<>();
    private Round currentRound;

    private Label roundNameLabel;

    public void configureUIElements(Label roundNameLabel,
                              Label songTitle,
                              Label songProgressInSeconds,
                              Slider volumeSlider,
                              ProgressBar songProgressBar,
                              Button previousButton,
                              Button nextButton,
                              VBox audioTracksTable,
                              HBox audioTracksTableRowTemplate,
                              Button changeWinCondition,
                              GridPane miniaturesProgressGrid,
                              RowConstraints progressRowsConstraints,
                              Button blankMiniatureTemplate,
                              AnchorPane blankPreview,
                              Label blankPreviewPlaceholder,
                              Label blankItemTemplate) {

        this.roundNameLabel = roundNameLabel;

        mediaPlayerService.configureUIElements(
                songTitle,
                songProgressInSeconds,
                volumeSlider,
                songProgressBar,
                previousButton,
                nextButton);
        
        blankProgressPreviewService.configureUIElements(
                blankPreview,
                blankPreviewPlaceholder,
                blankItemTemplate);
        
        miniaturesGridService.configureUIElements(
                changeWinCondition,
                miniaturesProgressGrid,
                progressRowsConstraints,
                blankMiniatureTemplate);
        
        playlistTableService.configureUIElements(audioTracksTable, audioTracksTableRowTemplate);

    }
    
    public void render(Game game) {
        rounds = gameService.findRoundsOfGame(game);
        rounds.forEach(roundService::setModelFields);
        currentRound = rounds.get(0);
        renderRound(currentRound);
    }

    private void renderRound(Round round) {
        roundNameLabel.setText(round.getName());

        playlistTableService.setActiveRound(round);
        blankProgressPreviewService.setActiveRound(round);
        miniaturesGridService.setActiveRound(round);
        mediaPlayerService.setActiveRound(round);
    }

    public void play() {
        mediaPlayerService.play();
    }

    public void pause() {
        mediaPlayerService.pause();
    }

    public void switchToNextTrack() {
        mediaPlayerService.switchToNextTrack();
    }

    public void switchToPreviousTrack() {
        mediaPlayerService.switchToPreviousTrack();
    }

    public void switchMute() {
        mediaPlayerService.switchMute();
    }

    public void updateVolume(double newValue) {
        mediaPlayerService.updateVolume(newValue);
    }


    public void shiftCurrentTargetWinCondition() {
        miniaturesGridService.shiftCurrentTargetWinCondition();
    }

    public void setNextRoundTrack(Button sourceButton) {
        playlistTableService.setNextTrack(sourceButton);
    }

    public void switchToNextRound() {
        int currentIndex = rounds.indexOf(currentRound);
        if (currentIndex < rounds.size() - 1) {
            currentRound = rounds.get(rounds.indexOf(currentRound) + 1);
            renderRound(currentRound);
        }
    }
    
    public void switchToPrevRound() {
        int currentIndex = rounds.indexOf(currentRound);
        if (currentIndex > 0) {
            currentRound = rounds.get(rounds.indexOf(currentRound) - 1);
            renderRound(currentRound);
        }
    }

    public void emptyBlankPreview() {
        blankProgressPreviewService.emptyBlankPreview();
    }

    public void showBlankWithProgress(Button miniature) {
        blankProgressPreviewService.showBlankWithProgress(miniature);
    }
}
