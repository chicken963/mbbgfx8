package ru.orthodox.mbbg.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import ru.orthodox.mbbg.mappers.TabToRoundMapper;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.model.Game;
import ru.orthodox.mbbg.model.Round;
import ru.orthodox.mbbg.services.AudioTrackService;
import ru.orthodox.mbbg.services.GameService;
import ru.orthodox.mbbg.services.ScreenService;
import ru.orthodox.mbbg.utils.ui.newGameScene.AudioTracksTable;
import ru.orthodox.mbbg.utils.ui.newGameScene.EditTracksWorkspaceDealer;
import ru.orthodox.mbbg.utils.ui.newGameScene.RoundTab;
import ru.orthodox.mbbg.utils.ui.newGameScene.RoundsTabPane;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.orthodox.mbbg.utils.ui.CustomFontDealer.setDefaultFont;
import static ru.orthodox.mbbg.utils.ui.NodeDeepCopyProvider.createDeepCopy;

@Configurable
@Scope("prototype")
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
    private ScreenService screenService;
    @Autowired
    private GameService gameService;
    @Autowired
    private AudioTrackService audioTrackService;
    @Autowired
    private TabToRoundMapper tabToRoundMapper;

    private EditTracksWorkspaceDealer editTracksWorkspaceDealer;
    private RoundsTabPane roundsTabPane;

    @PostConstruct
    private void setUp() {
        setDefaultFont(
                newGameLabel,
                enterNewGameNameLabel,
                addTracksButton,
                deleteRound,
                saveGame,
                cancelCreation);
    }

    @FXML
    private void openExplorerMenu(ActionEvent e) {
        FileChooser fileChooser = preconfigureFileChooser();
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(((Node) e.getSource()).getScene().getWindow());
        List<AudioTrack> audioTracks = mapFilesToAudioTracks(selectedFiles);
        RoundTab currentTab = roundsTabPane.findTabByChild((Node) e.getSource());
        AudioTracksTable roundTable = currentTab.getAudioTracksTable();
        roundTable.setAudioTracks(audioTracks);
        this.editTracksWorkspaceDealer = new EditTracksWorkspaceDealer(currentTab, audioTracks);
    }

    @FXML
    private void saveNewGame(ActionEvent e) {
        Game game = new Game();
        game.setId(UUID.randomUUID());
        game.setName(newGameName.getText());

        for (RoundTab tab: roundsTabPane.getRoundTabs()) {
            Round round = tabToRoundMapper.generateFromUIContent(tab);
            game.getRounds().add(round);
        }

        gameService.save(game);

        Button saveButton = (Button) e.getSource();
        saveButton.setDisable(true);


    }

    @FXML
    private void cancelCreation() {
        StartMenuController startMenuController = applicationContext.getBean(StartMenuController.class);
        startMenuController.fillGridWithAllGames();
        screenService.activate("startmenu");
    }


    @FXML
    private void deleteRound(ActionEvent actionEvent) {
        RoundTab tabToDelete = roundsTabPane.findTabByChild((Node) actionEvent.getSource());
        roundsTabPane.removeRoundTab(tabToDelete);
    }

    @FXML
    private void addRound(){
        RoundTab newTab = new RoundTab(createDeepCopy(tabSample), roundsTabPane.getTabsCount());
        roundsTabPane.addRoundTab(newTab);
    }

    public void render() {
        RoundTab firstTab = new RoundTab(createDeepCopy(tabSample), 0);
        List<RoundTab> roundTabs = new ArrayList(){{add(firstTab);}};
        this.roundsTabPane = new RoundsTabPane(tabPane, roundTabs);
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
}
