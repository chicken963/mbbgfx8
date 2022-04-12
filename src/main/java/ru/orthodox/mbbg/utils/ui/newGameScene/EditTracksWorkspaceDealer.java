package ru.orthodox.mbbg.utils.ui.newGameScene;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.controlsfx.control.RangeSlider;
import ru.orthodox.mbbg.enums.ButtonType;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.services.PlayService;
import ru.orthodox.mbbg.utils.SpringUtils;
import ru.orthodox.mbbg.utils.ThreadUtils;
import ru.orthodox.mbbg.utils.ui.ActiveTableRowDealer;
import ru.orthodox.mbbg.utils.ui.ImageButtonCellFactoryProvider;

import java.util.Collections;
import java.util.List;

public class EditTracksWorkspaceDealer {

    private Label currentTrackInfo;
    private Label currentTrackStartLabel;
    private Label currentTrackEndLabel;
    private Label currentSnippetRate;
    private Label currentSnippetLength;
    private HBox sliderContainer;
    private RoundTab tab;
    private AudioTracksTable audioTracksTable;

    private PlayService playService;
    private List<AudioTrack> audioTracks;

    private ActiveTableRowDealer activeTableRowDealer;

    private ImageButtonCellFactoryProvider imageButtonCellFactoryProvider;

    private RangeSliderDealer rangeSliderDealer;

    public EditTracksWorkspaceDealer(RoundTab tab, List<AudioTrack> audioTracks) {
        this.tab = tab;
        this.audioTracks = audioTracks;

        activeTableRowDealer = SpringUtils.getBean(ActiveTableRowDealer.class);
        imageButtonCellFactoryProvider = SpringUtils.getBean(ImageButtonCellFactoryProvider.class);

        currentTrackInfo = tab.getCurrentTrackInfoLabel();
        currentTrackStartLabel =  tab.getCurrentTrackStartLabel();
        currentTrackEndLabel = tab.getCurrentTrackEndLabel();
        currentSnippetRate = tab.getCurrentSnippetRate();
        currentSnippetLength = tab.getCurrentSnippetLength();

        sliderContainer = tab.getSliderContainer();
        audioTracksTable = tab.getAudioTracksTable();

        rangeSliderDealer = buildRangeSliderDealer();

        ThreadUtils.runTaskInSeparateThread(() -> rangeSliderDealer.updateRangeSlider(), "newGamePlayInfo" + tab.getIndex());
        defineRowsLogic(audioTracksTable);
        defineColumnsLogic(audioTracksTable);
    }

    private void defineRowsLogic(AudioTracksTable audioTracksTable) {

        Callback<TableView<AudioTrack>, TableRow<AudioTrack>> rowFactory = tv -> {
            TableRow<AudioTrack> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                this.updateCurrentTrackLayout(row);
                activeTableRowDealer.updateActiveRow(row);
            });
            row.setOnMouseEntered(event -> activeTableRowDealer.updateHoveredRow(row));
            row.setOnMouseExited(event -> activeTableRowDealer.deactivateHoveredRow(row));
            return row;
        };

        audioTracksTable.setRowFactory(rowFactory);
    }

    private void defineColumnsLogic(AudioTracksTable audioTracksTable) {
        TableColumn<AudioTrack, String> artistColumn = audioTracksTable.findArtistColumn();
        TableColumn<AudioTrack, String> titleColumn = audioTracksTable.findTitleColumn();

        defineEditableColumnsLogic(artistColumn, titleColumn);

        TableColumn<AudioTrack, String> playColumn = audioTracksTable.findPlayColumn();
        TableColumn<AudioTrack, String> pauseColumn = audioTracksTable.findPauseColumn();
        TableColumn<AudioTrack, String> stopColumn = audioTracksTable.findStopColumn();
        TableColumn<AudioTrack, String> deleteColumn = audioTracksTable.findDeleteColumn();

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
        audioTracksTable.setAudioTracks(audioTracks);
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
