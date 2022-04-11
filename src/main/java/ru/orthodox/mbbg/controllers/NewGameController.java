package ru.orthodox.mbbg.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.controlsfx.control.RangeSlider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import ru.orthodox.mbbg.enums.WinCondition;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.model.Game;
import ru.orthodox.mbbg.model.Round;
import ru.orthodox.mbbg.services.NewGameService;
import ru.orthodox.mbbg.services.ScreenService;
import ru.orthodox.mbbg.services.model.AudioTrackService;
import ru.orthodox.mbbg.services.model.GamesService;
import ru.orthodox.mbbg.services.model.RoundService;
import ru.orthodox.mbbg.utils.ui.EditTracksWorkspaceDealer;
import ru.orthodox.mbbg.utils.ui.newGameScene.ElementFinder;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.orthodox.mbbg.utils.ui.CustomFontDealer.setDefaultFont;
import static ru.orthodox.mbbg.utils.ui.HierarchyUtils.findParentTab;
import static ru.orthodox.mbbg.utils.ui.HierarchyUtils.findRecursivelyByStyleClass;
import static ru.orthodox.mbbg.utils.ui.NodeDeepCopyProvider.createDeepCopy;
import static ru.orthodox.mbbg.utils.ui.newGameScene.ElementFinder.*;

@Configurable
public class NewGameController {

    @FXML
    private Label enterNewGameNameLabel;
    @FXML
    private Label newGameLabel;
    @FXML
    private TextField newGameName;
    @FXML
    private Button addTracksButton;
    @FXML
    private Button deleteRound;
    @FXML
    private Button addNextRound;
    @FXML
    private Button saveGame;
    @FXML
    private Button cancelCreation;
    @FXML
    private Tab tabSample;
    @FXML
    private TabPane tabPane;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private NewGameService newGameService;
    @Autowired
    private ScreenService screenService;
    @Autowired
    private AudioTrackService audioTrackService;
    @Autowired
    private RoundService roundService;
    @Autowired
    private GamesService gamesService;
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

