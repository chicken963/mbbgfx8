package ru.orthodox.mbbg.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.controlsfx.control.RangeSlider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import ru.orthodox.mbbg.enums.ButtonType;
import ru.orthodox.mbbg.enums.WinCondition;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.model.Round;
import ru.orthodox.mbbg.services.PlayService;
import ru.orthodox.mbbg.services.model.AudioTrackService;
import ru.orthodox.mbbg.services.NewGameService;
import ru.orthodox.mbbg.services.ScreenService;
import ru.orthodox.mbbg.services.model.RoundService;
import ru.orthodox.mbbg.utils.ThreadUtils;
import ru.orthodox.mbbg.utils.ui.ActiveTableRowDealer;
import ru.orthodox.mbbg.utils.ui.EditTracksWorkspaceDealer;
import ru.orthodox.mbbg.utils.ui.ImageButtonCellFactoryProvider;
import ru.orthodox.mbbg.utils.ui.RangeSliderDealer;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static ru.orthodox.mbbg.utils.ThreadUtils.runTaskInSeparateThread;
import static ru.orthodox.mbbg.utils.TimeRepresentationConverter.toDoubleFormat;
import static ru.orthodox.mbbg.utils.TimeRepresentationConverter.toStringFormat;
import static ru.orthodox.mbbg.utils.ui.CustomFontDealer.setDefaultFont;

@Configurable
public class NewGameController {

    @FXML
    private Label enterNewGameNameLabel;
    @FXML
    private Label newGameLabel;
    @FXML
    private TextField newGameName;
    @FXML
    private TextField newRoundName;
    @FXML
    private ChoiceBox<String> rowsNumber;
    @FXML
    private ChoiceBox<String> columnsNumber;
    @FXML
    private ChoiceBox<String> firstPrizeCondition;
    @FXML
    private ChoiceBox<String> secondPrizeCondition;
    @FXML
    private ChoiceBox<String> thirdPrizeCondition;
    @FXML
    private Button addTracksButton;
    @FXML
    private Button deleteRound;
    @FXML
    private Button saveGame;
    @FXML
    private Button cancelCreation;
    @FXML
    private TableView<AudioTrack> tracksTable;
    @FXML
    private Tab tabSample;
    @FXML
    private TabPane tabPane;
    @Autowired
    private NewGameService newGameService;
    @Autowired
    private ScreenService screenService;
    @Autowired
    private AudioTrackService audioTrackService;
    @Autowired
    private RoundService roundService;
    @FXML
    private TableColumn<AudioTrack, String> artist;
    @FXML
    private TableColumn<AudioTrack, String> title;
    @FXML
    private TableColumn<AudioTrack, String> play;
    @FXML
    private TableColumn<AudioTrack, String> pause;
    @FXML
    private TableColumn<AudioTrack, String> stop;
    @FXML
    private TableColumn<AudioTrack, String> remove;
    @FXML
    private HBox sliderContainer;
    @FXML
    private Label currentTrackInfo;
    @FXML
    private Label currentTrackStartLabel;
    @FXML
    private Label currentTrackEndLabel;
    @FXML
    private Label currentSnippetRate;
    @FXML
    private Label currentSnippetLength;

    private EditTracksWorkspaceDealer editTracksWorkspaceDealer;


    private final List<String> prizeConditions = Arrays.stream(WinCondition.values())
            .map(WinCondition::getMessage)
            .collect(Collectors.toList());

    private List<AudioTrack> audioTracks;

    @PostConstruct
    private void setUp() {
        setDefaultFont(
                newGameLabel,
                enterNewGameNameLabel,
                addTracksButton,
                deleteRound,
                saveGame,
                cancelCreation);

        preconfigureRoundDimensions();
        preconfigureWinConditions();
    }

    @FXML
    private void openExplorerMenu(ActionEvent e) {
        FileChooser fileChooser = preconfigureFileChooser();
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(((Node) e.getSource()).getScene().getWindow());
        audioTracks = mapFilesToAudioTracks(selectedFiles);
//        currentTrack = audioTracks.get(0);
        tracksTable.getItems().setAll(audioTracks);
        this.editTracksWorkspaceDealer = EditTracksWorkspaceDealer.builder()
                .tracksTable(tracksTable)
                .artist(artist)
                .title(title)
                .play(play)
                .pause(pause)
                .stop(stop)
                .remove(remove)
                .sliderContainer(sliderContainer)
                .currentTrackInfo(currentTrackInfo)
                .currentTrackStartLabel(currentTrackStartLabel)
                .currentTrackEndLabel(currentTrackEndLabel)
                .currentSnippetRate(currentSnippetRate)
                .currentSnippetLength(currentSnippetLength)
                .audioTracks(audioTracks)
                .build();
        editTracksWorkspaceDealer.defineWorkspaceLogic();
    }

    @FXML
    private void saveNewGame() {
        Round round = Round.builder()
                .id(UUID.randomUUID())
                .name(newRoundName.getText())
                .firstStrikeCondition(WinCondition.findByMessage(firstPrizeCondition.getValue()))
                .secondStrikeCondition(WinCondition.findByMessage(secondPrizeCondition.getValue()))
                .thirdStrikeCondition(WinCondition.findByMessage(thirdPrizeCondition.getValue()))
                .width(Integer.parseInt(rowsNumber.getValue()))
                .height(Integer.parseInt(columnsNumber.getValue()))
                .tracksIds(audioTracks.stream().map(AudioTrack::getId).collect(Collectors.toList()))
                .build();
        audioTrackService.save(audioTracks);
        roundService.save(round);
    }

    @FXML
    private void cancelCreation() {
        screenService.activate("startmenu");
    }

    private FileChooser preconfigureFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Please select audio files");
        //TODO: uncomment and delete the subsequent line
//        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialDirectory(new File("F:\\myDocs\\Моя музыка"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Music files", "*.mp3", "*.aac", "*.wav", "*.flac")
        );
        return fileChooser;
    }

    private List<AudioTrack> mapFilesToAudioTracks(List<File> files) {
        return files.stream()
                .map(File::getAbsolutePath)
                .map(absPath -> audioTrackService.generateFromFile(absPath))
                .collect(Collectors.toList());
    }

    private void preconfigureRoundDimensions() {
        rowsNumber.getItems().addAll("4", "5", "6", "7", "8");
        columnsNumber.getItems().addAll("4", "5", "6", "7", "8", "9", "10", "11", "12");
    }

    private void preconfigureWinConditions() {
        firstPrizeCondition.getItems().addAll(prizeConditions);
        secondPrizeCondition.getItems().addAll(prizeConditions);
        thirdPrizeCondition.getItems().addAll(prizeConditions);
    }
}

