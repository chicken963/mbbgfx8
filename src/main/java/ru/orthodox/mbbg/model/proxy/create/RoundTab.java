package ru.orthodox.mbbg.model.proxy.create;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import ru.orthodox.mbbg.enums.BlankSize;
import ru.orthodox.mbbg.enums.WinCondition;
import ru.orthodox.mbbg.events.RoundNameChangedEvent;
import ru.orthodox.mbbg.events.TabClosedEvent;
import ru.orthodox.mbbg.events.TabCreatedEvent;
import ru.orthodox.mbbg.events.TextFieldChangeEvent;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.services.common.EventPublisherService;
import ru.orthodox.mbbg.services.common.PlayMediaService;
import ru.orthodox.mbbg.services.create.library.AudiotracksLibraryService;
import ru.orthodox.mbbg.utils.hierarchy.ElementFinder;
import ru.orthodox.mbbg.utils.hierarchy.HierarchyUtils;
import ru.orthodox.mbbg.utils.screen.ScreenService;

import java.util.stream.Stream;

import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findTabElementByTypeAndStyleclass;
import static ru.orthodox.mbbg.utils.hierarchy.NodeDeepCopyProvider.createDeepCopy;

@Getter
public class RoundTab {
    private final Tab tab;

    private final EditAudioTracksTable editAudioTracksTable;
    private final EventPublisherService eventPublisherService;

    private final AudiotracksLibraryService audiotracksLibraryService;
    private final AudioTracksLibraryTable audioTracksLibraryTable;
    private final ScreenService screenService;


    private ChoiceBox<WinCondition> firstPrizeCondition;
    private ChoiceBox<WinCondition> secondPrizeCondition;
    private ChoiceBox<WinCondition> thirdPrizeCondition;

    private ChoiceBox<BlankSize> blankDimensions;

    private TextField roundNameTextField;
    private TextField numberOfBlanks;

    public RoundTab(Tab tab,
                    HBox audioTracksGridRowTemplate,
                    PlayMediaService playMediaService,
                    EventPublisherService eventPublisherService,
                    AudiotracksLibraryService audiotracksLibraryService,
                    AudioTracksLibraryTable audioTracksLibraryTable,
                    ScreenService screenService,
                    Round round) {
        this.tab = tab;
        this.eventPublisherService = eventPublisherService;
        this.audiotracksLibraryService = audiotracksLibraryService;
        this.audioTracksLibraryTable = audioTracksLibraryTable;
        this.screenService = screenService;

        this.editAudioTracksTable = Stream.of(ElementFinder.<VBox>findTabElementByTypeAndStyleclass(tab, "tracks-grid"))
                .map(audiotracksVbox -> new EditAudioTracksTable(
                        audiotracksVbox,
                        audioTracksGridRowTemplate,
                        playMediaService,
                        eventPublisherService,
                        round))
                .findFirst()
                .orElse(null);

        if (!round.getAudioTracks().isEmpty()) {
            setRound(round);
        }

        initiateInnerFields();
        initiateInnerFieldsOnChangeBehaviour();

        tab.setOnClosed(event -> eventPublisherService.publishEvent(new TabClosedEvent(this)));

        eventPublisherService.publishEvent(new TabCreatedEvent(this));
    }

    private void initiateInnerFieldsOnChangeBehaviour() {
        initiateRoundNameInputOnChangeBehaviour();
        initiateNumberOfBlanksInputOnChangeBehaviour();
    }

    private void initiateNumberOfBlanksInputOnChangeBehaviour() {
        numberOfBlanks.setOnKeyReleased(
                keyEvent -> {
                    getNumberOfBlanksWarning().setVisible(false);
                    try {
                        int newValue = Integer.parseInt(numberOfBlanks.getText());
                        eventPublisherService.publishEvent(new TextFieldChangeEvent(this));
                        getRound().setNumberOfBlanks(newValue);
                    } catch (NumberFormatException e) {
                        getNumberOfBlanksWarning().setVisible(true);
                    }
                });
    }

    private void initiateRoundNameInputOnChangeBehaviour() {
        roundNameTextField.setOnKeyReleased(
                keyEvent -> {
                    eventPublisherService.publishEvent(new RoundNameChangedEvent(this));
                    getRound().setName(roundNameTextField.getText());
                });
    }

