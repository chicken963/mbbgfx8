package ru.orthodox.mbbg.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.AudioTrack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LocalFilesService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final TypeFactory t = TypeFactory.defaultInstance();

    public <T> List<T> readEntityListFromFile(File entitiesListFile, Class<T> clazz) {
        List<T> entities = new ArrayList<>();
        try {
            entities = mapper.readValue(entitiesListFile, t.constructCollectionType(ArrayList.class,clazz));
/*            entities = mapper.readValue(entitiesListFile, new TypeReference<List<T>>() {
            });*/
        } catch (IOException e) {
            if (!entitiesListFile.exists()) {
                log.error("Cannot find array storage file {}", entitiesListFile.getAbsolutePath());
            } else {
                e.printStackTrace();
            }
        }
        return entities;
    }

    public <T> void write(T entity, File entitiesListFile) {
        List<T> availableEntities = new ArrayList<>();
        try {
            availableEntities = mapper.readValue(entitiesListFile, t.constructCollectionType(ArrayList.class, entity.getClass()));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        List<T> availableEntities = this.readEntityListFromFile(entitiesListFile, T.class);
        availableEntities.add(entity);
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
}
