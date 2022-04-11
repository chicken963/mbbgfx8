package ru.orthodox.mbbg.utils.ui;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import lombok.Builder;
import org.controlsfx.control.RangeSlider;
import ru.orthodox.mbbg.enums.ButtonType;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.services.PlayService;
import ru.orthodox.mbbg.utils.SpringUtils;
import ru.orthodox.mbbg.utils.ThreadUtils;
import ru.orthodox.mbbg.utils.ui.newGameScene.ElementFinder;

import java.util.Collections;
import java.util.List;

import static ru.orthodox.mbbg.utils.ui.newGameScene.ElementFinder.findTracksTableColumnByNumber;

public class EditTracksWorkspaceDealer {

    private Label currentTrackInfo;
    private Label currentTrackStartLabel;
    private Label currentTrackEndLabel;
    private Label currentSnippetRate;
    private Label currentSnippetLength;
    private HBox sliderContainer;
    private Tab tab;
    private TableView<AudioTrack> audioTracksTable;

    private PlayService playService;
    private List<AudioTrack> audioTracks;

    private ActiveTableRowDealer activeTableRowDealer;

    private ImageButtonCellFactoryProvider imageButtonCellFactoryProvider;

    private RangeSliderDealer rangeSliderDealer;

    public EditTracksWorkspaceDealer(Tab tab, List<AudioTrack> audioTracks) {
        this.tab = tab;
        this.audioTracks = audioTracks;

        activeTableRowDealer = SpringUtils.getBean(ActiveTableRowDealer.class);
        imageButtonCellFactoryProvider = SpringUtils.getBean(ImageButtonCellFactoryProvider.class);

        currentTrackInfo = ElementFinder.<Label>findTabElementByTypeAndStyleclass(tab, "currentTrackInfo");
        currentTrackStartLabel = ElementFinder.<Label>findTabElementByTypeAndStyleclass(tab, "currentTrackStartLabel");
        currentTrackEndLabel = ElementFinder.<Label>findTabElementByTypeAndStyleclass(tab, "currentTrackEndLabel");
        currentSnippetRate = ElementFinder.<Label>findTabElementByTypeAndStyleclass(tab, "currentSnippetRate");
        currentSnippetLength = ElementFinder.<Label>findTabElementByTypeAndStyleclass(tab, "currentSnippetLength");

        sliderContainer = ElementFinder.<HBox>findTabElementByTypeAndStyleclass(tab, "sliderContainer");
        audioTracksTable = ElementFinder.<TableView<AudioTrack>>findTabElementByTypeAndStyleclass(tab, "tracksTable");
        rangeSliderDealer = buildRangeSliderDealer();

        ThreadUtils.runTaskInSeparateThread(() -> rangeSliderDealer.updateRangeSlider(), "newGamePlayInfo" + tab.getTabPane().getTabs().indexOf(tab));
        defineRowsLogic(audioTracksTable);
        defineColumnsLogic(audioTracksTable);
    }

