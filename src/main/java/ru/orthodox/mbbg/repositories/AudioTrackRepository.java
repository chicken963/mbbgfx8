package ru.orthodox.mbbg.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.orthodox.mbbg.model.AudioTrack;

public interface AudioTrackRepository extends CrudRepository<AudioTrack, String> {
}
