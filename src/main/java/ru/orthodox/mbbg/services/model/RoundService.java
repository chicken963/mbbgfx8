package ru.orthodox.mbbg.services.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.Round;
import ru.orthodox.mbbg.services.LocalFilesService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoundService {

    @Autowired
    private LocalFilesService localFilesService;

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

    public Optional<Round> findById(UUID id) {
        return findAllRounds().stream()
                .filter(round -> round.getId().equals(id))
                .findFirst();
    }

    public List<Round> findByIds(List<UUID> ids) {
        return findAllRounds().stream()
                .filter(round -> ids.contains(round.getId()))
                .collect(Collectors.toList());
    }

    public void save(Round round) {
        localFilesService.write(round, roundsFile);
    }
}
