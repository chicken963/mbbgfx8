package ru.orthodox.mbbg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.services.model.AudioTrackService;

import java.util.List;

public class LocalFilesController {
    @Autowired
    private AudioTrackService audioTrackService;

    @FXML
    private Label invalidTracksMessage;

    public void checkInvalidTracks(){
        List<AudioTrack> invalidAudioTracks = audioTrackService.checkInvalidTracks();
        if (invalidAudioTracks.size() != 0) {
            StringBuilder message = new StringBuilder("The following files do not exist or cannot be played:\n");
            for (AudioTrack audioTrack: invalidAudioTracks) {
                message.append(audioTrack.getLocalPath()).append("\n");
            }
            invalidTracksMessage.setText(message.toString());
        }
    }
}