    public Round getRound() {
        return getEditAudioTracksTable().getRound();
    }

    private void initiateInnerFields() {
        initiateChoiceBoxes();
        initiateTextFields();
    }

    private void initiateTextFields() {
        this.numberOfBlanks = getNumberOfBlanks();
        this.roundNameTextField = getNewRoundNameTextField();
    }

    private void initiateChoiceBoxes() {
        initiateWinConditions();
        this.blankDimensions = getBlankDimensions();
    }

    private void initiateWinConditions() {
        this.firstPrizeCondition = getFirstPrizeCondition();
        this.secondPrizeCondition = getSecondPrizeCondition();
        this.thirdPrizeCondition = getThirdPrizeCondition();
    }


    public boolean containsChild(Node child) {
        return HierarchyUtils.containsNode(child, tab);
    }

    public ChoiceBox<WinCondition> getThirdPrizeCondition() {
        return findTabElementByTypeAndStyleclass(tab, "third-prize-condition");
    }

    public ChoiceBox<WinCondition> getSecondPrizeCondition() {
        return findTabElementByTypeAndStyleclass(tab, "second-prize-condition");
    }

    public ChoiceBox<WinCondition> getFirstPrizeCondition() {
        return findTabElementByTypeAndStyleclass(tab, "first-prize-condition");
    }

    public TextField getNewRoundNameTextField() {
        return findTabElementByTypeAndStyleclass(tab, "new-round-name");
    }

    public ChoiceBox<BlankSize> getBlankDimensions() {
        return findTabElementByTypeAndStyleclass(tab, "blank-dimensions");
    }

    public TextField getNumberOfBlanks() {
        return findTabElementByTypeAndStyleclass(tab, "number-of-blanks");
    }

    public Label getNumberOfBlanksWarning() {
        return findTabElementByTypeAndStyleclass(tab, "number-of-blanks-warning");
    }

    private Button getLibraryButton() {
        return findTabElementByTypeAndStyleclass(tab, "open-library");
    }

    public boolean isFilled() {
        return firstPrizeCondition.getValue() != null
                && secondPrizeCondition.getValue() != null
                && thirdPrizeCondition.getValue() != null
                && blankDimensions.getValue() != null
                && !numberOfBlanks.getText().isEmpty()
                && !roundNameTextField.getText().isEmpty()
                && editAudioTracksTable.isFilled();

    }

    public void setRound(Round round) {
        getTab().setText(round.getName());
        getNumberOfBlanks().setText(round.getNumberOfBlanks().toString());
        getNewRoundNameTextField().setText(round.getName());
        getEditAudioTracksTable().addAudioTracks(round.getAudioTracks());
        getFirstPrizeCondition().setValue(round.getFirstStrikeCondition());
        getSecondPrizeCondition().setValue(round.getSecondStrikeCondition());
        getThirdPrizeCondition().setValue(round.getThirdStrikeCondition());
    }

    public void openLibrary() {

        AnchorPane audioTracksLibraryRootTemplate = (AnchorPane) screenService.getParentNode("audioTracksLibrary");
        AnchorPane audioTracksLibraryRoot = (AnchorPane) createDeepCopy(audioTracksLibraryRootTemplate);

        Scene audioTracksLibraryScene = new Scene(audioTracksLibraryRoot);
        audioTracksLibraryScene.getStylesheets().addAll("styleSheets/scrollable-table.css", "styleSheets/new-game.css");

        final Stage libraryStage = screenService.createSeparateStage(getLibraryButton(), audioTracksLibraryScene, "Вот всё, что ты надобавлял за эти годы");
        libraryStage.setOnCloseRequest(e -> audioTracksLibraryTable.stopPlayingIfNeeded());

        HBox libraryRowTemplate = (HBox) screenService.getParentNode("audioTracksLibraryRow");


        audioTracksLibraryTable.setRoot(audioTracksLibraryRoot);
        audioTracksLibraryTable.setRowTemplate(libraryRowTemplate);
        audiotracksLibraryService.populateTableWithAllAudioTracks();
        audiotracksLibraryService.defineSubmitProperty(getEditAudioTracksTable(), libraryStage);
        audiotracksLibraryService.defineHeaderCheckBoxProperty();
        libraryStage.setMaxWidth(1470);
        libraryStage.show();

    }
}
