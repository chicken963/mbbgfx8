package ru.orthodox.mbbg.model.proxy.play;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import ru.orthodox.mbbg.enums.BlankSize;
import ru.orthodox.mbbg.enums.WinCondition;
import ru.orthodox.mbbg.events.RoundNameChangedEvent;
import ru.orthodox.mbbg.events.TabClosedEvent;
import ru.orthodox.mbbg.events.TabCreatedEvent;
import ru.orthodox.mbbg.events.TextFieldChangeEvent;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.model.proxy.create.EditAudioTracksTable;
import ru.orthodox.mbbg.services.common.EventPublisherService;
import ru.orthodox.mbbg.services.common.PlayMediaService;
import ru.orthodox.mbbg.services.create.RoundDimensionsManager;
import ru.orthodox.mbbg.utils.hierarchy.ElementFinder;
import ru.orthodox.mbbg.utils.hierarchy.HierarchyUtils;

import java.util.stream.Stream;

import static ru.orthodox.mbbg.utils.common.CustomFontDealer.setDefaultFont;

@Getter
public class RoundTab {
    private final Tab tab;
    @Setter
    private Round round;

    private EditAudioTracksTable editAudioTracksTable;

//    private final int index;


    private final ChoiceBox<WinCondition> firstPrizeCondition;
    private final ChoiceBox<WinCondition> secondPrizeCondition;
    private final ChoiceBox<WinCondition> thirdPrizeCondition;

    private final ChoiceBox<BlankSize> blankDimensions;

    private final TextField roundNameTextField;
    private final TextField numberOfBlanks;

    public RoundTab(Tab tab,
                    HBox audioTracksGridRowTemplate,
                    PlayMediaService playMediaService,
                    EventPublisherService eventPublisherService,
                    Round round) {
        this.tab = tab;
        this.round = round;

        defineAudioTracksGridStaticMembers(
                eventPublisherService,
                audioTracksGridRowTemplate,
                playMediaService);



        this.editAudioTracksTable = Stream.of(ElementFinder.<VBox>findTabElementByTypeAndStyleclass(tab, "tracks-grid"))
                .map(EditAudioTracksTable::new)
                .findFirst()
                .orElse(null);

        this.firstPrizeCondition = getFirstPrizeCondition();
        this.secondPrizeCondition = getSecondPrizeCondition();
        this.thirdPrizeCondition = getThirdPrizeCondition();
        this.blankDimensions = getBlankDimensions();

        this.numberOfBlanks = getNumberOfBlanks();


        this.roundNameTextField = getNewRoundNameTextField();

        getNewRoundNameTextField().setOnKeyReleased(
                keyEvent -> eventPublisherService.publishEvent(new RoundNameChangedEvent(this)));
        getNumberOfBlanks().setOnKeyReleased(
                keyEvent -> eventPublisherService.publishEvent(new TextFieldChangeEvent(this)));
        tab.setOnClosed(event -> eventPublisherService.publishEvent(new TabClosedEvent(this)));

        eventPublisherService.publishEvent(new TabCreatedEvent(this));
    }

    private void defineAudioTracksGridStaticMembers(EventPublisherService eventPublisherService, HBox audioTracksGridRowTemplate, PlayMediaService playMediaService) {
        EditAudioTracksTable.setPlayMediaService(playMediaService);

        EditAudioTracksTable.setEventPublisherService(eventPublisherService);

        EditAudioTracksTable.setAudioTracksTableRowTemplate(audioTracksGridRowTemplate);
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

    public Label getFirstPrizeConditionWarning() {
        return ElementFinder.<Label>findTabElementByTypeAndStyleclass(tab, "firstConditionWarning");
    }

    public Label getSecondPrizeConditionWarning() {
        return ElementFinder.<Label>findTabElementByTypeAndStyleclass(tab, "secondConditionWarning");
    }

    public Label getThirdPrizeConditionWarning() {
        return ElementFinder.<Label>findTabElementByTypeAndStyleclass(tab, "thirdConditionWarning");
    }

    public Label getDimensionsWarning() {
        return ElementFinder.<Label>findTabElementByTypeAndStyleclass(tab, "blankDimensionsWarning");
    }

    public Label getNumberOfBlanksWarning() {
        return ElementFinder.<Label>findTabElementByTypeAndStyleclass(tab, "numberOfBlanksWarning");
    }

    public boolean isFilled(){
        return firstPrizeCondition.getValue() != null
            && secondPrizeCondition.getValue() != null
            && thirdPrizeCondition.getValue() != null
            && blankDimensions.getValue() != null
            && !numberOfBlanks.getText().isEmpty()
            && !roundNameTextField.getText().isEmpty()
            && editAudioTracksTable.isFilled();

    }
}
