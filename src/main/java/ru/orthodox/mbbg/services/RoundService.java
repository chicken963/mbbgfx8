package ru.orthodox.mbbg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.model.Round;
import ru.orthodox.mbbg.repositories.RoundRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoundService {
    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private AudioTrackService audioTrackService;

    public void save(Round round) {
        round.setTracksIds(round.getAudioTracks().stream()
                .map(AudioTrack::getId)
                .collect(Collectors.toList()));
        roundRepository.save(round);
        round.getAudioTracks().forEach(audioTrackService::save);
    }

    public List<AudioTrack> getAudioTracks(Round round) {
        return audioTrackService.findByIds(round.getTracksIds());
    }

    public void shiftCurrentTargetWinCondition(Round round) {
        if (round.getCurrentTargetWinCondition().equals(round.getFirstStrikeCondition())) {
            round.setCurrentTargetWinCondition(round.getSecondStrikeCondition());
        } else if (round.getCurrentTargetWinCondition().equals(round.getSecondStrikeCondition())) {
            round.setCurrentTargetWinCondition(round.getThirdStrikeCondition());
        }
    }
}
