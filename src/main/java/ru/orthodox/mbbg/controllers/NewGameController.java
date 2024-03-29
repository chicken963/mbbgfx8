package ru.orthodox.mbbg.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import ru.orthodox.mbbg.exceptions.GameInputsValidationException;
import ru.orthodox.mbbg.mappers.SceneToGameMapper;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.model.Game;
import ru.orthodox.mbbg.services.GameService;
import ru.orthodox.mbbg.services.ScreenService;
import ru.orthodox.mbbg.utils.ui.newGameScene.*;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    private SceneToGameMapper sceneToGameMapper;
    @Autowired
    private FileChooserDealer fileChooserDealer;
    @Autowired
    private GameValidator gameValidator;

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

    public void render() {
        RoundTab firstTab = new RoundTab(createDeepCopy(tabSample), 0);
        List<RoundTab> roundTabs = new ArrayList<RoundTab>() {{
            add(firstTab);
        }};
        this.roundsTabPane = new RoundsTabPane(tabPane, roundTabs);
    }

    @FXML
    private void openExplorerMenu(ActionEvent e) {
        FileChooser fileChooser = fileChooserDealer.preconfigureFileChooser();
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(((Node) e.getSource()).getScene().getWindow());
        List<AudioTrack> audioTracks = fileChooserDealer.mapFilesToAudioTracks(selectedFiles);
        RoundTab currentTab = roundsTabPane.findTabByChild((Node) e.getSource());
        AudioTracksTable roundTable = currentTab.getAudioTracksTable();
        roundTable.setAudioTracks(audioTracks);
        this.editTracksWorkspaceDealer = new EditTracksWorkspaceDealer(currentTab, audioTracks);
    }

    @FXML
    private void saveNewGame(ActionEvent e) {
        GameScene gameScene = new GameScene(newGameName, roundsTabPane);

        try {
            gameValidator.validateGameScene(gameScene);
        } catch (GameInputsValidationException ex) {
            return;
        }

        Game game = sceneToGameMapper.generateFromScene(gameScene);
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
    private void addRound() {
        RoundTab newTab = new RoundTab(createDeepCopy(tabSample), roundsTabPane.getTabsCount());
        roundsTabPane.addRoundTab(newTab);
    }

    @FXML
    private void onFirstConditionChosen(ActionEvent actionEvent) {
        RoundTab sourceTab = roundsTabPane.findTabByChild((Node) actionEvent.getSource());
        sourceTab.validateConditions(1);
    }

    @FXML
    private void onSecondConditionChosen(ActionEvent actionEvent) {
        RoundTab sourceTab = roundsTabPane.findTabByChild((Node) actionEvent.getSource());
        sourceTab.validateConditions(2);
    }

    @FXML
    private void onThirdConditionChosen(ActionEvent actionEvent) {
        RoundTab sourceTab = roundsTabPane.findTabByChild((Node) actionEvent.getSource());
        sourceTab.validateConditions(3);
    }

    @Getter
    @AllArgsConstructor
    public class GameScene {
        private TextField gameNameInput;
        private RoundsTabPane roundsTabPane;

    }
}
