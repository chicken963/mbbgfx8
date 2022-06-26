package ru.orthodox.mbbg.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.model.Round;
import ru.orthodox.mbbg.services.LocalFilesService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoundRepository {

    @Autowired
    private LocalFilesService localFilesService;

    @Autowired
    private AudioTrackRepository audioTrackRepository;

    @Value("${rounds.json.filepath}")
    private String roundsInfoFilePath;

    private File roundsFile;

    @PostConstruct
    private void init() {
        this.roundsFile = new File(roundsInfoFilePath);
    }

    public List<Round> findAllRounds() {
        return localFilesService.readEntityListFromFile(roundsFile, Round.class);
    }

    public Round findById(UUID id) {
        return findAllRounds().stream()
                .filter(round -> round.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Round> findByIds(List<UUID> ids) {
        return findAllRounds().stream()
                .filter(round -> ids.contains(round.getId()))
                .collect(Collectors.toList());
    }

    public void save(Round round) {
        localFilesService.write(round, roundsFile);
    }

    public List<AudioTrack> findAudioTracksByRoundId(UUID roundId) {
        Round round = this.findById(roundId);
        return round != null
                ? audioTrackRepository.findByIds(round.getTracksIds())
                : null;
    }

    public List<AudioTrack> findAudioTracksByRound(Round round) {
        return audioTrackRepository.findByIds(round.getTracksIds());
    }
}
