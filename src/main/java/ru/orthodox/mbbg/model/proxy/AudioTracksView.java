package ru.orthodox.mbbg.model.proxy;

import javafx.scene.control.Label;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.services.common.PlayMediaService;

import java.util.List;

public interface AudioTracksView {

    List<AudioTrack> getAudioTracks();

    public Label getPlaceholder();

    public void setPlayMediaService(PlayMediaService playMediaService);

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
