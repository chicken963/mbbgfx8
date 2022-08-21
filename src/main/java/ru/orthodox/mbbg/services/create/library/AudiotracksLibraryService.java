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
import ru.orthodox.mbbg.model.proxy.create.EditAudioTracksTable;
import ru.orthodox.mbbg.model.proxy.create.AudioTracksLibraryGrid;
import ru.orthodox.mbbg.services.common.PlayMediaService;
import ru.orthodox.mbbg.services.create.AudioTrackUIViewService;
import ru.orthodox.mbbg.services.create.RangeSliderService;
import ru.orthodox.mbbg.utils.hierarchy.ElementFinder;

import java.util.List;
import java.util.stream.Stream;

import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findElementByTypeAndStyleclass;
import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findRecursivelyByStyleClass;

@Service
public class AudiotracksLibraryService {
    @Autowired
    private AudioTrackRepository audioTrackRepository;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private PlayMediaService playMediaService;
    @Autowired
    private RangeSliderService rangeSliderService;
    @Autowired
    private AudioTrackUIViewService audioTrackUIViewService;

    public AudioTracksLibraryGrid populateTableWithAllAudioTracks(Pane libraryPopupContent) {
        AudioTracksLibraryGrid libraryTable = getAudioTracksLibraryGridFromUI(libraryPopupContent);
        libraryTable.setPlayMediaService(playMediaService);
        libraryTable.addAudioTracks(audioTrackRepository.findAllAudioTracks());
        return libraryTable;
    }


    public void addSelectedTracksToRound(AudioTracksLibraryGrid libraryGrid, EditAudioTracksTable roundTableToSendTheSelectedAudiotracks, Stage stage) {
        List<AudioTrack> selectedAudioTracks = libraryGrid.getSelectedAudiotracks();
        roundTableToSendTheSelectedAudiotracks.addAudioTracks(selectedAudioTracks);
        for (AudioTrack audioTrack: selectedAudioTracks) {
           eventPublisher.publishEvent(new AudioTrackLengthLoadedEvent(this, audioTrack));
        }
        stage.close();
    }

    private AudioTracksLibraryGrid getAudioTracksLibraryGridFromUI(Pane libraryPopupContent) {
        return Stream.of(findElementByTypeAndStyleclass(libraryPopupContent, "tracks-grid"))
                .map(node -> (GridPane) node)
                .map(gridPane -> new AudioTracksLibraryGrid(gridPane, rangeSliderService))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("There is no grid pane found by style class 'tracks-grid'"));
    }

    public Button getAddLibraryTracksButtonFromUI(Pane libraryPopupContent) {
        return ElementFinder.findElementByTypeAndStyleclass(libraryPopupContent, "add-selected-tracks");
    }
}
