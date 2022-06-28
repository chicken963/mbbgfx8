package ru.orthodox.mbbg.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.Blank;
import ru.orthodox.mbbg.services.LocalFilesService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BlankRepository {

    @Autowired
    private LocalFilesService localFilesService;

    @Value("${blanks.json.filepath}")
    private String blanksInfoFilePath;

    private File blanksFile;

    @PostConstruct
    private void init() {
        this.blanksFile = new File(blanksInfoFilePath);
    }

    public List<Blank> findAllBlanks() {
        return localFilesService.readEntityListFromFile(blanksFile, Blank.class);
    }

    public Blank findById(UUID id) {
        return findAllBlanks().stream()
                .filter(blank -> blank.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Blank> findByIds(List<UUID> ids) {
        return findAllBlanks().stream()
                .filter(blank -> ids.contains(blank.getId()))
                .collect(Collectors.toList());
    }

    public void save(Blank blank) {
        localFilesService.write(blank, blanksFile);
    }
}
