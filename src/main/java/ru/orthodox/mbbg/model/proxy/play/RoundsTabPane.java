package ru.orthodox.mbbg.model.proxy.play;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.orthodox.mbbg.enums.BlankSize;
import ru.orthodox.mbbg.enums.EntityUpdateMode;
import ru.orthodox.mbbg.events.GameAudioTracksListChangedEvent;
import ru.orthodox.mbbg.events.TabAddedEvent;
import ru.orthodox.mbbg.events.TabClosedEvent;
import ru.orthodox.mbbg.events.TextFieldChangeEvent;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.model.proxy.create.AudioTrackEditUIView;
import ru.orthodox.mbbg.services.common.AudioTrackAsyncLengthLoadService;
import ru.orthodox.mbbg.services.common.EventPublisherService;
import ru.orthodox.mbbg.services.common.PlayMediaService;
import ru.orthodox.mbbg.utils.hierarchy.ElementFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.orthodox.mbbg.utils.common.CustomFontDealer.setDefaultFont;
import static ru.orthodox.mbbg.utils.hierarchy.NodeDeepCopyProvider.createDeepCopy;

@Getter
@Component
public class RoundsTabPane {
    private TabPane tabPane;
    private Tab tabSample;
    private HBox audioTracksGridRowTemplate;
    @Getter
    private Game game;

    private List<RoundTab> roundTabs = new ArrayList<>();
    List<AudioTrackEditUIView> gameGridRows = new ArrayList<>();
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private PlayMediaService playMediaService;
    @Autowired
    private AudioTrackAsyncLengthLoadService audioTrackAsyncLengthLoadService;
    @Autowired
    private EventPublisherService eventPublisherService;

    @EventListener
    public void onGameAudioTracksListChanged(GameAudioTracksListChangedEvent gameAudioTracksListChangedEvent) {
        AudioTrackEditUIView affectedRow = gameAudioTracksListChangedEvent.getRow();
        if (EntityUpdateMode.ADD.equals(gameAudioTracksListChangedEvent.getMode())) {
            gameGridRows.add(affectedRow);
        } else if (EntityUpdateMode.DELETE.equals(gameAudioTracksListChangedEvent.getMode())) {
            gameGridRows.remove(affectedRow);
        }
    }

    @EventListener
    public void onTabClosed(TabClosedEvent tabClosedEvent) {
        RoundTab tabToDelete = tabClosedEvent.getRoundTab();
        this.getRoundTabs().remove(tabToDelete);
        eventPublisherService.publishEvent(new TextFieldChangeEvent(this));
    }

    public void configureUIElements(TabPane tabPane, Tab tabSample, HBox audioTracksGridRowTemplate) {
        this.tabPane = tabPane;
        this.tabSample = tabSample;
        this.audioTracksGridRowTemplate = audioTracksGridRowTemplate;
        setDefaultFont(
                ElementFinder.findAllLabelsRecursively((Parent) tabSample.getContent()).toArray(new Labeled[0])
        );
    }

    public void renderEmpty() {
        gameGridRows = new ArrayList<>();
        audioTrackAsyncLengthLoadService.setGridRows(gameGridRows);
        RoundTab firstTab = generateRoundTabBasedOnRound(null);
        this.roundTabs = new ArrayList<RoundTab>() {{
            add(firstTab);
        }};
        tabPane.getTabs().setAll(roundTabs.stream().map(RoundTab::getTab).collect(Collectors.toList()));
        eventPublisherService.publishEvent(new TabAddedEvent(this, firstTab));
        tabPane.getTabs().add(newTabButton(tabPane, tabSample));

    }

    private RoundTab generateRoundTabBasedOnRound(Round round) {
        return new RoundTab(createDeepCopy(tabSample),
                audioTracksGridRowTemplate,
                playMediaService,
                eventPublisherService,
                round);
    }

    public void renderGame(Game game) {
        this.roundTabs = game.getRounds().stream().map(round -> {
            RoundTab tab = generateRoundTabBasedOnRound(round);
            tab.getTab().setText(round.getName());
            tab.getNumberOfBlanks().setText(round.getNumberOfBlanks().toString());
            tab.getNewRoundNameTextField().setText(round.getName());
            tab.getEditAudioTracksTable().addAudioTracks(round.getAudioTracks());
            return tab;
        }).collect(Collectors.toList());
        tabPane.getTabs().setAll(roundTabs.stream().map(RoundTab::getTab).collect(Collectors.toList()));
        tabPane.getTabs().add(newTabButton(tabPane, tabSample));
    }

    public Optional<RoundTab> findTabByChild(Node child) {
        return roundTabs.stream()
                .filter(roundTab -> roundTab.containsChild(child))
                .findFirst();
    }

    public void addRoundTab(int index, RoundTab roundTab) {
        this.getRoundTabs().add(roundTab);
        tabPane.getTabs().add(index, roundTab.getTab());
        eventPublisherService.publishEvent(new TabAddedEvent(this, roundTab));
        setDefaultFont(
                ElementFinder.findAllLabelsRecursively((Parent) roundTab.getTab().getContent()).toArray(new Labeled[0])
        );
    }

    public int getTabsCount() {
        return tabPane.getTabs().size();
    }

    private Tab newTabButton(TabPane tabPane, Tab tabSample) {
        Tab addTab = new Tab("+");
        addTab.setTooltip(new Tooltip("Add round"));
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if(newTab == addTab) {
                RoundTab newTab1 = new RoundTab(
                        createDeepCopy(tabSample),
                        audioTracksGridRowTemplate,
                        playMediaService,
                        eventPublisherService,
                        null);
                this.addRoundTab(this.getTabsCount() - 1, newTab1);
                tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
                tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2); // Selecting the tab before the button, which is the newly created one
            }
        });
        addTab.setClosable(false);
        return addTab;
    }
}
