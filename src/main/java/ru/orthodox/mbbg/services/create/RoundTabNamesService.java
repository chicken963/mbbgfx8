package ru.orthodox.mbbg.services.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.events.RoundNameChangedEvent;
import ru.orthodox.mbbg.events.TabAddedEvent;
import ru.orthodox.mbbg.events.TabClosedEvent;
import ru.orthodox.mbbg.model.proxy.play.RoundTab;
import ru.orthodox.mbbg.model.proxy.play.RoundsTabPane;

@Service
public class RoundTabNamesService {
    @Autowired
    private RoundsTabPane roundsTabPane;

    @EventListener
    public void onTabClosed(TabClosedEvent tabClosedEvent) {
        recalculateTabNames();
    }

    @EventListener
    public void onTabAdded(TabAddedEvent tabAddedEvent) {
        recalculateTabNames();
    }

    @EventListener
    public void onTabClosed(RoundNameChangedEvent roundNameChangedEvent) {
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