    private void defineRowsLogic(TableView<AudioTrack> audioTracksTable) {
        audioTracksTable.setRowFactory(tv -> {
            TableRow<AudioTrack> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                this.updateCurrentTrackLayout(row);
                activeTableRowDealer.updateActiveRow(row);
            });
            row.setOnMouseEntered(event -> activeTableRowDealer.updateHoveredRow(row));
            row.setOnMouseExited(event -> activeTableRowDealer.deactivateHoveredRow(row));
            return row;
        });
    }

    private void defineColumnsLogic(TableView<AudioTrack> audioTracksTable) {
        TableColumn<AudioTrack, String> artistColumn = findTracksTableColumnByNumber(audioTracksTable, 0);
        TableColumn<AudioTrack, String> titleColumn = findTracksTableColumnByNumber(audioTracksTable, 1);
        defineEditableColumnsLogic(artistColumn, titleColumn);

        TableColumn<AudioTrack, String> playColumn = findTracksTableColumnByNumber(audioTracksTable, 2);
        TableColumn<AudioTrack, String> pauseColumn = findTracksTableColumnByNumber(audioTracksTable, 3);
        TableColumn<AudioTrack, String> stopColumn = findTracksTableColumnByNumber(audioTracksTable, 4);
        TableColumn<AudioTrack, String> deleteColumn = findTracksTableColumnByNumber(audioTracksTable, 5);
        defineButtonedColumnsLogic(playColumn, pauseColumn, stopColumn, deleteColumn);
    }

    private void defineEditableColumnsLogic(TableColumn<AudioTrack, String> artistColumn,
                                            TableColumn<AudioTrack, String> titleColumn) {
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        artistColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        artistColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<AudioTrack, String> t) ->
                        (t.getTableView().getItems().get(t.getTablePosition().getRow())).setArtist(t.getNewValue()));

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        titleColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<AudioTrack, String> t) ->
                        (t.getTableView().getItems().get(t.getTablePosition().getRow())).setTitle(t.getNewValue()));

    }

    private void defineButtonedColumnsLogic(TableColumn<AudioTrack, String> playColumn,
                                            TableColumn<AudioTrack, String> pauseColumn,
                                            TableColumn<AudioTrack, String> stopColumn,
                                            TableColumn<AudioTrack, String> deleteColumn) {
        playColumn.setCellFactory(imageButtonCellFactoryProvider.provide(
                "/mediaplayerIcons/play-small.png",
                this::activateTrackAndPlay,
                ButtonType.PLAY));

        pauseColumn.setCellFactory(imageButtonCellFactoryProvider.provide(
                "/mediaplayerIcons/pause-small.png",
                this::pause,
                ButtonType.PAUSE));

        stopColumn.setCellFactory(imageButtonCellFactoryProvider.provide(
                "/mediaplayerIcons/stop-small.png",
                this::stop,
                ButtonType.STOP));

        deleteColumn.setCellFactory(imageButtonCellFactoryProvider.provide(
                "/mediaplayerIcons/delete2.png",
                this::removeFromTable,
                ButtonType.DELETE));
    }

    private void activateTrackAndPlay(AudioTrack audioTrack) {
        double cachedRate = 0;
        if (playService != null && !audioTrack.equals(playService.getCurrentTrack())) {
            playService.stop();
        }
        if (playService == null || playService.isStopped()) {
            this.playService = new PlayService(Collections.singletonList(audioTrack));
        }
        updateCurrentTrackInfoUIElements(playService);
        playService.play();

    }

    private void pause(AudioTrack audioTrack) {
        if (playService != null && audioTrack.equals(playService.getCurrentTrack())) {
            playService.pause();
        }
    }

    private void stop(AudioTrack audioTrack) {
        if (playService != null && audioTrack.equals(playService.getCurrentTrack())) {
            playService.stop();
        }
    }

    private void removeFromTable(AudioTrack audioTrack) {
        audioTracks.remove(audioTrack);
        audioTracksTable.getItems().setAll(audioTracks);
    }

    private void updateCurrentTrackInfoUIElements(PlayService playService) {
        currentTrackInfo.setText(
                playService.getCurrentTrack().getArtist() + " - "
                + playService.getCurrentTrack().getTitle());
        RangeSlider slider = rangeSliderDealer.createAndIntegrateRangeSlider(playService);
        sliderContainer.getChildren().clear();
        sliderContainer.getChildren().addAll(slider);
    }


    private void updateCurrentTrackLayout(TableRow<AudioTrack> row) {
        AudioTrack selectedTrack = row.getItem();
        if (playService != null
                && selectedTrack != null
                && selectedTrack != playService.getCurrentTrack()) {
            playService.stop();
            playService = new PlayService(Collections.singletonList(selectedTrack));
            updateCurrentTrackInfoUIElements(playService);
        }
    }

    private RangeSliderDealer buildRangeSliderDealer() {
        return RangeSliderDealer.builder()
                .currentSnippetRate(currentSnippetRate)
                .currentSnippetLength(currentSnippetLength)
                .currentTrackStartLabel(currentTrackStartLabel)
                .currentTrackEndLabel(currentTrackEndLabel)
                .currentTrackInfo(currentTrackInfo)
                .build();
    }
}
