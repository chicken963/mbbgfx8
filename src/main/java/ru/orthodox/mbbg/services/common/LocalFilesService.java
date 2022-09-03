package ru.orthodox.mbbg.services.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.enums.EntityUpdateMode;
import ru.orthodox.mbbg.model.basic.MarkedWithId;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LocalFilesService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final TypeFactory t = TypeFactory.defaultInstance();

    public <T extends MarkedWithId> void write(T entity, File entitiesListFile) {
        updateEntityInFile(entity, entitiesListFile, EntityUpdateMode.EDIT);
    }

    public <T extends MarkedWithId> void write(List<T> entities, File entitiesListFile) {
        updateEntitiesInFile(entities, entitiesListFile, EntityUpdateMode.EDIT);
    }

    public <T extends MarkedWithId> void delete(T entity, File entitiesListFile) {
        updateEntityInFile(entity, entitiesListFile, EntityUpdateMode.DELETE);
    }

    public <T extends MarkedWithId> void delete(List<T> entities, File entitiesListFile) {
        updateEntitiesInFile(entities, entitiesListFile, EntityUpdateMode.DELETE);
    }

    private <T extends MarkedWithId> void updateEntityInFile(T entity, File entitiesListFile, EntityUpdateMode mode) {
        List<T> availableEntities = readEntitiesFromFile(entitiesListFile, entity.getClass());
        Optional<T> entityWithTheSameId = findEntityById(availableEntities, entity.getId());
        entityWithTheSameId.ifPresent(availableEntities::remove);
        if (EntityUpdateMode.EDIT == mode) {
            availableEntities.add(entity);
        }
        writeEntitiesToFile(availableEntities, entitiesListFile);
    }

    private <T extends MarkedWithId> void updateEntitiesInFile(List<T> entitiesWithNewState, File entitiesListFile, EntityUpdateMode mode) {
        if (entitiesWithNewState.size() == 0) {
            return;
        }
        List<T> availableEntities = readEntitiesFromFile(entitiesListFile, entitiesWithNewState.get(0).getClass());
        List<T> entitiesWithRequestedIds = findAvailableEntitiesWithTheSameIds(availableEntities, entitiesWithNewState);

        availableEntities.removeAll(entitiesWithRequestedIds);

        if (EntityUpdateMode.EDIT == mode) {
            availableEntities.addAll(entitiesWithNewState);
        }
        writeEntitiesToFile(availableEntities, entitiesListFile);
    }

    public <T extends MarkedWithId> List<T> readEntitiesFromFile(File entitiesListFile, Class<? extends MarkedWithId> entityClass) {
        List<T> availableEntities = new ArrayList<>();
        try {
            availableEntities = mapper.readValue(entitiesListFile, t.constructCollectionType(ArrayList.class, entityClass));
        } catch (IOException e) {
            if (!entitiesListFile.exists()) {
                log.error("Cannot find storage file {}", entitiesListFile.getAbsolutePath());
            } else {
                e.printStackTrace();
            }
        }
        return availableEntities;
    }

    private <T extends MarkedWithId> void writeEntitiesToFile(List<T> availableEntities, File entitiesListFile) {
        try {
            mapper.writeValue(entitiesListFile, availableEntities);
        } catch (IOException e) {
            if (!entitiesListFile.exists()) {
                log.error("Cannot find audio tracks info file {}", entitiesListFile.getAbsolutePath());
            } else {
                e.printStackTrace();
            }
        }
    }

    private <T extends MarkedWithId> Optional<T> findEntityById(List<T> availableEntities, UUID targetId) {
        return availableEntities.stream()
                .filter(existingEntity -> existingEntity.getId().equals(targetId))
                .findFirst();
    }

    private <T extends MarkedWithId> List<T> findAvailableEntitiesWithTheSameIds(List<T> availableEntities, List<T> entities) {
        List<UUID> idsToEdit = entities.stream()
                .map(MarkedWithId::getId)
                .collect(Collectors.toList());
        return availableEntities.stream()
                .filter(existingEntity -> idsToEdit.contains(existingEntity.getId()))
                .collect(Collectors.toList());
    }
}
