package ru.orthodox.mbbg.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.services.play.PlayGameService;
import ru.orthodox.mbbg.utils.screen.ScreenService;

import javax.annotation.PostConstruct;

import static ru.orthodox.mbbg.utils.common.CustomFontDealer.setDefaultFont;

@Configurable
public class PlayController {
    @FXML
    private Button prevRound;
    @FXML
    private Button nextRound;
    @FXML
    private Label artistHeader;
    @FXML
    private Label titleHeader;
    @FXML
    private Label blankPreviewPlaceholder;
    @FXML
    private AnchorPane blankPreview;
    @FXML
    private Label blankItem;
    @FXML
    private GridPane miniaturesProgressGrid;
    @FXML
    private RowConstraints progressRowsConstraints;
    @FXML
    private Label songTitle;
    @FXML
    private Label songProgressInSeconds;
    @FXML
    private Label roundNameLabel;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ProgressBar songProgressBar;
    @FXML
    private VBox audioTracksTable;
    @FXML
    private HBox audioTracksTableRowTemplate;
    @FXML
    private Button previousButton;
    @FXML
    private Button blankMiniature;
    @FXML
    private Button changeWinCondition;
    @FXML
    private Button nextButton;

    @Autowired
    private ScreenService screenService;
    @Autowired
    private PlayGameService playGameService;

    @PostConstruct
    public void initialize() {
        setDefaultFont(
                prevRound,
                nextRound,
                songTitle,
                songProgressInSeconds,
                roundNameLabel,
                changeWinCondition,
                artistHeader,
                titleHeader);
        if (playGameService != null) {
            playGameService.configureUIElements(
                    roundNameLabel,
                    songTitle,
                    songProgressInSeconds,
                    volumeSlider,
                    songProgressBar,
                    previousButton,
                    nextButton,
                    audioTracksTable,
                    audioTracksTableRowTemplate,
                    changeWinCondition,
                    miniaturesProgressGrid,
                    progressRowsConstraints,
                    blankMiniature,
                    blankPreview,
                    blankPreviewPlaceholder,
                    blankItem);
        }
    }

    public void renderNewGame(Game game) {
        playGameService.render(game);
    }

    @FXML
    private void play() {
        playGameService.play();
    }

    @FXML
    private void pausePlaying() {
        playGameService.pause();
    }

    @FXML
    private void switchToNextTrack() {
        playGameService.switchToNextTrack();
    }

    @FXML
    private void switchToPreviousTrack() {
        playGameService.switchToPreviousTrack();
    }

    @FXML
    private void switchMute() {
        playGameService.switchMute();
    }

    @FXML
    private void updateVolume() {
        playGameService.updateVolume(volumeSlider.getValue());
    }

    @FXML
    private void backToMenu(ActionEvent actionEvent) {
        screenService.activate("startMenu");
    }

    @FXML
    private void switchToNextRound() {
        playGameService.switchToNextRound();

    }

    @FXML
    private void switchToPrevRound() {
        playGameService.switchToPrevRound();
    }

    @FXML
    private void increaseWinLevel() {
        playGameService.shiftCurrentTargetWinCondition();
        changeWinCondition.setDisable(true);
    }

    @FXML
    private void showBlankWithProgress(MouseEvent event) {
        playGameService.showBlankWithProgress((Button) event.getSource());
    }

    @FXML
    private void emptyBlankPreview() {
        playGameService.emptyBlankPreview();
    }

    @FXML
    private void setRoundNextTrack(MouseEvent mouseEvent) {
        Button sourceButton = (Button) mouseEvent.getSource();
        playGameService.setNextRoundTrack(sourceButton);
    }
}