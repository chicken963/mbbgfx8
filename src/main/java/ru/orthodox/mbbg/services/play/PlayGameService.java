package ru.orthodox.mbbg.services.play;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.enums.WinConditionChangeMode;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.services.model.GameService;
import ru.orthodox.mbbg.services.model.RoundService;
import ru.orthodox.mbbg.services.play.blank.BlankProgressPreviewService;
import ru.orthodox.mbbg.services.play.blank.MiniaturesGridService;
import ru.orthodox.mbbg.services.popup.PopupAlerter;
import ru.orthodox.mbbg.utils.screen.ScreenService;

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
    @Autowired
    private ScreenService screenService;
    @Autowired
    private PopupAlerter popupAlerter;

    private List<Round> rounds = new ArrayList<>();
    private Round currentRound;

    private Label roundNameLabel;

    private Button previousRoundButton;
    private Button nextRoundButton;

    public void configureUIElements(Label roundNameLabel,
                                    Button nextRound,
                                    Button prevRound,
                                    Label songTitle,
                                    Label songProgressInSeconds,
                                    HBox volumeSliderContainer,
                                    ProgressBar songProgressBar,
                                    Button previousButton,
                                    Button nextButton,
                                    VBox audioTracksTable,
                                    HBox audioTracksTableRowTemplate,
                                    Button changeWinCondition,
                                    GridPane miniaturesProgressGrid,
                                    RowConstraints progressRowsConstraints,
                                    Button blankMiniatureTemplate,
                                    AnchorPane blankPreviewContainer,
                                    Label blankPreviewPlaceholder) {

        this.roundNameLabel = roundNameLabel;

        mediaPlayerService.configureUIElements(
                songTitle,
                songProgressInSeconds,
                volumeSliderContainer,
                songProgressBar,
                previousButton,
                nextButton);

        this.previousRoundButton = prevRound;
        this.nextRoundButton = nextRound;

        previousRoundButton.setDisable(true);

        blankProgressPreviewService.configureUIElements(
                blankPreviewContainer,
                blankPreviewPlaceholder);

        miniaturesGridService.configureUIElements(
                changeWinCondition,
                miniaturesProgressGrid,
                progressRowsConstraints,
                blankMiniatureTemplate);

        playlistTableService.configureUIElements(audioTracksTable, audioTracksTableRowTemplate);

    }

    public void render(Game game) {
        gameService.setModelFields(game);
        nextRoundButton.setDisable(rounds.size() == 1);

        rounds = game.getRounds();
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


    public void shiftCurrentTargetWinCondition() {
        miniaturesGridService.changeCurrentTargetWinCondition(WinConditionChangeMode.SHIFT);
    }

    public void setNextRoundTrack(Button sourceButton) {
        playlistTableService.setNextTrack(sourceButton);
    }

    public void switchToNextRound(Button sourceButton) {
        popupAlerter.invokeOkCancel(sourceButton.getScene().getWindow(),
                "Moving to the next round", "Are you sure you want to proceed? You'll be unable to return back to the current round.",
                event -> {
                    int roundIndexBeforeSwitching = rounds.indexOf(currentRound);
                    currentRound = rounds.get(roundIndexBeforeSwitching + 1);
                    if (rounds.indexOf(currentRound) == rounds.size() - 1) {
                        nextRoundButton.setDisable(true);
                    }
                    previousRoundButton.setDisable(false);
                    mediaPlayerService.pause();
                    renderRound(currentRound);
                    ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
                },
                event -> ((Stage) ((Button) event.getSource()).getScene().getWindow()).close());
    }

    public void switchToPrevRound() {
        int roundIndexBeforeSwitching = rounds.indexOf(currentRound);
        currentRound = rounds.get(roundIndexBeforeSwitching - 1);
        if (rounds.indexOf(currentRound) == 0) {
            previousRoundButton.setDisable(true);
        }
        nextRoundButton.setDisable(false);
        mediaPlayerService.pause();
        renderRound(currentRound);

    }

    public void emptyBlankPreview() {
        blankProgressPreviewService.emptyBlankPreview();
    }

    public void showBlankWithProgress(Button miniature) {
        blankProgressPreviewService.showBlankWithProgress(miniature);
    }

    public void backToMenu() {
        mediaPlayerService.stop();
        screenService.activate("startMenu");
    }
}
