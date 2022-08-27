package ru.orthodox.mbbg.services.create.library;

import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.events.AudioTrackLengthLoadedEvent;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.proxy.create.AudioTracksLibraryTable;
import ru.orthodox.mbbg.model.proxy.create.EditAudioTracksTable;
import ru.orthodox.mbbg.model.proxy.play.RoundTab;
import ru.orthodox.mbbg.repositories.AudioTrackRepository;
import ru.orthodox.mbbg.services.common.PlayMediaService;
import ru.orthodox.mbbg.utils.hierarchy.ElementFinder;

import java.util.List;
import java.util.stream.Stream;

import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findElementByTypeAndStyleclass;

@Service
public class AudiotracksLibraryService {
    @Autowired
    private AudioTrackRepository audioTrackRepository;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private PlayMediaService playMediaService;


    public void populateTableWithAllAudioTracks(AudioTracksLibraryTable libraryTable) {
        libraryTable.setPlayMediaService(playMediaService);
        libraryTable.addAudioTracks(audioTrackRepository.findAllAudioTracks());
    }


    public void defineSubmitProperty(AudioTracksLibraryTable audioTracksLibraryTable, RoundTab currentTab, Stage libraryStage) {
        audioTracksLibraryTable.getAddSelectedTracksButton()
                .setOnAction(e -> addSelectedTracksToRound(audioTracksLibraryTable, currentTab.getEditAudioTracksTable(), libraryStage));
    }

    public void addSelectedTracksToRound(AudioTracksLibraryTable audioTracksLibraryTable, EditAudioTracksTable roundTableToSendTheSelectedAudiotracks, Stage stage) {
        List<AudioTrack> selectedAudioTracks = audioTracksLibraryTable.getSelectedAudiotracks();
        roundTableToSendTheSelectedAudiotracks.addAudioTracks(selectedAudioTracks);
        for (AudioTrack audioTrack: selectedAudioTracks) {
            eventPublisher.publishEvent(new AudioTrackLengthLoadedEvent(this, audioTrack));
        }
        stage.close();
    }
}
