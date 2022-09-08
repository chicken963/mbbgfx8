package ru.orthodox.mbbg.services.create;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.enums.BlanksStatus;
import ru.orthodox.mbbg.events.create.BrokenAudiotrackEvent;
import ru.orthodox.mbbg.exceptions.GameInputsValidationException;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.model.proxy.create.AudioTracksLibraryTable;
import ru.orthodox.mbbg.model.proxy.create.EditAudioTracksTable;
import ru.orthodox.mbbg.model.proxy.create.RoundTab;
import ru.orthodox.mbbg.model.proxy.create.RoundsTabPane;
import ru.orthodox.mbbg.services.common.PlayMediaService;
import ru.orthodox.mbbg.services.create.library.AudiotracksLibraryService;
import ru.orthodox.mbbg.services.create.validator.GameValidator;
import ru.orthodox.mbbg.services.model.AudioTrackService;
import ru.orthodox.mbbg.services.model.GameService;
import ru.orthodox.mbbg.services.popup.PopupAlerter;
import ru.orthodox.mbbg.services.start.StartMenuService;
import ru.orthodox.mbbg.utils.AudioTrackAsyncDataUpdater;
import ru.orthodox.mbbg.utils.screen.ScreenService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NewGameService {
    @Autowired
    private RoundsTabPane roundsTabPane;
    @Autowired
    private FileChooserDealer fileChooserDealer;
    @Autowired
    private GameValidator gameValidator;
    @Autowired
    private GameService gameService;
    @Autowired
    protected ScreenService screenService;
    @Autowired
    private AudiotracksLibraryService audiotracksLibraryService;
    @Autowired
    private AudioTracksLibraryTable audioTracksLibraryTable;
    @Autowired
    private AudioTrackAsyncDataUpdater audioTrackAsyncDataUpdater;
    @Autowired
    private PopupAlerter popupAlerter;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private AudioTrackService audioTrackService;
    @Autowired
    private PlayMediaService playMediaService;

    @EventListener
    private void onBrokenAudiotrack(BrokenAudiotrackEvent brokenAudiotrackEvent) {
        AudioTrack audioTrack = brokenAudiotrackEvent.getAudioTrack();
        brokenAudiotracks.add(audioTrack);
    }

    private TextField newGameName;
    private Label newGameLabel;

    private List<AudioTrack> brokenAudiotracks = new ArrayList<>();

    public void configureUIElements(Label newGameLabel, TextField newGameName) {
        this.newGameLabel = newGameLabel;
        this.newGameName = newGameName;
    }

    public void renderGame(Optional<Game> optGame) {
        if (optGame.isPresent()) {
            Game game = optGame.get();
            this.newGameLabel.setText("Edit game");
            this.newGameName.setText(game.getName());
            this.roundsTabPane.renderGame(game);
        } else {
            this.newGameLabel.setText("New game");
            this.newGameName.clear();
            this.roundsTabPane.renderEmpty();
        }

    }

    public void openExplorerMenuAndDefineOnSubmit(Button sourceButton) {
        audioTrackAsyncDataUpdater.setSourceButton(sourceButton);
        brokenAudiotracks = new ArrayList<>();
        FileChooser fileChooser = fileChooserDealer.provideFileChooser();
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(sourceButton.getScene().getWindow());
        if (selectedFiles == null) {
            return;
        }
        RoundTab currentTab = roundsTabPane.findTabByChild(sourceButton)
                .orElseThrow(() -> new IllegalArgumentException("Parent tab for table not found"));
        fileChooserDealer.setRound(currentTab.getRound());
        List<AudioTrack> audioTracks = fileChooserDealer.mapFilesToAudioTracks(selectedFiles);

        EditAudioTracksTable roundTable = currentTab.getEditAudioTracksTable();
        audioTracks.removeAll(brokenAudiotracks);
        roundTable.addAudioTracks(audioTracks);
        roundTable.getRound().getAudioTracks().addAll(audioTracks);
    }


    public void saveGame(Button saveButton) {
        GameScene gameScene = new GameScene(newGameName, roundsTabPane);

        try {
            gameValidator.validateGameScene(gameScene, saveButton);
        } catch (GameInputsValidationException ex) {
            return;
        }
        Game game = roundsTabPane.getGame();
        game.setName(newGameName.getText());
        if (game.getId() == null) {
            game.setId(UUID.randomUUID());
            game.setBlanksStatus(BlanksStatus.ABSENT);
        } else {
            game.setBlanksStatus(BlanksStatus.OUTDATED);
        }
        gameService.deepSave(game);
        game.setHavingUnsavedChanges(false);
        saveButton.setDisable(true);
        popupAlerter.invokeOkCancel(
                saveButton.getScene().getWindow(),
                "Success",
                "Game has been saved successfully. Would you like to continue editing?",
                event -> ((Stage) ((Button) event.getSource()).getScene().getWindow()).close(),
                this::backToMainMenu);
    }

    public void cancelCreation(Button cancelButton) {
        playMediaService.stop(playMediaService.getCurrentTrack());
        if (roundsTabPane.getGame().isHavingUnsavedChanges()) {
            popupAlerter.invokeOkCancel(
                    cancelButton.getScene().getWindow(),
                    "You have unsaved changes",
                    "You're going to exit edit mode. All the unsaved changes will be lost. Continue?",
                    event ->  {
                        StartMenuService startMenuService = applicationContext.getBean(StartMenuService.class);
                        startMenuService.fillGridWithAllGames();
                        ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
                        screenService.activate("startMenu");
                    },
                    event -> ((Stage) ((Button) event.getSource()).getScene().getWindow()).close());
        } else {
            StartMenuService startMenuService = applicationContext.getBean(StartMenuService.class);
            startMenuService.fillGridWithAllGames();
            screenService.activate("startMenu");
        }

    }

    public void openLibrary(Button libraryButton) {
        if (audioTrackService.findAll().isEmpty()) {
            popupAlerter.invoke(libraryButton.getScene().getWindow(), "Library is empty", "Oops. It seems that you've never created games before or cleaned up used audio tracks info storage.\nAfter you add audiotracks from your local machine, their info will be available during subsequent games creation.");
            return;
        }
        RoundTab currentTab = roundsTabPane.findTabByChild(libraryButton)
                .orElseThrow(() -> new IllegalArgumentException("Parent tab for table not found"));
        currentTab.openLibrary();
    }

    private void backToMainMenu(ActionEvent event) {
        StartMenuService startMenuService = applicationContext.getBean(StartMenuService.class);
        startMenuService.fillGridWithAllGames();
        Scene currentScene = ((Button) event.getSource()).getScene();
        Stage currentStage = (Stage) currentScene.getWindow();
        currentStage.close();
        screenService.activate("startMenu");
    }

    @Getter
    @AllArgsConstructor
    public static class GameScene {
        private TextField gameNameInput;
        private RoundsTabPane roundsTabPane;
    }
}
