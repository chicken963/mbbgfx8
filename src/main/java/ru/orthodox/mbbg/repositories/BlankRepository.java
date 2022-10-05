package ru.orthodox.mbbg.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.Blank;
import ru.orthodox.mbbg.services.common.LocalFilesService;

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
        this.blanksFile = localFilesService.createOrUseLocalFile(blanksInfoFilePath);
    }

    public List<Blank> findAllBlanks() {
        return localFilesService.readEntitiesFromFile(blanksFile, Blank.class);
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

    public void delete(Blank blankToDelete) {
        localFilesService.delete(blankToDelete, blanksFile);
    }

    public void delete(List<Blank> blanksToDelete) {
        localFilesService.delete(blanksToDelete, blanksFile);
    }

    public void save(Blank blank) {
        localFilesService.write(blank, blanksFile);
    }

    public void save(List<Blank> blanks) {
        localFilesService.write(blanks, blanksFile);
    }
}
