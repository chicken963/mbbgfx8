package ru.orthodox.mbbg.services.create;

import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.proxy.create.AudioTrackEditUIView;

import java.util.List;
import java.util.Optional;

@Service
public class AudioTrackUIViewService {

    public Optional<AudioTrackEditUIView> findByAudioTrack(List<AudioTrackEditUIView> gridRows, AudioTrack audioTrack) {
        return gridRows.stream()
                .filter(row -> row.getAudioTrack() == audioTrack)
                .findFirst();
    }

}
