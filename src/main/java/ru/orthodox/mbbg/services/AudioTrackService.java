package ru.orthodox.mbbg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.repositories.AudioTrackRepository;
import ru.orthodox.mbbg.utils.AudioUtils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AudioTrackService {

    @Autowired
    private AudioTrackRepository audioTrackRepository;

    private static final String[] SEPARATORS = {"_-_", " - ", "-", "_"};
    Pattern digitPattern = Pattern.compile("\\d+");

    public void save(AudioTrack audioTrack) {
        audioTrackRepository.save(audioTrack);
    }

    public AudioTrack generateFromFile(String absolutePath) {
        AudioTrack audioTrack = AudioTrack.builder()
                .id(UUID.randomUUID())
                .localPath(absolutePath)
                .artist(extractArtistFromFileName(absolutePath))
                .title(extractSongNameFromFileName(absolutePath))
                .startInSeconds(0)
                .build();
        AudioUtils.setAudioTrackLength(audioTrack);
        return audioTrack;
    }

    private String extractArtistFromFileName(String filename) {
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

    private String extractSongNameFromFileName(String filename) {
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

    public List<AudioTrack> checkInvalidTracks() {
        List<AudioTrack> tracksInDb = audioTrackRepository.findAllAudioTracks();
        List<AudioTrack> invalidTracks = new ArrayList<>();
        for (AudioTrack audioTrack : tracksInDb) {
            if (!Paths.get(audioTrack.getLocalPath()).toFile().exists()) {
                invalidTracks.add(audioTrack);
            }
        }
        return invalidTracks;
    }

    public List<AudioTrack> findByIds(List<UUID> uuids) {
        return audioTrackRepository.findByIds(uuids);
    }
}
