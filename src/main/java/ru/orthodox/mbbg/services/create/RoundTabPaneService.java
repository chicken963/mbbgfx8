package ru.orthodox.mbbg.services.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.events.RoundNameChangedEvent;
import ru.orthodox.mbbg.events.TabAddedEvent;
import ru.orthodox.mbbg.events.TabClosedEvent;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.model.proxy.create.RoundsTabPane;

@Service
public class RoundTabPaneService {
    @Autowired
    private RoundsTabPane roundsTabPane;

    @EventListener
    public void onTabClosed(TabClosedEvent tabClosedEvent) {
        recalculateTabNames();
        roundsTabPane.getGame().getRounds().remove(tabClosedEvent.getRoundTab().getRound());
    }

    @EventListener
    public void onTabAdded(TabAddedEvent tabAddedEvent) {
        recalculateTabNames();
        Round addedRound = tabAddedEvent.getRoundTab().getRound();
        roundsTabPane.getGame().getRounds().add(addedRound);
    }

    @EventListener
    public void onRoundNameChanged(RoundNameChangedEvent roundNameChangedEvent) {
        recalculateTabNames();
    }

    private void recalculateTabNames() {
        roundsTabPane.getRoundTabs()
                .forEach(roundTab -> {
                            String roundNameInputValue = roundTab.getNewRoundNameTextField().getText();
                            int tabNumber = roundsTabPane.getRoundTabs().indexOf(roundTab) + 1;
                            roundTab.getTab().setText(roundNameInputValue.isEmpty()
                                    ? "Round " + tabNumber
                                    : roundNameInputValue);
                        }
                );
    }
}
