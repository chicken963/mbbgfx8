package ru.orthodox.mbbg.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.model.Game;
import ru.orthodox.mbbg.model.Round;
import ru.orthodox.mbbg.repositories.AudioTrackRepository;
import ru.orthodox.mbbg.repositories.GamesRepository;
import ru.orthodox.mbbg.repositories.RoundRepository;
import ru.orthodox.mbbg.services.PlayService;
import ru.orthodox.mbbg.services.ScreenService;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import static ru.orthodox.mbbg.utils.ThreadUtils.runTaskInSeparateThread;
import static ru.orthodox.mbbg.utils.TimeRepresentationConverter.getSongProgressAsString;
import static ru.orthodox.mbbg.ui.CustomFontDealer.setDefaultFont;

@Configurable
public class PlayController {
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
    private TableView<AudioTrack> playlistTable;
    @FXML
    private TableColumn<AudioTrack, String> numberInPlaylist;
    @FXML
    private TableColumn<AudioTrack, String> artistInPlaylist;
    @FXML
    private TableColumn<AudioTrack, String> titleInPlaylist;
    @FXML
    private Button nextRound;
    @FXML
    private Button prevRound;
    @FXML
    private Button previousButton;
    @FXML
    private HBox roundsNavigator;
    @FXML
    private HBox nextRoundButtonContainer;
    @FXML
    private Button nextButton;
    private AudioTrack currentTrack;
    private int currentTrackNumber = 1;
    private List<AudioTracksTableRow> audioTracksTableRows;

    @Autowired
    private ScreenService screenService;
    @Autowired
    private AudioTrackRepository audioTrackRepository;
    @Autowired
    private GamesRepository gameService;
    @Autowired
    private RoundRepository roundRepository;

    private PlayService playService;
    private List<Round> rounds = new ArrayList<>();
    private Round currentRound;
    private static final String NOT_COVERED_PROGRESS_RANGE_COLOR = "#eee";
    private static final String COVERED_PROGRESS_RANGE_COLOR = "#999";
    private static final String COVERED_PROGRESS_RANGE_COLOR_INTERMEDIATE = "#aaa";
    private DecimalFormat decimalFormat;

    @Setter
    private Game game;

    @PostConstruct
    public void initialize() {
        setDefaultFont(songTitle, songProgressInSeconds, roundNameLabel);
        preconfigureDecimalFormatForCss();
    }

    public void render() {

        startTrackingTitle();
        if (audioTrackRepository != null && game != null) {
            rounds = gameService.findRoundsByGame(game);
            currentRound = rounds.get(0);
            renderRound(currentRound);
        }
    }

    private void renderRound(Round round) {
        roundNameLabel.setText(round.getName());
        List<AudioTrack> roundQueue = roundRepository.findAudioTracksByRound(round);
        this.playService = new PlayService(roundQueue);
        songTitle.setText(this.playService.getQueue().get(0).getTitle());
        fillPlaylistTable();
    }

    public void startPlaying() {
        currentTrack = playService.play();
        recalculateProgressBarBackgroundRange();
        updateActiveRow();
    }

    public void pausePlaying() {
        playService.pause();
    }

    public void nextTrack() {
        currentTrack = playService.next();
        recalculateProgressBarBackgroundRange();
        updateActiveRow();
    }


    public void previousTrack() {
        currentTrack = playService.previous();
        recalculateProgressBarBackgroundRange();
        updateActiveRow();
    }

    public void switchMute(ActionEvent actionEvent) {
        playService.switchMute();
    }

    public void updateVolume(MouseEvent actionEvent) {
        playService.setVolume(volumeSlider.getValue());
    }


    private void updateUIElementsState() {
        if (currentTrack != null) {
            songTitle.setText(currentTrack.getTitle());
        }
        if (playService != null) {
            double current = playService.getCurrentTime();
            double end = playService.getCurrentSongLength();

            songProgressInSeconds.setText(getSongProgressAsString(current, end));
            songProgressBar.setProgress(current / end);

            previousButton.setDisable(playService.isFirstTrackActive());
            nextButton.setDisable(playService.isLastTrackActive());
        }
    }

    private void checkAudioTrackSwitching() {
        if (playService != null && currentTrack != null) {

            double current = playService.getCurrentTime();
            double end = currentTrack.getFinishInSeconds();

            if (current / end >= 1) {
                currentTrack = playService.next();
                recalculateProgressBarBackgroundRange();
                updateActiveRow();
                playService.setVolume(volumeSlider.getValue());
            }
        }
    }

