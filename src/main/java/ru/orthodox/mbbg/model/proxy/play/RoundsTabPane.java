package ru.orthodox.mbbg.model.proxy.play;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.orthodox.mbbg.services.common.AudioTrackAsyncLengthLoadService;
import ru.orthodox.mbbg.services.common.PlayMediaService;
import ru.orthodox.mbbg.services.create.AudioTrackUIViewService;
import ru.orthodox.mbbg.services.create.RangeSliderService;
import ru.orthodox.mbbg.utils.hierarchy.ElementFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.orthodox.mbbg.utils.common.CustomFontDealer.setDefaultFont;
import static ru.orthodox.mbbg.utils.hierarchy.NodeDeepCopyProvider.createDeepCopy;

@Getter
@Component
public class RoundsTabPane {
    private TabPane tabPane;
    private Tab tabSample;
    private HBox audioTracksGridRowTemplate;

    private List<RoundTab> roundTabs;
    private boolean allRoundsAreFilled;
    @Autowired
    private AudioTrackAsyncLengthLoadService audioTrackAsyncLengthLoadService;
    @Autowired
    private AudioTrackUIViewService audioTrackUIViewService;
    @Autowired
    private RangeSliderService rangeSliderService;
    @Autowired
    private PlayMediaService playMediaService;

    public void configureUIElements(TabPane tabPane, Tab tabSample, HBox audioTracksGridRowTemplate) {
        this.tabPane = tabPane;
        this.tabSample = tabSample;
        this.audioTracksGridRowTemplate = audioTracksGridRowTemplate;
        setDefaultFont(
//                ElementFinder.findAllLabelsRecursively(tabPane).toArray(new Labeled[0])
                ElementFinder.findAllLabelsRecursively((Parent) tabSample.getContent()).toArray(new Labeled[0])
        );
    }

    public void renderEmpty() {
        RoundTab firstTab = new RoundTab(createDeepCopy(tabSample),
                audioTracksGridRowTemplate,
                audioTrackAsyncLengthLoadService,
                rangeSliderService,
                audioTrackUIViewService,
                playMediaService,
                0);
        this.roundTabs = new ArrayList<RoundTab>() {{
            add(firstTab);
        }};
        this.allRoundsAreFilled = false;
        tabPane.getTabs().setAll(roundTabs.stream().map(RoundTab::getTab).collect(Collectors.toList()));
        tabPane.getTabs().add(newTabButton(tabPane, tabSample));
        setDefaultTabNamesToUnnamedRounds();

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
                RoundTab newTab1 = new RoundTab(
                        createDeepCopy(tabSample),
                        audioTracksGridRowTemplate,
                        audioTrackAsyncLengthLoadService,
                        rangeSliderService,
                        audioTrackUIViewService,
                        playMediaService,
                        this.getTabsCount() - 1);
                this.addRoundTab(this.getTabsCount() - 1, newTab1);
                tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
                tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2); // Selecting the tab before the button, which is the newly created one
            }
        });
        addTab.setClosable(false);
        return addTab;
    }
}
