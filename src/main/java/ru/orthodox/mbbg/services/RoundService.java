package ru.orthodox.mbbg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.model.Round;
import ru.orthodox.mbbg.repositories.RoundRepository;

import java.util.stream.Collectors;

@Service
public class RoundService {
    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private AudioTrackService audioTrackService;

    public void save(Round round) {
        roundRepository.save(round);
        round.getAudioTracks().forEach(audioTrackService::save);
    }

}
