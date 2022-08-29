package ru.orthodox.mbbg.services.create;

import javafx.scene.control.ChoiceBox;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.enums.BlankSize;
import ru.orthodox.mbbg.events.BlankDimensionsChangedEvent;
import ru.orthodox.mbbg.events.TabCreatedEvent;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.model.proxy.create.RoundTab;

@Service
public class RoundDimensionsManager {

    public int getBlankDimension(ChoiceBox<BlankSize> blankDimensions) {
        return blankDimensions.getValue().getMainSize();
    }

    @EventListener
    public void onTabCreatedEvent(TabCreatedEvent tabCreatedEvent) {
        RoundTab tabToPopulate = tabCreatedEvent.getRoundTab();
        populateDimensionsChoiceBox(tabToPopulate);
        Round roundToUseDefaultData = tabToPopulate.getRound();
        if (roundToUseDefaultData != null && roundToUseDefaultData.getWidth() != null) {
            setRoundValue(tabToPopulate);
        }
    }

    @EventListener
    public void onBlankDimensionsChangedEvent(BlankDimensionsChangedEvent blankDimensionsChangedEvent) {
        RoundTab tab = blankDimensionsChangedEvent.getTab();
        Round roundToUpdate = tab.getRound();
        roundToUpdate.setWidth(tab.getBlankDimensions().getValue().getMainSize());
        roundToUpdate.setHeight(tab.getBlankDimensions().getValue().getMainSize());
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
