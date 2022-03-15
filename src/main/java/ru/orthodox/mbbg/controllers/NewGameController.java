package ru.orthodox.mbbg.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.controlsfx.control.RangeSlider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import ru.orthodox.mbbg.enums.WinCondition;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.model.Round;
import ru.orthodox.mbbg.services.model.AudioTrackService;
import ru.orthodox.mbbg.services.NewGameService;
import ru.orthodox.mbbg.services.ScreenService;
import ru.orthodox.mbbg.services.model.RoundService;
import ru.orthodox.mbbg.utils.NormalizedPathString;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Please select audio files");
        //TODO: uncomment and delete the subsequent line
//        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialDirectory(new File("F:\\myDocs\\Моя музыка"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Music files", "*.mp3", "*.aac", "*.wav", "*.flac")
        );
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(((Node) e.getSource()).getScene().getWindow());
        audioTracks = selectedFiles.stream()
                .map(File::getAbsolutePath)
                .map(absPath -> audioTrackService.generateFromFile(absPath))
                .collect(Collectors.toList());
        artist.setCellValueFactory(new PropertyValueFactory<AudioTrack, String>("artist"));
        title.setCellValueFactory(new PropertyValueFactory<AudioTrack, String>("title"));
        play.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<AudioTrack, String>, TableCell<AudioTrack, String>> cellFactory
                = new Callback<TableColumn<AudioTrack, String>, TableCell<AudioTrack, String>>() {
                    @Override
                    public TableCell call(final TableColumn<AudioTrack, String> param) {
                        Image img = new Image("/mediaplayerIcons/play2.png");
                        ImageView imgv = new ImageView(img);
                        imgv.setFitHeight(18);
                        imgv.setFitWidth(18);
                        imgv.setPickOnBounds(true);
                        imgv.setPreserveRatio(true);
                        Button btn = new Button(null, imgv);
                        btn.setPrefWidth(18);
                        btn.setPrefHeight(18);
                        btn.setTranslateX(-10);
                        btn.setPickOnBounds(true);
                        btn.setCursor(Cursor.HAND);
                        btn.setStyle("-fx-background-color: transparent;");
                        final TableCell<AudioTrack, String> cell = new TableCell<AudioTrack, String>() {

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        AudioTrack person = getTableView().getItems().get(getIndex());
                                        System.out.println(person.getTitle());

                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };
        play.setCellFactory(cellFactory);
        tracksTable.getItems().setAll(audioTracks);
        prepareRangeSlider(audioTracks.get(0));
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
    private void prepareRangeSlider(AudioTrack audioTrack) {
        RangeSlider slider = new RangeSlider(0, 100, 10, 90);
        //Setting the slider properties
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(25);
        slider.setBlockIncrement(10);
        //VBox to arrange circle and the slider
        sliderContainer.getChildren().addAll(slider);

    }

}
