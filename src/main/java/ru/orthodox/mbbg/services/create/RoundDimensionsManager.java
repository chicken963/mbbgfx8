package ru.orthodox.mbbg.services.create;

import javafx.scene.control.ChoiceBox;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.enums.BlankSize;
import ru.orthodox.mbbg.events.TabCreatedEvent;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.model.proxy.play.RoundTab;

@Service
public class RoundDimensionsManager implements ApplicationListener<TabCreatedEvent> {

    public int getBlankDimension(ChoiceBox<BlankSize> blankDimensions) {
        return blankDimensions.getValue().getMainSize();
    }

    @Override
    public void onApplicationEvent(TabCreatedEvent tabCreatedEvent) {
        RoundTab tabToPopulate = tabCreatedEvent.getRoundTab();
        populateDimensionsChoiceBox(tabToPopulate);
        Round roundToUseDefaultData = tabToPopulate.getRound();
        if (roundToUseDefaultData != null) {
            setRoundValue(tabToPopulate);
        }
    }

    private void setRoundValue(RoundTab tabToPopulate) {
        tabToPopulate
                .getBlankDimensions()
                .setValue(BlankSize.withSize(tabToPopulate.getRound().getWidth()));
    }

    private void populateDimensionsChoiceBox(RoundTab roundTab) {
        roundTab.getBlankDimensions().getItems().addAll(BlankSize.values());
    }
}
