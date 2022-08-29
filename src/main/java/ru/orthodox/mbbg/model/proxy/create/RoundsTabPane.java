package ru.orthodox.mbbg.model.proxy.create;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.orthodox.mbbg.enums.EntityUpdateMode;
import ru.orthodox.mbbg.events.GameAudioTracksListChangedEvent;
import ru.orthodox.mbbg.events.TabAddedEvent;
import ru.orthodox.mbbg.events.TabClosedEvent;
import ru.orthodox.mbbg.events.TextFieldChangeEvent;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.services.common.AudioTrackAsyncLengthLoadService;
import ru.orthodox.mbbg.services.common.EventPublisherService;
import ru.orthodox.mbbg.services.common.PlayMediaService;
import ru.orthodox.mbbg.services.create.RangeSliderService;
import ru.orthodox.mbbg.utils.common.ThreadUtils;
import ru.orthodox.mbbg.utils.hierarchy.ElementFinder;

import javax.annotation.PostConstruct;
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
    @Setter
    @Getter
    private Game game;

    private List<RoundTab> roundTabs = new ArrayList<>();
    private final List<AudioTrackEditUIView> gameGridRows = new ArrayList<>();
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private PlayMediaService playMediaService;
    @Autowired
    private AudioTrackAsyncLengthLoadService audioTrackAsyncLengthLoadService;
    @Autowired
    private EventPublisherService eventPublisherService;
    @Autowired
    private RangeSliderService rangeSliderService;

    @PostConstruct
    public void setUp() {
        audioTrackAsyncLengthLoadService.setGridRows(gameGridRows);
        ThreadUtils.runTaskInSeparateThread(() -> rangeSliderService.updateRangeSlider(gameGridRows), "editTable-" + this.hashCode());
    }

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
        this.game = new Game();
        gameGridRows.clear();
        Round firstRound = new Round();
        RoundTab firstTab = new RoundTab(
                createDeepCopy(tabSample),
                audioTracksGridRowTemplate,
                playMediaService,
                eventPublisherService, firstRound);
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
                eventPublisherService, round);
    }

    public void renderGame(Game game) {
        this.game = game;
        this.roundTabs = game.getRounds().stream()
                .map(this::generateRoundTabBasedOnRound)
                .collect(Collectors.toList());
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
        addTab.getStyleClass().add("plus-tab");
        addTab.setTooltip(new Tooltip("Add round"));
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if(newTab == addTab) {
                Round round = new Round();
                RoundTab newTab1 = new RoundTab(
                        createDeepCopy(tabSample),
                        audioTracksGridRowTemplate,
                        playMediaService,
                        eventPublisherService,
                        round);
                this.addRoundTab(this.getTabsCount() - 1, newTab1);
                tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
                tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2); // Selecting the tab before the button, which is the newly created one
            }
        });
        addTab.setClosable(false);
        return addTab;
    }
}
