package ru.orthodox.mbbg.services.model;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.AudioTrack;

import ru.orthodox.mbbg.services.LocalFilesService;
import ru.orthodox.mbbg.utils.AudioUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AudioTrackService {

    @Autowired
    private LocalFilesService localFilesService;

    @Value("${audiotracks.json.filepath}")
    private String audioTracksInfoFilePath;

    private File audioTracksFile;

    private static final String[] SEPARATORS = {"_-_", " - ", "-", "_"};
    Pattern digitPattern = Pattern.compile("\\d+");

    @PostConstruct
    private void init() {
        this.audioTracksFile = new File(audioTracksInfoFilePath);
    }

    public List<AudioTrack> findAllAudioTracks() {
        return localFilesService.readEntityListFromFile(audioTracksFile, AudioTrack.class);
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
        if (isUnique(audioTrack)) {
            localFilesService.write(audioTrack, audioTracksFile);
        }
    }

    public void save(List<AudioTrack> audioTracks) {
        for (AudioTrack audioTrack : audioTracks) {
            save(audioTrack);
        }
    }

    public List<AudioTrack> checkInvalidTracks() {
        List<AudioTrack> tracksInDb = findAllAudioTracks();
        List<AudioTrack> invalidTracks = new ArrayList<>();
        for (AudioTrack audioTrack : tracksInDb) {
            if (!Paths.get(audioTrack.getLocalPath()).toFile().exists()) {
                invalidTracks.add(audioTrack);
            }
        }
        return invalidTracks;
    }

    public AudioTrack generateFromFile(String absolutePath) {
        AudioTrack audioTrack = AudioTrack.builder()
                .id(UUID.randomUUID())
                .localPath(absolutePath)
                .artist(extractArtist(absolutePath))
                .title(extractSongName(absolutePath))
                .startInSeconds(0)
                .build();
        AudioUtils.setAudioTrackLength(audioTrack);
        return audioTrack;
    }

    private boolean isUnique(AudioTrack audioTrack) {
        List<AudioTrack> allAudioTracks = findAllAudioTracks();
        return allAudioTracks.stream()
                .noneMatch(at -> at.equals(audioTrack));
    }

    private String extractArtist(String filename) {
        filename = filename.substring(filename.lastIndexOf(System.getProperty("file.separator")) + 1);
        for (String separator : SEPARATORS) {
            List<String> splittedSegments = Arrays.asList(filename.split(separator));
            int numberOfDigitSegments = 0;
            while (digitPattern.matcher(splittedSegments.get(numberOfDigitSegments).trim()).matches()) {
                numberOfDigitSegments++;
            }
            splittedSegments = splittedSegments.subList(numberOfDigitSegments, splittedSegments.size());
            if (splittedSegments.size() == 2) {
                return splittedSegments.get(0).trim();
            }
        }
        return "";
    }

    private String extractSongName(String filename) {
        filename = filename.substring(filename.lastIndexOf(System.getProperty("file.separator")) + 1);
        for (String separator : SEPARATORS) {
            List<String> splittedSegments = Arrays.asList(filename.split(separator));
            int numberOfDigitSegments = 0;
            while (digitPattern.matcher(splittedSegments.get(numberOfDigitSegments).trim()).matches()) {
                numberOfDigitSegments++;
            }
            splittedSegments = splittedSegments.subList(numberOfDigitSegments, splittedSegments.size());
            if (splittedSegments.size() == 2) {
                return splittedSegments.get(1).split("\\.[A-z0-9]+$").length != 0
                        ? splittedSegments.get(1).split("\\.[A-z0-9]+$")[0].trim()
                        : "";
            }
        }
        return filename.split("\\.[A-z0-9]+$")[0].trim();
    }
}
