package ru.orthodox.mbbg.utils.ui.newGameScene;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import lombok.RequiredArgsConstructor;
import ru.orthodox.mbbg.model.AudioTrack;

import java.util.List;

import static ru.orthodox.mbbg.utils.ui.newGameScene.ElementFinder.findTracksTableColumnByNumber;

@RequiredArgsConstructor
public class AudioTracksTable {

    private final TableView<AudioTrack> table;

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
        return findTracksTableColumnByNumber(table, 0);
    }

    public TableColumn<AudioTrack, String> findTitleColumn(){
        return findTracksTableColumnByNumber(table, 1);
    }

    public TableColumn<AudioTrack, String> findPlayColumn(){
        return findTracksTableColumnByNumber(table, 2);
    }

    public TableColumn<AudioTrack, String> findPauseColumn(){
        return findTracksTableColumnByNumber(table, 3);
    }

    public TableColumn<AudioTrack, String> findStopColumn(){
        return findTracksTableColumnByNumber(table, 4);
    }

    public TableColumn<AudioTrack, String> findDeleteColumn(){
        return findTracksTableColumnByNumber(table, 5);
    }

    public Label getPlaceholder() {
        return (Label) table.getPlaceholder();
    }

    public boolean isEmpty() {
        return table.getItems().isEmpty();
    }
}