    private void startTrackingTitle() {
        runTaskInSeparateThread(this::updateUIElementsState);
        runTaskInSeparateThread(this::checkAudioTrackSwitching);
    }

    private void fillPlaylistTable() {
        numberInPlaylist.setCellValueFactory(
                new PropertyValueFactory<AudioTrack, String>(String.valueOf(++currentTrackNumber)));
        titleInPlaylist.setCellValueFactory(
                new PropertyValueFactory<AudioTrack, String>("title"));
        artistInPlaylist.setCellValueFactory(
                new PropertyValueFactory<AudioTrack, String>("artist"));
        playlistTable.getItems().setAll(playService.getQueue());
    }

    public void backToMenu(ActionEvent actionEvent) {
        screenService.activate("startmenu");
    }

    public void switchToNextRound(ActionEvent event) {
        int currentIndex = rounds.indexOf(currentRound);
        if (currentIndex < rounds.size() - 1) {
            Round nextRound = rounds.get(rounds.indexOf(currentRound) + 1);
            currentRound = nextRound;
            renderRound(currentRound);
        }
    }

    public void switchToPrevRound(ActionEvent event) {
        int currentIndex = rounds.indexOf(currentRound);
        if (currentIndex > 0) {
            Round prevRound = rounds.get(rounds.indexOf(currentRound) - 1);
            renderRound(prevRound);
        }
    }

    private void preconfigureDecimalFormatForCss() {
        if (decimalFormat == null) {
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
            decimalFormatSymbols.setDecimalSeparator('.');
            decimalFormat = new DecimalFormat("#0.00", decimalFormatSymbols);
        }
    }

    private void recalculateProgressBarBackgroundRange() {
        Node sliderBar = songProgressBar.lookup(".track");

        double rangeStripStart = currentTrack.getStartInSeconds() / currentTrack.getLengthInSeconds() * 100;
        double rangeStripStartBlurred = rangeStripStart - 2;

        double rangeStripMiddle = (currentTrack.getStartInSeconds() + currentTrack.getFinishInSeconds()) / 2 / currentTrack.getLengthInSeconds() * 100;

        double rangeStripEnd = currentTrack.getFinishInSeconds() / currentTrack.getLengthInSeconds() * 100;
        double rangeStripEndBlurred = Math.min(100, rangeStripEnd + 2);

        if (sliderBar != null) { //-fx-background-color: linear-gradient(to right, #eee 30%, #aaa 30%, #999 50%, #aaa 70%, #eee 70%)
            sliderBar.setStyle(new StringBuilder("-fx-background-color: ")
                    .append("linear-gradient(to right, ")
                    .append(NOT_COVERED_PROGRESS_RANGE_COLOR + " ")
                    .append(decimalFormat.format(rangeStripStartBlurred))
                    .append("%, ")
                    .append(COVERED_PROGRESS_RANGE_COLOR_INTERMEDIATE + " ")
                    .append(decimalFormat.format(rangeStripStart))
                    .append("%, ")
                    .append(COVERED_PROGRESS_RANGE_COLOR + " ")
                    .append(decimalFormat.format(rangeStripMiddle))
                    .append("%, ")
                    .append(COVERED_PROGRESS_RANGE_COLOR_INTERMEDIATE + " ")
                    .append(decimalFormat.format(rangeStripEnd))
                    .append("%, ")
                    .append(NOT_COVERED_PROGRESS_RANGE_COLOR + " ")
                    .append(decimalFormat.format(rangeStripEndBlurred))
                    .append("%)")
                    .toString());
        }
    }

    private void updateActiveRow() {
        playlistTable.lookupAll(".table-row-cell").forEach(row -> row.getStyleClass().remove("highlighted"));
        playlistTable.lookupAll(".table-row-cell").stream()
                .map(node -> (TableRow<AudioTrack>) node)
                .filter(row -> currentTrack.equals(row.getItem()))
                .forEach(row -> row.getStyleClass().add("highlighted"));
    }

    @Data
    private class AudioTracksTableRow {
        private final List<Node> cells;
        private AudioTrack audioTrack;

        public boolean isActive() {
            return audioTrack == currentTrack;
        }

        public void makeActive() {
            cells.forEach(cell -> cell.getStyleClass().add("highlighted"));
        }

        public void makeInactive() {
            cells.forEach(cell -> cell.getStyleClass().remove("highlighted"));
        }
    }
}