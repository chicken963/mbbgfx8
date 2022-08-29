package ru.orthodox.mbbg.services.create;

import javafx.scene.control.ChoiceBox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.enums.WinCondition;
import ru.orthodox.mbbg.events.TabCreatedEvent;
import ru.orthodox.mbbg.events.WinConditionChangedEvent;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.model.proxy.create.RoundTab;

@Slf4j
@Service
public class PrizeConditionsManager {

    @EventListener
    public void onTabCreatedEvent(TabCreatedEvent tabCreatedEvent) {
        prefillWinConditions(tabCreatedEvent.getRoundTab());

        RoundTab tabToPopulate = tabCreatedEvent.getRoundTab();
        Round roundToUseDefaultData = tabToPopulate.getRound();

        if (roundToUseDefaultData == null) {
            setDefaultValues(tabToPopulate);
        }
    }

    @Order(1)
    @EventListener
    public void onWinConditionChangedEvent(WinConditionChangedEvent winConditionChangedEvent) {
        validateConditions(winConditionChangedEvent.getRoundTab(), winConditionChangedEvent.getNumberOfWinLevel());
        saveNewValues(winConditionChangedEvent.getRoundTab());
    }

    private void saveNewValues(RoundTab roundTab) {
        Round roundToUpdate = roundTab.getRound();
        roundToUpdate.setFirstStrikeCondition(roundTab.getFirstPrizeCondition().getValue());
        roundToUpdate.setSecondStrikeCondition(roundTab.getSecondPrizeCondition().getValue());
        roundToUpdate.setThirdStrikeCondition(roundTab.getThirdPrizeCondition().getValue());
    }

    private void validateConditions(RoundTab tab, int sourceConditionNumber) {
        switch (sourceConditionNumber) {
            case 1:
                validateThatEasier(tab.getFirstPrizeCondition(), tab.getSecondPrizeCondition());
                validateThatEasier(tab.getFirstPrizeCondition(), tab.getThirdPrizeCondition());
                break;
            case 2:
                validateThatHarder(tab.getSecondPrizeCondition(), tab.getFirstPrizeCondition());
                validateThatEasier(tab.getSecondPrizeCondition(), tab.getThirdPrizeCondition());
                break;
            case 3:
                validateThatHarder(tab.getThirdPrizeCondition(), tab.getFirstPrizeCondition());
                validateThatHarder(tab.getThirdPrizeCondition(), tab.getSecondPrizeCondition());
                break;
            default:
                break;
        }
    }

    private void validateThatEasier(ChoiceBox<WinCondition> sourceCondition, ChoiceBox<WinCondition> conditionToValidate) {
        if (sourceCondition.getValue() != null
                && conditionToValidate.getValue() != null
                && sourceCondition.getValue().compare(conditionToValidate.getValue()) > 0) {
            log.warn("Condition {} was expected not to be harder than condition {}, but actual result differs.",
                    sourceCondition.getValue(),
                    conditionToValidate.getValue());
            conditionToValidate.setValue(null);
        }
    }

    private void validateThatHarder(ChoiceBox<WinCondition> sourceCondition, ChoiceBox<WinCondition> conditionToValidate) {
        if (sourceCondition.getValue() != null
            && conditionToValidate.getValue() != null
            && sourceCondition.getValue().compare(conditionToValidate.getValue()) < 0) {
        log.warn("Condition {} was expected not to be easier than condition {}, but actual result differs.",
            sourceCondition.getValue(),
            conditionToValidate.getValue());
        conditionToValidate.setValue(null);
        }
    }

    private void prefillWinConditions(RoundTab roundTab) {
        roundTab.getFirstPrizeCondition().getItems().addAll(WinCondition.values());
        roundTab.getSecondPrizeCondition().getItems().addAll(WinCondition.values());
        roundTab.getThirdPrizeCondition().getItems().addAll(WinCondition.values());
    }

    private void setDefaultValues(RoundTab roundTab) {
        roundTab.getFirstPrizeCondition().setValue(WinCondition.ONE_LINE_STRIKE);
        roundTab.getSecondPrizeCondition().setValue(WinCondition.THREE_LINES_STRIKE);
        roundTab.getThirdPrizeCondition().setValue(WinCondition.WHOLE_FIELD_STRIKE);
    }

    private void setRoundValues(RoundTab roundTab) {
        Round round = roundTab.getRound();
        roundTab.getFirstPrizeCondition().setValue(round.getFirstStrikeCondition());
        roundTab.getSecondPrizeCondition().setValue(round.getSecondStrikeCondition());
        roundTab.getThirdPrizeCondition().setValue(round.getThirdStrikeCondition());
    }
}
