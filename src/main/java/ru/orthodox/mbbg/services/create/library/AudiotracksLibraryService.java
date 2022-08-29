package ru.orthodox.mbbg.services.create.library;

import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.proxy.create.AudioTracksLibraryTable;
import ru.orthodox.mbbg.model.proxy.create.EditAudioTracksTable;
import ru.orthodox.mbbg.model.proxy.create.RoundTab;
import ru.orthodox.mbbg.repositories.AudioTrackRepository;
import ru.orthodox.mbbg.services.common.PlayMediaService;

import java.util.List;

@Service
public class AudiotracksLibraryService {
    @Autowired
    private AudioTrackRepository audioTrackRepository;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private PlayMediaService playMediaService;
    @Autowired
    private AudioTracksLibraryTable libraryTable;


    public void populateTableWithAllAudioTracks() {
        libraryTable.addAudioTracks(audioTrackRepository.findAllAudioTracks());
    }


    public void defineSubmitProperty(RoundTab currentTab, Stage libraryStage) {
        libraryTable.getAddSelectedTracksButton()
                .setOnAction(e -> addSelectedTracksToRound(currentTab.getEditAudioTracksTable(), libraryStage));
    }

    public void addSelectedTracksToRound(EditAudioTracksTable roundTableToSendTheSelectedAudiotracks, Stage stage) {
        List<AudioTrack> selectedAudioTracks = libraryTable.getSelectedAudiotracks();
        roundTableToSendTheSelectedAudiotracks.addAudioTracks(selectedAudioTracks);
        roundTableToSendTheSelectedAudiotracks.getRound().getAudioTracks().addAll(selectedAudioTracks);
        libraryTable.clear();
        stage.close();
    }
}
