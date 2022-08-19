package ru.orthodox.mbbg.model.proxy.play;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import lombok.Getter;
import ru.orthodox.mbbg.services.common.EventsHandlingService;
import ru.orthodox.mbbg.utils.hierarchy.ElementFinder;

import java.util.List;
import java.util.stream.Collectors;

import static ru.orthodox.mbbg.utils.common.CustomFontDealer.setDefaultFont;
import static ru.orthodox.mbbg.utils.hierarchy.NodeDeepCopyProvider.createDeepCopy;

@Getter
public class RoundsTabPane {
    private final TabPane tabPane;
    private final List<RoundTab> roundTabs;
    private boolean allRoundsAreFilled;
    private EventsHandlingService eventsHandlingService;

    public RoundsTabPane (TabPane tabPane, Tab tabSample, List<RoundTab> roundTabs, EventsHandlingService eventsHandlingService) {
        this.tabPane = tabPane;
        this.roundTabs = roundTabs;
        this.allRoundsAreFilled = false;
        this.eventsHandlingService = eventsHandlingService;
        tabPane.getTabs().clear();
        tabPane.getTabs().addAll(roundTabs.stream().map(RoundTab::getTab).collect(Collectors.toList()));
        tabPane.getTabs().add(newTabButton(tabPane, tabSample));
        setDefaultTabNamesToUnnamedRounds();
        setDefaultFont(
            ElementFinder.findAllLabelsRecursively(tabPane).toArray(new Labeled[0])
        );
    }

    public RoundTab findTabByChild(Node child) {
        return roundTabs.stream()
                .filter(roundTab -> roundTab.containsChild(child))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Parent tab for table not found"));
    }

    public void addRoundTab(int index, RoundTab roundTab) {
        this.getRoundTabs().add(roundTab);
        roundTab.getTab().setText("Round " + (roundTab.getIndex() + 1));
        tabPane.getTabs().add(index, roundTab.getTab());
        allRoundsAreFilled = false;
        setDefaultFont(
                ElementFinder.findAllLabelsRecursively((Parent) roundTab.getTab().getContent()).toArray(new Labeled[0])
        );
    }

    public void removeRoundTab(RoundTab roundTab) {
        this.getRoundTabs().remove(roundTab);
        tabPane.getTabs().remove(roundTab.getTab());
        if (tabPane.getTabs().size() == 1) {
            this.getTabPane().setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        }
        setDefaultTabNamesToUnnamedRounds();
    }

    public int getTabsCount() {
        return tabPane.getTabs().size();
    }

    private void setDefaultTabNamesToUnnamedRounds(){
        tabPane.getTabs().stream()
                .limit(roundTabs.size())
                .filter(tab -> ElementFinder.<TextField>findTabElementByTypeAndStyleclass(tab, "newRoundName").getText().isEmpty())
                .forEach(tab -> tab.setText("Round " + (tabPane.getTabs().indexOf(tab) + 1)));
    }

    private Tab newTabButton(TabPane tabPane, Tab tabSample) {
        Tab addTab = new Tab("+");
        addTab.setTooltip(new Tooltip("Add round"));
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if(newTab == addTab) {
                RoundTab newTab1 = new RoundTab(createDeepCopy(tabSample), eventsHandlingService, this.getTabsCount() - 1);
                this.addRoundTab(this.getTabsCount() - 1, newTab1);
                tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
                tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2); // Selecting the tab before the button, which is the newly created one
            }
        });
        addTab.setClosable(false);
        return addTab;
    }
}
