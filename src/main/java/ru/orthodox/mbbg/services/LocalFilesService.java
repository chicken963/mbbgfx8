package ru.orthodox.mbbg.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.AudioTrack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class LocalFilesService {

    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${audiotracks.json.filepath}")
    private String audioTracksInfoFilePath;

    public List<AudioTrack> readAudioTracksInfo() {
        File audioTracksFile = new File(audioTracksInfoFilePath);
        List<AudioTrack> audioTracks = new ArrayList<>();
        try {
            audioTracks = mapper.readValue(audioTracksFile, new TypeReference<List<AudioTrack>>() {
            });
        } catch (IOException e) {
            if (!audioTracksFile.exists()) {
                log.error("Cannot find audio tracks info file {}", audioTracksInfoFilePath);
            } else {
                e.printStackTrace();
            }
        }
        return audioTracks;
    }

    public void write(AudioTrack audioTrack) {
        File audioTracksFile = new File(audioTracksInfoFilePath);
        List<AudioTrack> availableAudioTracks = this.readAudioTracksInfo();
        availableAudioTracks.add(audioTrack);
        try {
            mapper.writeValue(audioTracksFile, availableAudioTracks);
        } catch (IOException e) {
            if (!audioTracksFile.exists()) {
                log.error("Cannot find audio tracks info file {}", audioTracksInfoFilePath);
            } else {
                e.printStackTrace();
            }
        }
    }

    public List<AudioTrack> checkInvalidTracks() {
        List<AudioTrack> tracksInDb = readAudioTracksInfo();
        List<AudioTrack> invalidTracks = new ArrayList<>();
        for (AudioTrack audioTrack : tracksInDb) {
            if (!audioTrack.getLocalFile().exists()) {
                invalidTracks.add(audioTrack);
            }
        }
        return invalidTracks;
    }
}
