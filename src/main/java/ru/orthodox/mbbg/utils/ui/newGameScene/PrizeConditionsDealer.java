package ru.orthodox.mbbg.utils.ui.newGameScene;

import javafx.scene.control.ChoiceBox;
import ru.orthodox.mbbg.enums.WinCondition;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PrizeConditionsDealer {

    private ChoiceBox firstPrizeCondition;
    private ChoiceBox secondPrizeCondition;
    private ChoiceBox thirdPrizeCondition;

    private final List<String> prizeConditions = Arrays.stream(WinCondition.values())
            .map(WinCondition::getMessage)
            .collect(Collectors.toList());

    public PrizeConditionsDealer(ChoiceBox firstPrizeCondition, ChoiceBox secondPrizeCondition, ChoiceBox thirdPrizeCondition) {
        this.firstPrizeCondition = firstPrizeCondition;
        this.secondPrizeCondition = secondPrizeCondition;
        this.thirdPrizeCondition = thirdPrizeCondition;

        preconfigureWinConditions(Arrays.asList(firstPrizeCondition, secondPrizeCondition, thirdPrizeCondition));
    }


    private void preconfigureWinConditions(List<ChoiceBox> conditionContainers) {
        conditionContainers.forEach(container -> container.getItems().addAll(prizeConditions));
    }
}
