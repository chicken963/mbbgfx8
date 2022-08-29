package ru.orthodox.mbbg.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import ru.orthodox.mbbg.events.BlankDimensionsChangedEvent;
import ru.orthodox.mbbg.events.TextFieldChangeEvent;
import ru.orthodox.mbbg.events.WinConditionChangedEvent;
import ru.orthodox.mbbg.model.proxy.create.RoundTab;
import ru.orthodox.mbbg.model.proxy.create.RoundsTabPane;
import ru.orthodox.mbbg.services.common.EventPublisherService;
import ru.orthodox.mbbg.services.create.EditGameService;
import ru.orthodox.mbbg.services.create.NewGameService;
import ru.orthodox.mbbg.services.create.validator.GameValidator;

import javax.annotation.PostConstruct;
import java.util.Optional;

import static ru.orthodox.mbbg.utils.common.CustomFontDealer.setDefaultFont;

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
    private Button importTracksButton;
    @FXML
    private Button saveGame;
    @FXML
    private Button cancelCreation;
    @FXML
    private Tab tabSample;
    @FXML
    private TabPane tabPane;
    @FXML
    private HBox audioTracksGridRowTemplate;
    @Autowired
    private RoundsTabPane roundsTabPane;
    @Autowired
    protected GameValidator gameValidator;
    @Autowired
    private EventPublisherService eventPublisherService;
    @Autowired
    private NewGameService newGameService;
    @Autowired
    private EditGameService editGameService;

    @PostConstruct
    private void configureUIElements() {

        setDefaultFont(
                newGameLabel,
                enterNewGameNameLabel,
                addTracksButton,
                saveGame,
                cancelCreation,
                importTracksButton);

        roundsTabPane.configureUIElements(tabPane, tabSample, audioTracksGridRowTemplate);
        gameValidator.setSaveButton(saveGame);
        gameValidator.setGameName(newGameName);
        gameValidator.setTabPane(roundsTabPane);

        newGameService.configureUIElements(newGameName);
        editGameService.configureUIElements(newGameName);
    }

    public void renderNewGameForm() {
        newGameService.renderGame(Optional.empty());
    }

    @FXML
    private void openExplorerMenu(ActionEvent e) {
        newGameService.openExplorerMenuAndDefineOnSubmit((Button) e.getSource());
    }

    @FXML
    private void saveGame(ActionEvent e) {
        newGameService.saveGame((Button) e.getSource());
    }

    @FXML
    private void cancelCreation() {
        newGameService.cancelCreation();
    }

    @FXML
    private void onFirstConditionChosen(ActionEvent actionEvent) {
        Optional<RoundTab> sourceTab = roundsTabPane.findTabByChild((Node) actionEvent.getSource());
        if (sourceTab.isPresent()) {
            eventPublisherService.publishEvent(new WinConditionChangedEvent(this, sourceTab.get(), 1));
        }
    }

    @FXML
    private void onSecondConditionChosen(ActionEvent actionEvent) {
        Optional<RoundTab> sourceTab = roundsTabPane.findTabByChild((Node) actionEvent.getSource());
        if (sourceTab.isPresent()) {
            eventPublisherService.publishEvent(new WinConditionChangedEvent(this, sourceTab.get(), 2));
        }
    }

    @FXML
    private void onThirdConditionChosen(ActionEvent actionEvent) {
        Optional<RoundTab> sourceTab = roundsTabPane.findTabByChild((Node) actionEvent.getSource());
        if (sourceTab.isPresent()) {
            eventPublisherService.publishEvent(new WinConditionChangedEvent(this, sourceTab.get(), 3));
        }
    }

    @FXML
    private void openLibrary(ActionEvent event) {
        newGameService.openLibrary((Button) event.getSource());
    }

    @FXML
    private void onBlankDimensionsChosen(ActionEvent event) {
        Optional<RoundTab> sourceTab = roundsTabPane.findTabByChild((Node) event.getSource());
        if (sourceTab.isPresent()) {
            eventPublisherService.publishEvent(new BlankDimensionsChangedEvent(sourceTab.get()));
        }
    }

    @FXML
    private void onGameNameChanged() {
        eventPublisherService.publishEvent(new TextFieldChangeEvent(this));
    }

}