        Tab firstTab = createDeepCopy(tabSample);
        bindRoundNameToTabName(firstTab);
        configureCheckBoxes(firstTab);
        disableDeleteRoundButton(firstTab);
        tabPane.getTabs().add(firstTab);
        setTabNames(tabPane);
    }

    @FXML
    private void openExplorerMenu(ActionEvent e) {
        FileChooser fileChooser = preconfigureFileChooser();
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(((Node) e.getSource()).getScene().getWindow());
        audioTracks = mapFilesToAudioTracks(selectedFiles);
        Tab currentTab = findParentTab((Node) e.getSource(), tabPane);
        TableView<AudioTrack> roundTable = ElementFinder.<TableView<AudioTrack>>findTabElementByTypeAndStyleclass(currentTab, "tracksTable");
        roundTable.getItems().setAll(audioTracks);
        this.editTracksWorkspaceDealer = new EditTracksWorkspaceDealer(currentTab, audioTracks);
    }

    @FXML
    private void saveNewGame(ActionEvent e) {
        audioTrackService.save(audioTracks);
        List<Round> rounds = new ArrayList<>();
        for (Tab tab: tabPane.getTabs()) {

            ChoiceBox firstPrizeCondition = ElementFinder.<ChoiceBox>findTabElementByTypeAndStyleclass(tab, "firstPrizeCondition");
            ChoiceBox secondPrizeCondition = ElementFinder.<ChoiceBox>findTabElementByTypeAndStyleclass(tab, "secondPrizeCondition");
            ChoiceBox thirdPrizeCondition = ElementFinder.<ChoiceBox>findTabElementByTypeAndStyleclass(tab, "thirdPrizeCondition");

            TextField newRoundName = ElementFinder.<TextField>findTabElementByTypeAndStyleclass(tab, "newRoundName");

            ChoiceBox<Integer> rowsNumber = ElementFinder.<ChoiceBox<Integer>>findTabElementByTypeAndStyleclass(tab, "rowsNumber");
            ChoiceBox<Integer> columnsNumber = ElementFinder.<ChoiceBox<Integer>>findTabElementByTypeAndStyleclass(tab, "columnsNumber");

            TableView<AudioTrack> roundTracksTable = ElementFinder.<TableView<AudioTrack>>findTabElementByTypeAndStyleclass(tab, "tracksTable");
            List<AudioTrack> roundAudioTracks = roundTracksTable.getItems();

            Round round = Round.builder()
                    .id(UUID.randomUUID())
                    .name(newRoundName.getText())
                    .firstStrikeCondition(WinCondition.findByMessage(firstPrizeCondition.getValue().toString()))
                    .secondStrikeCondition(WinCondition.findByMessage(secondPrizeCondition.getValue().toString()))
                    .thirdStrikeCondition(WinCondition.findByMessage(thirdPrizeCondition.getValue().toString()))
                    .width(rowsNumber.getValue())
                    .height(columnsNumber.getValue())
                    .tracksIds(roundAudioTracks.stream().map(AudioTrack::getId).collect(Collectors.toList()))
                    .build();
            rounds.add(round);
            roundService.save(round);
        }

        Button saveButton = (Button) e.getSource();
        saveButton.setDisable(true);
        Game game = new Game();
        game.setId(UUID.randomUUID());
        game.setName(newGameName.getText());
        game.setRoundIds(rounds.stream()
                .map(Round::getId)
                .collect(Collectors.toList()));
        gamesService.save(game);
    }

    @FXML
    private void cancelCreation() {
        StartMenuController startMenuController = applicationContext.getBean(StartMenuController.class);
        startMenuController.fillGridWithAllGames();
        screenService.activate("startmenu");
    }


    @FXML
    private void deleteRound(ActionEvent actionEvent) {
        Node eventTarget = (Node) actionEvent.getSource();
        while (!(eventTarget instanceof SplitPane)) {
            eventTarget = eventTarget.getParent();
        }
        SplitPane mainContentLevel = (SplitPane) eventTarget;
        Tab tabToDelete = tabPane.getTabs()
                .stream()
                .filter(tab -> tab.getContent().equals(mainContentLevel))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Not found tab to delete"));
        tabPane.getTabs().remove(tabToDelete);
        setTabNames(tabPane);
        if (tabPane.getTabs().size() == 1) {
            disableDeleteRoundButton(tabPane.getTabs().get(0));
        }
    }

    @FXML
    private void addRound(){
        Tab newTab = createDeepCopy(tabSample);
        newTab.setText("Round 2");
        configureCheckBoxes(newTab);
        tabPane.getTabs().add(newTab);
        bindRoundNameToTabName(newTab);
        setTabNames(tabPane);
        for (Tab tab : tabPane.getTabs()) {
            enableDeleteRoundButton(tab);
        }
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

    private void preconfigureRoundDimensions(ChoiceBox rowsNumber, ChoiceBox columnsNumber) {
        rowsNumber.getItems().addAll(4, 5, 6, 7, 8);
        columnsNumber.getItems().addAll(4, 5, 6, 7, 8, 9, 10, 11, 12);
    }


    private void preconfigureWinConditions(List<ChoiceBox> conditionContainers) {
        conditionContainers.forEach(container -> container.getItems().addAll(prizeConditions));
    }

    private void disableDeleteRoundButton(Tab tab) {
        findRecursivelyByStyleClass((Parent) tab.getContent(), "deleteRound").stream()
                .map(item -> (Button) item)
                .forEach(button -> button.setDisable(true));
    }

    private void enableDeleteRoundButton(Tab tab) {
        findRecursivelyByStyleClass((Parent) tab.getContent(), "deleteRound").stream()
                .map(item -> (Button) item)
                .forEach(button -> button.setDisable(false));
    }

    private void configureCheckBoxes(Tab tab) {
        ChoiceBox firstPrizeCondition = ElementFinder.<ChoiceBox>findTabElementByTypeAndStyleclass(tab, "firstPrizeCondition");
        ChoiceBox secondPrizeCondition = ElementFinder.<ChoiceBox>findTabElementByTypeAndStyleclass(tab, "secondPrizeCondition");
        ChoiceBox thirdPrizeCondition = ElementFinder.<ChoiceBox>findTabElementByTypeAndStyleclass(tab, "thirdPrizeCondition");
        preconfigureWinConditions(Arrays.asList(firstPrizeCondition, secondPrizeCondition, thirdPrizeCondition));
        ChoiceBox rowsNumber = ElementFinder.<ChoiceBox<Integer>>findTabElementByTypeAndStyleclass(tab, "rowsNumber");
        ChoiceBox columnsNumber = ElementFinder.<ChoiceBox<Integer>>findTabElementByTypeAndStyleclass(tab, "columnsNumber");
        preconfigureRoundDimensions(rowsNumber, columnsNumber);
    }

    private void setTabNames(TabPane tabPane) {
        tabPane.getTabs().stream()
                .filter(tab -> ElementFinder.<TextField>findTabElementByTypeAndStyleclass(tab, "newRoundName").getText().isEmpty())
                .forEach(tab -> tab.setText("Round " + (tabPane.getTabs().indexOf(tab) + 1)));
    }

    private void bindRoundNameToTabName(Tab tab){
        TextField roundName = ElementFinder.<TextField>findTabElementByTypeAndStyleclass(tab, "newRoundName");
        roundName.setOnKeyPressed((event) -> tab.setText(roundName.getText()));
    }
}
