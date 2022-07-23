package ru.orthodox.mbbg.ui.modelExtensions.newGameScene;

import javafx.scene.control.Label;
import ru.orthodox.mbbg.model.AudioTrack;

import java.util.List;

public interface AudioTracksView {

    List<AudioTrack> getAudioTracks();

    public Label getPlaceholder();

/*    TableColumn<AudioTrack, String> findArtistColumn();

    TableColumn<AudioTrack, String> findTitleColumn();

    TableColumn<AudioTrack, String> findStartTimeColumn();

    TableColumn<AudioTrack, String> findEndTimeColumn();

    TableColumn<AudioTrack, String> findProgressColumn();

    TableColumn<AudioTrack, String> findTimelineColumn();

    TableColumn<AudioTrack, String> findPlayColumn();

    TableColumn<AudioTrack, String> findPauseColumn();

    TableColumn<AudioTrack, String> findStopColumn();
    TableColumn<AudioTrack, String> findDeleteColumn();*/

    boolean isEmpty();

    boolean isFilled();

    void addAudioTracks(List<AudioTrack> audioTracks);
}
