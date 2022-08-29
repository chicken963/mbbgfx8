package ru.orthodox.mbbg.model.proxy.create;

import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
import ru.orthodox.mbbg.utils.hierarchy.ElementFinder;
import ru.orthodox.mbbg.utils.hierarchy.HierarchyUtils;

import java.util.stream.Stream;

@Getter
public class RoundTab {
    private final Tab tab;

    private final EditAudioTracksTable editAudioTracksTable;
    private final EventPublisherService eventPublisherService;


    private ChoiceBox<WinCondition> firstPrizeCondition;
    private ChoiceBox<WinCondition> secondPrizeCondition;
    private ChoiceBox<WinCondition> thirdPrizeCondition;

    private ChoiceBox<BlankSize> blankDimensions;

    private TextField roundNameTextField;
    private TextField numberOfBlanks;

    public RoundTab(Tab tab,
                    HBox audioTracksGridRowTemplate,
                    PlayMediaService playMediaService,
                    EventPublisherService eventPublisherService, Round round) {
        this.tab = tab;
        this.eventPublisherService = eventPublisherService;

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
        return ElementFinder.<ChoiceBox<WinCondition>>findTabElementByTypeAndStyleclass(tab, "thirdPrizeCondition");
    }

    public ChoiceBox<WinCondition> getSecondPrizeCondition() {
        return ElementFinder.<ChoiceBox<WinCondition>>findTabElementByTypeAndStyleclass(tab, "secondPrizeCondition");
    }

    public ChoiceBox<WinCondition> getFirstPrizeCondition() {
        return ElementFinder.<ChoiceBox<WinCondition>>findTabElementByTypeAndStyleclass(tab, "firstPrizeCondition");
    }

    public TextField getNewRoundNameTextField() {
        return ElementFinder.<TextField>findTabElementByTypeAndStyleclass(tab, "newRoundName");
    }

    public ChoiceBox<BlankSize> getBlankDimensions() {
        return ElementFinder.<ChoiceBox<BlankSize>>findTabElementByTypeAndStyleclass(tab, "blankDimensions");
    }

    public TextField getNumberOfBlanks() {
        return ElementFinder.<TextField>findTabElementByTypeAndStyleclass(tab, "numberOfBlanks");
    }

    public Label getNumberOfBlanksWarning() {
        return ElementFinder.<Label>findTabElementByTypeAndStyleclass(tab, "numberOfBlanksWarning");
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
}
