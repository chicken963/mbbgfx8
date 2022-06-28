package ru.orthodox.mbbg.ui.modelExtensions.newGameScene;

import javafx.scene.control.ChoiceBox;
import lombok.extern.slf4j.Slf4j;
import ru.orthodox.mbbg.enums.WinCondition;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class PrizeConditionsDealer {

    private ChoiceBox<WinCondition> firstPrizeCondition;
    private ChoiceBox<WinCondition> secondPrizeCondition;
    private ChoiceBox<WinCondition> thirdPrizeCondition;

    private final List<String> prizeConditions = Arrays.stream(WinCondition.values())
            .map(WinCondition::getMessage)
            .collect(Collectors.toList());

    public PrizeConditionsDealer(ChoiceBox<WinCondition> firstPrizeCondition,
                                 ChoiceBox<WinCondition> secondPrizeCondition,
                                 ChoiceBox<WinCondition> thirdPrizeCondition) {
        this.firstPrizeCondition = firstPrizeCondition;
        this.secondPrizeCondition = secondPrizeCondition;
        this.thirdPrizeCondition = thirdPrizeCondition;

        preconfigureWinConditions(Arrays.asList(firstPrizeCondition, secondPrizeCondition, thirdPrizeCondition));
    }


    private void preconfigureWinConditions(List<ChoiceBox> conditionContainers) {
        conditionContainers.forEach(container -> container.getItems().addAll(WinCondition.values()));
    }

    public void validateThatEasier(ChoiceBox<WinCondition> sourceCondition, ChoiceBox<WinCondition> conditionToValidate) {
        if (sourceCondition.getValue() != null
                && conditionToValidate.getValue() != null
                && sourceCondition.getValue().compare(conditionToValidate.getValue()) >= 0) {
            log.warn("Condition {} was expected to be easier than condition {}, but actual result differs.",
                    sourceCondition.getValue(),
                    conditionToValidate.getValue());
            conditionToValidate.setValue(null);
        }
    }

    public void validateThatHarder(ChoiceBox<WinCondition> sourceCondition, ChoiceBox<WinCondition> conditionToValidate) {
        if (sourceCondition.getValue() != null
                && conditionToValidate.getValue() != null
                && sourceCondition.getValue().compare(conditionToValidate.getValue()) <= 0) {
            log.warn("Condition {} was expected to be harder than condition {}, but actual result differs.",
                    sourceCondition.getValue(),
                    conditionToValidate.getValue());
            conditionToValidate.setValue(null);
        }
    }
}
