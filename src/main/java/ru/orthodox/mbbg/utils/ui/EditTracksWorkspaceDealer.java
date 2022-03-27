package ru.orthodox.mbbg.utils.ui;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
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

import java.util.Collections;
import java.util.List;

@Builder
public class EditTracksWorkspaceDealer {

    private TableView<AudioTrack> tracksTable;
    private TableColumn<AudioTrack, String> artist;
    private TableColumn<AudioTrack, String> title;
    private TableColumn<AudioTrack, String> play;
    private TableColumn<AudioTrack, String> pause;
    private TableColumn<AudioTrack, String> stop;
    private TableColumn<AudioTrack, String> remove;
    private HBox sliderContainer;
    private Label currentTrackInfo;
    private Label currentTrackStartLabel;
    private Label currentTrackEndLabel;
    private Label currentSnippetRate;
    private Label currentSnippetLength;

    private PlayService playService;
    private List<AudioTrack> audioTracks;

    private ActiveTableRowDealer activeTableRowDealer;

    private ImageButtonCellFactoryProvider imageButtonCellFactoryProvider;

    private RangeSliderDealer rangeSliderDealer;

    public void defineWorkspaceLogic() {
        activeTableRowDealer = SpringUtils.getBean(ActiveTableRowDealer.class);
        imageButtonCellFactoryProvider = SpringUtils.getBean(ImageButtonCellFactoryProvider.class);

        rangeSliderDealer = buildRangeSliderDealer();
        ThreadUtils.runTaskInSeparateThread(() -> rangeSliderDealer.updateRangeSlider(), "newGamePlayInfo");
        defineRowsLogic();
        defineColumnsLogic();
    }

    private void defineRowsLogic() {
        tracksTable.setRowFactory(tv -> {
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

    private void defineColumnsLogic() {
        defineEditableColumnsLogic();
        defineButtonedColumnsLogic();
    }

    private void defineEditableColumnsLogic() {
        artist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        artist.setCellFactory(TextFieldTableCell.forTableColumn());
        artist.setOnEditCommit(
                (TableColumn.CellEditEvent<AudioTrack, String> t) ->
                        (t.getTableView().getItems().get(t.getTablePosition().getRow())).setArtist(t.getNewValue()));

        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        title.setCellFactory(TextFieldTableCell.forTableColumn());
        title.setOnEditCommit(
                (TableColumn.CellEditEvent<AudioTrack, String> t) ->
                        (t.getTableView().getItems().get(t.getTablePosition().getRow())).setTitle(t.getNewValue()));

    }

    private void defineButtonedColumnsLogic() {
        play.setCellFactory(imageButtonCellFactoryProvider.provide(
                "/mediaplayerIcons/play-small.png",
                this::activateTrackAndPlay,
                ButtonType.PLAY));

        pause.setCellFactory(imageButtonCellFactoryProvider.provide(
                "/mediaplayerIcons/pause-small.png",
                this::pause,
                ButtonType.PAUSE));

        stop.setCellFactory(imageButtonCellFactoryProvider.provide(
                "/mediaplayerIcons/stop-small.png",
                this::stop,
                ButtonType.STOP));

        remove.setCellFactory(imageButtonCellFactoryProvider.provide(
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
        tracksTable.getItems().setAll(audioTracks);
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
