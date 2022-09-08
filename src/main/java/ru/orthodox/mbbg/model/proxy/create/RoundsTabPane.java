package ru.orthodox.mbbg.model.proxy.create;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.orthodox.mbbg.enums.BlanksStatus;
import ru.orthodox.mbbg.enums.EntityUpdateMode;
import ru.orthodox.mbbg.events.create.ActiveRowChangedEvent;
import ru.orthodox.mbbg.events.create.gameResave.GameOutdatedEvent;
import ru.orthodox.mbbg.events.create.gameResave.blankStatusImpact.AudioTrackTextFieldEditRequestedEvent;
import ru.orthodox.mbbg.events.create.gameResave.blankStatusImpact.BlankOutdatedEvent;
import ru.orthodox.mbbg.events.create.gameResave.blankStatusImpact.GameAudioTracksListChangedEvent;
import ru.orthodox.mbbg.events.create.gameResave.blankStatusImpact.tab.TabAddedEvent;
import ru.orthodox.mbbg.events.create.gameResave.blankStatusImpact.tab.TabClosedEvent;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.services.common.AudioTrackAsyncLengthLoadService;
import ru.orthodox.mbbg.services.common.EventPublisherService;
import ru.orthodox.mbbg.services.common.PlayMediaService;
import ru.orthodox.mbbg.services.common.VolumeSliderService;
import ru.orthodox.mbbg.services.create.RangeSliderService;
import ru.orthodox.mbbg.services.create.library.AudiotracksLibraryService;
import ru.orthodox.mbbg.utils.hierarchy.ElementFinder;
import ru.orthodox.mbbg.utils.screen.ScreenService;

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

    private AudioTracksTable playedTable;
    private AudioTrackEditUIView currentlyEditedRow;

    private List<RoundTab> roundTabs = new ArrayList<>();
    private final List<AudioTrackEditUIView> gameGridRows = new ArrayList<>();
    @Autowired
    private PlayMediaService playMediaService;
    @Autowired
    private AudioTrackAsyncLengthLoadService audioTrackAsyncLengthLoadService;
    @Autowired
    private EventPublisherService eventPublisherService;
    @Autowired
    private RangeSliderService rangeSliderService;
    @Autowired
    private VolumeSliderService volumeSliderService;
    @Autowired
    private ScreenService screenService;
    @Autowired
    private AudiotracksLibraryService audiotracksLibraryService;
    @Autowired
    private AudioTracksLibraryTable audioTracksLibraryTable;

    @PostConstruct
    public void setUp() {
        audioTrackAsyncLengthLoadService.setGridRows(gameGridRows);
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
        gameGridRows.removeAll(tabToDelete.getEditAudioTracksTable().getRows());
        if (tabToDelete.getEditAudioTracksTable() == playedTable) {
            playMediaService.stop();
        }
    }

    @EventListener
    public void onActiveRowChanged(ActiveRowChangedEvent activeRowChangedEvent) {
        AudioTrackEditUIView newActiveRow = activeRowChangedEvent.getRow();
        rangeSliderService.setActiveRow(newActiveRow);
        this.playedTable = activeRowChangedEvent.getTable();
    }

    @EventListener
    public void onAudioTrackTextFieldEditRequested(AudioTrackTextFieldEditRequestedEvent audioTrackTextFieldEditRequestedEvent) {
        AudioTrackEditUIView newEditedRow = audioTrackTextFieldEditRequestedEvent.getRow();
        if (currentlyEditedRow != null) {
            saveAudioTrackWithCurrentInputValues(currentlyEditedRow);
        }
        currentlyEditedRow = newEditedRow;
    }

    @EventListener
    private void onBlanksInvalidatedEvent(BlankOutdatedEvent blankOutdatedEvent) {
        if (game != null) {
            if (BlanksStatus.ACTUALIZED.equals(game.getBlanksStatus())) {
                game.setBlanksStatus(BlanksStatus.OUTDATED);
            }
        }
    }

    @EventListener
    public void onGameRevalidateRequiredEvent(GameOutdatedEvent gameOutdatedEvent) {
        game.setHavingUnsavedChanges(true);
    }

    private void saveAudioTrackWithCurrentInputValues(AudioTrackEditUIView row) {
        AudioTrack editedAudioTrack = row.getAudioTrack();
        TextField artistField = (TextField) row.getArtistLabel();
        TextField songTitleField = (TextField) row.getSongTitleLabel();
        editedAudioTrack.setArtist(artistField.getText());
        editedAudioTrack.setTitle(songTitleField.getText());
    }

    public void configureUIElements(TabPane tabPane, Tab tabSample, HBox audioTracksGridRowTemplate, HBox volumeSliderContainer) {
        this.tabPane = tabPane;
        this.tabSample = tabSample;
        this.audioTracksGridRowTemplate = audioTracksGridRowTemplate;
        setDefaultFont(
                ElementFinder.findAllLabelsRecursively((Parent) tabSample.getContent()).toArray(new Labeled[0])
        );
        VolumeSlider volumeSlider = volumeSliderService.createNewSlider();
        volumeSliderContainer.getChildren().setAll(volumeSlider.getRoot());
    }

    public void renderEmpty() {
        this.game = new Game();
        gameGridRows.clear();
        RoundTab firstTab = new RoundTab(
                createDeepCopy(tabSample),
                audioTracksGridRowTemplate,
                playMediaService,
                eventPublisherService,
                audiotracksLibraryService,
                audioTracksLibraryTable,
                screenService,
                new Round());
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
                audiotracksLibraryService,
                audioTracksLibraryTable,
                screenService,
                round);
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
                RoundTab newTab1 = new RoundTab(
                        createDeepCopy(tabSample),
                        audioTracksGridRowTemplate,
                        playMediaService,
                        eventPublisherService,
                        audiotracksLibraryService,
                        audioTracksLibraryTable,
                        screenService,
                        new Round());
                this.addRoundTab(this.getTabsCount() - 1, newTab1);
                tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
                tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2); // Selecting the tab before the button, which is the newly created one
            }
        });
        addTab.setClosable(false);
        return addTab;
    }
}
