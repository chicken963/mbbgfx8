package ru.orthodox.mbbg.services.create;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.exceptions.GameInputsValidationException;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.model.proxy.create.AudioTracksLibraryTable;
import ru.orthodox.mbbg.model.proxy.create.EditAudioTracksTable;
import ru.orthodox.mbbg.model.proxy.create.RoundTab;
import ru.orthodox.mbbg.model.proxy.create.RoundsTabPane;
import ru.orthodox.mbbg.services.create.library.AudiotracksLibraryService;
import ru.orthodox.mbbg.services.create.validator.GameValidator;
import ru.orthodox.mbbg.services.model.GameService;
import ru.orthodox.mbbg.services.popup.PopupAlerter;
import ru.orthodox.mbbg.services.start.StartMenuService;
import ru.orthodox.mbbg.utils.screen.ScreenService;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.orthodox.mbbg.utils.hierarchy.NodeDeepCopyProvider.createDeepCopy;

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

    private TextField newGameName;
    private Scene audioTracksLibraryScene;

    public void configureUIElements(TextField newGameName) {
        this.newGameName = newGameName;
    }

    public void renderGame(Optional<Game> optGame) {
        if (optGame.isPresent()) {
            Game game = optGame.get();
            this.newGameName.setText(game.getName());
            this.roundsTabPane.renderGame(game);
        } else {
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
        List<AudioTrack> audioTracks = fileChooserDealer.mapFilesToAudioTracks(selectedFiles);
        RoundTab currentTab = roundsTabPane.findTabByChild(sourceButton)
                .orElseThrow(() -> new IllegalArgumentException("Parent tab for table not found"));;
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
        }
        gameService.save(game);
        saveButton.setDisable(true);
        popupAlerter.invoke(saveButton.getScene().getWindow(), "Success", "Game has been saved successfully.", this::backToMainMenu);
    }

    public void cancelCreation() {
        screenService.activate("startMenu");
    }

    public void openLibrary(Button libraryButton) {
        AnchorPane audioTracksLibraryRootTemplate = (AnchorPane) screenService.getParentNode("audioTracksLibrary");
        AnchorPane audioTracksLibraryRoot = (AnchorPane) createDeepCopy(audioTracksLibraryRootTemplate);

        audioTracksLibraryScene = new Scene(audioTracksLibraryRoot);
        audioTracksLibraryScene.getStylesheets().addAll("styleSheets/scrollable-table.css", "styleSheets/new-game.css");

        final Stage libraryStage = screenService.createSeparateStage(libraryButton, audioTracksLibraryScene, "Вот всё, что ты надобавлял за эти годы");


        HBox libraryRowTemplate = (HBox) screenService.getParentNode("audioTracksLibraryRow");

        RoundTab currentTab = roundsTabPane.findTabByChild(libraryButton)
                .orElseThrow(() -> new IllegalArgumentException("Parent tab for table not found"));
        audioTracksLibraryTable.setRoot(audioTracksLibraryRoot);
        audioTracksLibraryTable.setRowTemplate(libraryRowTemplate);
        audiotracksLibraryService.populateTableWithAllAudioTracks();
        audiotracksLibraryService.defineSubmitProperty(currentTab, libraryStage);
        libraryStage.show();
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
