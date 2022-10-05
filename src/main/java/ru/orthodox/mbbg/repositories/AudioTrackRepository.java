package ru.orthodox.mbbg.repositories;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.services.common.LocalFilesService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AudioTrackRepository {

    @Autowired
    private LocalFilesService localFilesService;

    @Value("${audiotracks.json.filepath}")
    private String audioTracksInfoFilePath;

    private File audioTracksFile;

    @PostConstruct
    private void init() {
        this.audioTracksFile = localFilesService.createOrUseLocalFile(audioTracksInfoFilePath);
    }

    public List<AudioTrack> findAllAudioTracks() {
        return localFilesService.readEntitiesFromFile(audioTracksFile, AudioTrack.class);
    }

    public Optional<AudioTrack> findById(UUID id) {
        return findAllAudioTracks().stream()
                .filter(audioTrack -> audioTrack.getId().equals(id))
                .findFirst();
    }

    public List<AudioTrack> findByIds(List<UUID> ids) {
        return findAllAudioTracks().stream()
                .filter(audioTrack -> ids.contains(audioTrack.getId()))
                .collect(Collectors.toList());
    }

    public void save(AudioTrack audioTrack) {
        localFilesService.write(audioTrack, audioTracksFile);
    }

    public void save(List<AudioTrack> audioTracks) {
        localFilesService.write(audioTracks, audioTracksFile);
    }

    private boolean isUnique(AudioTrack audioTrack) {
        List<AudioTrack> allAudioTracks = findAllAudioTracks();
        return allAudioTracks.stream()
                .noneMatch(at -> at.equals(audioTrack));
    }

    public void delete(AudioTrack trackToDelete) {
        localFilesService.delete(trackToDelete, audioTracksFile);
    }

    public void delete(List<AudioTrack> tracksToDelete) {
        localFilesService.delete(tracksToDelete, audioTracksFile);
    }
}
