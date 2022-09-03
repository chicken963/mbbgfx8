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
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.enums.BlanksStatus;
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
import ru.orthodox.mbbg.utils.screen.ScreenService;

import java.io.File;
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
    private PopupAlerter popupAlerter;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private AudioTrackService audioTrackService;
    @Autowired
    private PlayMediaService playMediaService;

    private TextField newGameName;
    private Label newGameLabel;

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
        saveButton.setDisable(true);
        popupAlerter.invokeOkCancel(
                saveButton.getScene().getWindow(),
                "Success",
                "Game has been saved successfully. Would you like to continue editing?",
                event -> ((Stage) ((Button) event.getSource()).getScene().getWindow()).close(),
                this::backToMainMenu);
    }

    public void cancelCreation() {
        playMediaService.stop(playMediaService.getCurrentTrack());
        StartMenuService startMenuService = applicationContext.getBean(StartMenuService.class);
        startMenuService.fillGridWithAllGames();
        screenService.activate("startMenu");
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
