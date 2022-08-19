package ru.orthodox.mbbg.services.create.library;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.events.AudioTrackLengthLoadedEvent;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.repositories.AudioTrackRepository;
import ru.orthodox.mbbg.model.proxy.create.AudioTracksGrid;
import ru.orthodox.mbbg.model.proxy.create.AudioTracksLibraryGrid;
import ru.orthodox.mbbg.services.common.PlayMediaService;

import java.util.List;

import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findRecursivelyByStyleClass;

@Service
public class AudiotracksLibraryService {
    @Autowired
    private AudioTrackRepository audioTrackRepository;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private PlayMediaService playMediaService;

    public AudioTracksLibraryGrid populateTableWithAllAudioTracks(Pane libraryPopupContent) {
        AudioTracksLibraryGrid libraryTable = getAudioTracksLibraryGridFromUI(libraryPopupContent);
        libraryTable.setPlayMediaService(playMediaService);
        libraryTable.addAudioTracks(audioTrackRepository.findAllAudioTracks());
        return libraryTable;
    }


    public void addSelectedTracksToRound(AudioTracksLibraryGrid libraryGrid, AudioTracksGrid roundTableToSendTheSelectedAudiotracks, Stage stage) {
        List<AudioTrack> selectedAudioTracks = libraryGrid.getSelectedAudiotracks();
        roundTableToSendTheSelectedAudiotracks.addAudioTracks(selectedAudioTracks);
        for (AudioTrack audioTrack: selectedAudioTracks) {
           eventPublisher.publishEvent(new AudioTrackLengthLoadedEvent(this, audioTrack));
        }
        stage.close();
    }

    private AudioTracksLibraryGrid getAudioTracksLibraryGridFromUI(Pane libraryPopupContent) {
        return findRecursivelyByStyleClass(libraryPopupContent, "tracks-grid")
                .stream()
                .map(node -> (GridPane) node)
                .map(AudioTracksLibraryGrid::new)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("There is no grid pane found by style class 'tracks-grid'"));
    }

    public Button getAddLibraryTracksButtonFromUI(Pane libraryPopupContent) {
        return findRecursivelyByStyleClass(libraryPopupContent, "add-selected-tracks")
                .stream()
                .map(node -> (Button) node)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("There is no grid pane found by style class 'tracks-grid'"));
    }
}
