package ru.orthodox.mbbg.utils.ui.newGameScene;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Labeled;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

import static ru.orthodox.mbbg.utils.ui.CustomFontDealer.setDefaultFont;
import static ru.orthodox.mbbg.utils.ui.HierarchyUtils.findAllLabelsRecursively;

@Getter
public class RoundsTabPane {
    private final TabPane tabPane;
    private final List<RoundTab> roundTabs;
    private boolean allRoundsAreFilled;

    public RoundsTabPane (TabPane tabPane, List<RoundTab> roundTabs) {
        this.tabPane = tabPane;
        this.roundTabs = roundTabs;
        this.allRoundsAreFilled = false;
        tabPane.getTabs().clear();
        tabPane.getTabs().addAll(roundTabs.stream().map(RoundTab::getTab).collect(Collectors.toList()));
        setDefaultTabNamesToUnnamedRounds();
        setDefaultFont(
            findAllLabelsRecursively(tabPane).toArray(new Labeled[0])
        );
    }

    public RoundTab findTabByChild(Node child) {
        return roundTabs.stream()
                .filter(roundTab -> roundTab.containsChild(child))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Parent tab for table not found"));
    }

    public void addRoundTab(RoundTab roundTab) {
        this.getRoundTabs().add(roundTab);
        roundTab.getTab().setText("Round " + (roundTab.getIndex() + 1));
        tabPane.getTabs().add(roundTab.getTab());
        for (RoundTab tab : this.getRoundTabs()) {
            tab.enableDeleteRoundButton();
        }
        allRoundsAreFilled = false;
        setDefaultFont(
                findAllLabelsRecursively((Parent) roundTab.getTab().getContent()).toArray(new Labeled[0])
        );
    }

    public void removeRoundTab(RoundTab roundTab) {
        this.getRoundTabs().remove(roundTab);
        tabPane.getTabs().remove(roundTab.getTab());
        if (tabPane.getTabs().size() == 1) {
            this.getRoundTabs().get(0).disableDeleteRoundButton();
        }
        setDefaultTabNamesToUnnamedRounds();
    }

    public int getTabsCount() {
        return tabPane.getTabs().size();
    }

    private void setDefaultTabNamesToUnnamedRounds(){
        tabPane.getTabs().stream()
                .filter(tab -> ElementFinder.<TextField>findTabElementByTypeAndStyleclass(tab, "newRoundName").getText().isEmpty())
                .forEach(tab -> tab.setText("Round " + (tabPane.getTabs().indexOf(tab) + 1)));
    }
}
