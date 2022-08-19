package ru.orthodox.mbbg.model.proxy.play;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import lombok.RequiredArgsConstructor;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.proxy.AudioTracksView;
import ru.orthodox.mbbg.services.common.PlayMediaService;
import ru.orthodox.mbbg.utils.hierarchy.ElementFinder;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class AudioTracksTable implements AudioTracksView {

    private final TableView<AudioTrack> table;

    public void addAudioTracks(List<AudioTrack> audioTracks) {
        List<AudioTrack> buffer = new ArrayList<>(table.getItems());
        buffer.addAll(audioTracks);
        table.getItems().setAll(buffer);
    }

    public void setAudioTracks(List<AudioTrack> audioTracks) {
        table.getItems().setAll(audioTracks);
    }

    public List<AudioTrack> getAudioTracks() {
        return table.getItems();
    }

    public void setRowFactory(Callback<TableView<AudioTrack>, TableRow<AudioTrack>> rowFactory) {
        table.setRowFactory(rowFactory);
    }

    public TableColumn<AudioTrack, String> findArtistColumn(){
        return ElementFinder.findTracksTableColumnByNumber(table, 0);
    }

    public TableColumn<AudioTrack, String> findTitleColumn(){
        return ElementFinder.findTracksTableColumnByNumber(table, 1);
    }

    public TableColumn<AudioTrack, String> findStartTimeColumn() {
        return ElementFinder.findTracksTableColumnByNumber(table, 2);
    }

    public TableColumn<AudioTrack, String> findEndTimeColumn() {
        return ElementFinder.findTracksTableColumnByNumber(table, 3);
    }

    public TableColumn<AudioTrack, String> findProgressColumn() {
        return ElementFinder.findTracksTableColumnByNumber(table, 4);
    }

    public TableColumn<AudioTrack, String> findTimelineColumn() {
        return ElementFinder.findTracksTableColumnByNumber(table, 5);
    }

    public TableColumn<AudioTrack, String> findPlayColumn(){
        return ElementFinder.findTracksTableColumnByNumber(table, 6);
    }

    public TableColumn<AudioTrack, String> findPauseColumn(){
        return ElementFinder.findTracksTableColumnByNumber(table, 7);
    }

    public TableColumn<AudioTrack, String> findStopColumn(){
        return ElementFinder.findTracksTableColumnByNumber(table, 8);
    }

    public TableColumn<AudioTrack, String> findDeleteColumn(){
        return ElementFinder.findTracksTableColumnByNumber(table, 9);
    }

    public Label getPlaceholder() {
        return (Label) table.getPlaceholder();
    }

    @Override
    public void setPlayMediaService(PlayMediaService playMediaService) {

    }

    public boolean isEmpty() {
        return table.getItems().isEmpty();
    }

    public boolean isFilled() {
        return table.getItems().stream().allMatch(audioTrack ->
            audioTrack.getTitle() != null && audioTrack.getArtist() != null
        );
    }
}
