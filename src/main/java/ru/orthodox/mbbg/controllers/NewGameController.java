package ru.orthodox.mbbg.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.RowConstraints;
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
import ru.orthodox.mbbg.services.EventsHandlingService;
import ru.orthodox.mbbg.services.GameService;
import ru.orthodox.mbbg.services.ScreenService;
import ru.orthodox.mbbg.ui.modelExtensions.newGameScene.*;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ru.orthodox.mbbg.ui.CustomFontDealer.setDefaultFont;
import static ru.orthodox.mbbg.ui.hierarchy.NodeDeepCopyProvider.createDeepCopy;

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
    private TextField numberOfBlanks;
    @FXML
    private Button addTracksButton;
    @FXML
    private Button importTracksButton;
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
    @Autowired
    private EventsHandlingService eventsHandlingService;


    private RoundsTabPane roundsTabPane;

    @PostConstruct
    private void setUp() {
        setDefaultFont(
                newGameLabel,
                enterNewGameNameLabel,
                addTracksButton,
                saveGame,
                cancelCreation,
                importTracksButton);
    }

    public void render() {
        RoundTab firstTab = new RoundTab(createDeepCopy(tabSample), eventsHandlingService, 0);
        List<RoundTab> roundTabs = new ArrayList<RoundTab>() {{
            add(firstTab);
        }};
        this.roundsTabPane = new RoundsTabPane(tabPane, tabSample, roundTabs, eventsHandlingService);
        //saveGame.setDisable(true);
    }

    @FXML
    private void openExplorerMenu(ActionEvent e) {
        FileChooser fileChooser = fileChooserDealer.preconfigureFileChooser();
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(((Node) e.getSource()).getScene().getWindow());
        if (selectedFiles == null) {
            return;
        }
        List<AudioTrack> audioTracks = fileChooserDealer.mapFilesToAudioTracks(selectedFiles);
        RoundTab currentTab = roundsTabPane.findTabByChild((Node) e.getSource());
        AudioTracksView roundTable = currentTab.getAudioTracksTable();
        roundTable.addAudioTracks(audioTracks);
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
        screenService.activate("startMenu");
    }


    @FXML
    private void deleteRound(ActionEvent actionEvent) {
        RoundTab tabToDelete = roundsTabPane.findTabByChild((Node) actionEvent.getSource());
        roundsTabPane.removeRoundTab(tabToDelete);
    }

    @FXML
    private void addRound() {
        RoundTab newTab = new RoundTab(createDeepCopy(tabSample), eventsHandlingService, roundsTabPane.getTabsCount());
        roundsTabPane.addRoundTab(1, newTab);
        roundsTabPane.getTabPane().setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
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
