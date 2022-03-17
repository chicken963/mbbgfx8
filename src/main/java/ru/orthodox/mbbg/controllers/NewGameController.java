package ru.orthodox.mbbg.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
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
import ru.orthodox.mbbg.utils.NormalizedPathString;
import ru.orthodox.mbbg.utils.ui.ActiveTableRowDealer;
import ru.orthodox.mbbg.utils.ui.ImageButtonCellFactoryProvider;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.orthodox.mbbg.utils.TimeRepresentationConverter.toDoubleFormat;
import static ru.orthodox.mbbg.utils.TimeRepresentationConverter.toStringFormat;

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
    @Autowired
    private NewGameService newGameService;
    @Autowired
    private ScreenService screenService;
    @Autowired
    private AudioTrackService audioTrackService;
    @Autowired
    private RoundService roundService;
    @Autowired
    private PlayService playService;
    @Autowired
    private ImageButtonCellFactoryProvider imageButtonCellFactoryProvider;
    @Autowired
    private ActiveTableRowDealer activeTableRowDealer;
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

    private AudioTrack currentTrack;


    private final List<String> prizeConditions = Arrays.stream(WinCondition.values())
            .map(WinCondition::getMessage)
            .collect(Collectors.toList());

    private List<AudioTrack> audioTracks;

    @PostConstruct
    private void setUp() {
        newGameLabel.setFont(Font.loadFont(NormalizedPathString.of("src\\main\\resources\\fonts\\AntykwaTorunskaMed-Regular.ttf"), 32));
        enterNewGameNameLabel.setFont(Font.loadFont(NormalizedPathString.of("src\\main\\resources\\fonts\\AntykwaTorunskaMed-Regular.ttf"), 20));
        addTracksButton.setFont(Font.loadFont(NormalizedPathString.of("src\\main\\resources\\fonts\\AntykwaTorunskaMed-Regular.ttf"), 14));
        deleteRound.setFont(Font.loadFont(NormalizedPathString.of("src\\main\\resources\\fonts\\AntykwaTorunskaMed-Regular.ttf"), 14));
        saveGame.setFont(Font.loadFont(NormalizedPathString.of("src\\main\\resources\\fonts\\AntykwaTorunskaMed-Regular.ttf"), 16));
        cancelCreation.setFont(Font.loadFont(NormalizedPathString.of("src\\main\\resources\\fonts\\AntykwaTorunskaMed-Regular.ttf"), 16));
        rowsNumber.getItems().addAll("4", "5", "6", "7", "8");
        columnsNumber.getItems().addAll("4", "5", "6", "7", "8", "9", "10", "11", "12");
        firstPrizeCondition.getItems().addAll(prizeConditions);
        secondPrizeCondition.getItems().addAll(prizeConditions);
        thirdPrizeCondition.getItems().addAll(prizeConditions);
    }

    @FXML
    private void openExplorerMenu(ActionEvent e) {
        FileChooser fileChooser = preconfigureFileChooser();
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(((Node) e.getSource()).getScene().getWindow());
        audioTracks = mapFilesToAudioTracks(selectedFiles);
        currentTrack = audioTracks.get(0);
        currentTrackInfo.setText(currentTrack.getArtist() + " - " + currentTrack.getTitle());
        tracksTable.getItems().setAll(audioTracks);
        defineTableLogic();
        prepareRangeSlider();
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

    @FXML
    private void prepareRangeSlider() {
        RangeSlider slider = new RangeSlider(
                0,
                currentTrack.getLengthInSeconds(),
                currentTrack.getStartInSeconds(),
                currentTrack.getFinishInSeconds()
        );
        //Setting the slider properties
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setPrefWidth(600);
        slider.setMinWidth(600);
        slider.setMajorTickUnit(60);
        slider.setBlockIncrement(5);
        slider.setLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return toStringFormat(object.doubleValue());
            }

            @Override
            public Number fromString(String string) {
                return new Number() {
                    @Override
                    public int intValue() {
                        return (int) toDoubleFormat(string);
                    }

                    @Override
                    public long longValue() {
                        return (long) toDoubleFormat(string);
                    }

                    @Override
                    public float floatValue() {
                        return (float) toDoubleFormat(string);
                    }

                    @Override
                    public double doubleValue() {
                        return toDoubleFormat(string);
                    }
                };
            }
        });
        slider.highValueProperty().addListener((observable, oldValue, newValue) -> {
            String currentTrackFinish = toStringFormat(newValue.doubleValue());
            currentTrack.setFinishInSeconds(newValue.doubleValue());
            currentTrackEndLabel.setText(currentTrackFinish);
        });
        slider.lowValueProperty().addListener((observable, oldValue, newValue) -> {
            String currentTrackStart = toStringFormat(newValue.doubleValue());
            currentTrack.setStartInSeconds(newValue.doubleValue());
            currentTrackStartLabel.setText(currentTrackStart);
        });
        //VBox to arrange circle and the slider
        sliderContainer.getChildren().clear();
        sliderContainer.getChildren().addAll(slider);

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

    private void defineTableLogic() {
        defineRowsLogic();
        defineColumnsLogic();
    }

    private void defineRowsLogic() {
        tracksTable.setRowFactory(tv -> {
            TableRow<AudioTrack> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (row.getItem() != currentTrack) {
                    playService.stop();
                    currentTrack = row.getItem();
                    currentTrackInfo.setText(currentTrack.getArtist() + " - " + currentTrack.getTitle());
                    currentTrackStartLabel.setText(toStringFormat(currentTrack.getStartInSeconds()));
                    currentTrackEndLabel.setText(toStringFormat(currentTrack.getFinishInSeconds()));
                    prepareRangeSlider();
                }
                activeTableRowDealer.updateActiveRow(row);
            });
            row.setOnMouseEntered(event -> {
                activeTableRowDealer.updateHoveredRow(row);
            });
            row.setOnMouseExited(event -> {
                row.getStyleClass().remove("hovered");
            });
            return row;
        });
    }

    private void defineColumnsLogic() {
        defineEditableColumnsLogic();
        defineButtonedColumnsLogic();
    }

    private void defineEditableColumnsLogic() {
        artist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        artist.setCellFactory(TextFieldTableCell.forTableColumn());
        artist.setOnEditCommit(
                (TableColumn.CellEditEvent<AudioTrack, String> t) ->
                        (t.getTableView().getItems().get(t.getTablePosition().getRow())).setArtist(t.getNewValue()));

        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        title.setCellFactory(TextFieldTableCell.forTableColumn());
        title.setOnEditCommit(
                (TableColumn.CellEditEvent<AudioTrack, String> t) ->
                        (t.getTableView().getItems().get(t.getTablePosition().getRow())).setTitle(t.getNewValue()));

    }

    private void defineButtonedColumnsLogic() {
        play.setCellFactory(imageButtonCellFactoryProvider.provide(
                "/mediaplayerIcons/play-small.png",
                this::activateTrackAndPlay,
                ButtonType.PLAY));

        pause.setCellFactory(imageButtonCellFactoryProvider.provide(
                "/mediaplayerIcons/pause-small.png",
                this::pause,
                ButtonType.PAUSE));

        stop.setCellFactory(imageButtonCellFactoryProvider.provide(
                "/mediaplayerIcons/stop-small.png",
                this::stop,
                ButtonType.STOP));

        remove.setCellFactory(imageButtonCellFactoryProvider.provide(
                "/mediaplayerIcons/delete2.png",
                this::removeFromTable,
                ButtonType.DELETE));
    }

    private void activateTrackAndPlay(AudioTrack audioTrack) {
        if (!audioTrack.equals(currentTrack)) {
            currentTrack = audioTrack;
            playService.resetQueue(Collections.singletonList(audioTrack));
            currentTrackInfo.setText(currentTrack.getArtist() + " - " + currentTrack.getTitle());
            prepareRangeSlider();
        }
        playService.play();
    }

    private void pause(AudioTrack audioTrack) {
        if (audioTrack.equals(currentTrack)) {
            playService.pause();
        }
    }

    private void stop(AudioTrack audioTrack) {
        if (audioTrack.equals(currentTrack)) {
            playService.stop();
        }
    }

    private void removeFromTable(AudioTrack audioTrack) {
        audioTracks.remove(audioTrack);
        tracksTable.getItems().setAll(audioTracks);
    }
}

