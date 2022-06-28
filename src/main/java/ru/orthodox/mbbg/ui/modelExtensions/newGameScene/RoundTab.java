package ru.orthodox.mbbg.ui.modelExtensions.newGameScene;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import lombok.Getter;
import ru.orthodox.mbbg.enums.WinCondition;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.ui.hierarchy.ElementFinder;
import ru.orthodox.mbbg.ui.hierarchy.HierarchyUtils;

import java.util.stream.Stream;

@Getter
public class RoundTab {
    private final Tab tab;

    private AudioTracksTable audioTracksTable;

    private final int index;

    private ChoiceBox<WinCondition> firstPrizeCondition;
    private ChoiceBox<WinCondition> secondPrizeCondition;
    private ChoiceBox<WinCondition> thirdPrizeCondition;

    private ChoiceBox<String> blankDimensions;

    private TextField roundNameTextField;
    private TextField numberOfBlanks;

    private PrizeConditionsDealer prizeConditionsDealer;
    private RoundDimensionsDealer roundDimensionsDealer;

    private EditTracksWorkspaceDealer editTracksWorkspaceDealer;

    public RoundTab(Tab tab, int index) {
        this.tab = tab;
        this.index = index;
        this.audioTracksTable = Stream.of(ElementFinder.<TableView<AudioTrack>>findTabElementByTypeAndStyleclass(tab, "tracksTable"))
                .map(AudioTracksTable::new)
                .findFirst()
                .orElse(null);

        this.firstPrizeCondition = getFirstPrizeCondition();
        this.secondPrizeCondition = getSecondPrizeCondition();
        this.thirdPrizeCondition = getThirdPrizeCondition();
        this.prizeConditionsDealer = new PrizeConditionsDealer(firstPrizeCondition, secondPrizeCondition, thirdPrizeCondition);
        this.blankDimensions = getBlankDimensions();
        RoundDimensionsDealer.populateDimensionsChoiceBox(blankDimensions);


        this.numberOfBlanks = getNumberOfBlanks();


        this.roundNameTextField = getNewRoundNameTextField();
        this.editTracksWorkspaceDealer = new EditTracksWorkspaceDealer(this);

        this.bindRoundNameToTabName();
        this.disableDeleteRoundButton();
    }

    public boolean containsChild(Node child) {
        return HierarchyUtils.containsNode(child, tab);
    }

    public Label getCurrentTrackInfoLabel() {
        return ElementFinder.<Label>findTabElementByTypeAndStyleclass(tab, "currentTrackInfo");
    }

    public Label getCurrentTrackStartLabel() {
        return ElementFinder.<Label>findTabElementByTypeAndStyleclass(tab, "currentTrackStartLabel");
    }

    public Label getCurrentTrackEndLabel() {
        return ElementFinder.<Label>findTabElementByTypeAndStyleclass(tab, "currentTrackEndLabel");
    }

    public Label getCurrentSnippetRate() {
        return ElementFinder.<Label>findTabElementByTypeAndStyleclass(tab, "currentSnippetRate");
    }

    public Label getCurrentSnippetLength() {
        return ElementFinder.<Label>findTabElementByTypeAndStyleclass(tab, "currentSnippetLength");
    }

    public HBox getSliderContainer() {
        return ElementFinder.<HBox>findTabElementByTypeAndStyleclass(tab, "sliderContainer");
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

    public ChoiceBox<String> getBlankDimensions() {
        return ElementFinder.<ChoiceBox<String>>findTabElementByTypeAndStyleclass(tab, "blankDimensions");
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

    private Button findDeleteRoundButton() {
        return ElementFinder.findRecursivelyByStyle((Parent) tab.getContent(), "deleteRound").stream()
                .map(item -> (Button) item).findFirst().orElseThrow(() ->
                        new IllegalArgumentException("Delete round button was not found"));
    }

    private void bindRoundNameToTabName() {
        roundNameTextField.setOnKeyReleased((event) ->
                tab.setText(roundNameTextField.getText().isEmpty()
                        ? "Round " + (index + 1)
                        : roundNameTextField.getText()));
    }

    public void disableDeleteRoundButton() {
        findDeleteRoundButton().setDisable(true);
    }

    public void enableDeleteRoundButton() {
        findDeleteRoundButton().setDisable(false);
    }

    public void validateConditions(int sourceConditionNumber) {
        switch (sourceConditionNumber) {
            case 1:
                prizeConditionsDealer.validateThatEasier(firstPrizeCondition, secondPrizeCondition);
                prizeConditionsDealer.validateThatEasier(firstPrizeCondition, thirdPrizeCondition);
                break;
            case 2:
                prizeConditionsDealer.validateThatHarder(secondPrizeCondition, firstPrizeCondition);
                prizeConditionsDealer.validateThatEasier(secondPrizeCondition, thirdPrizeCondition);
                break;
            case 3:
                prizeConditionsDealer.validateThatHarder(thirdPrizeCondition, firstPrizeCondition);
                prizeConditionsDealer.validateThatHarder(thirdPrizeCondition, secondPrizeCondition);
                break;
            default:
                break;
        }
    }

    public boolean isFilled(){
        return !firstPrizeCondition.getValue().equals(null)
            && !secondPrizeCondition.getValue().equals(null)
            && !thirdPrizeCondition.getValue().equals(null)
            && !blankDimensions.getValue().equals(null)
            && !numberOfBlanks.getText().isEmpty()
            && !roundNameTextField.getText().isEmpty()
            && audioTracksTable.isFilled();

    }
}
