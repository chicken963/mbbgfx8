package ru.orthodox.mbbg.repositories;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.services.common.LocalFilesService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
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
        this.roundsFile = localFilesService.createOrUseLocalFile(roundsInfoFilePath);
    }

    public List<Round> findAllRounds() {
        return localFilesService.readEntitiesFromFile(roundsFile, Round.class);
    }

    public List<Round> findByIds(List<UUID> ids) {
        return findAllRounds().stream()
                .filter(round -> ids.contains(round.getId()))
                .collect(Collectors.toList());
    }

    public void save(Round round) {
        localFilesService.write(round, roundsFile);
    }

    public void save(List<Round> rounds) {
        localFilesService.write(rounds, roundsFile);
    }

    public void delete(Round roundToDelete) {
        localFilesService.delete(roundToDelete, roundsFile);
    }

    public void delete(List<Round> roundsToDelete) {
        localFilesService.delete(roundsToDelete, roundsFile);
    }

}
