package ru.orthodox.mbbg.services.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.mappers.RoundEntityFieldsMapper;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.repositories.RoundRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RoundService {
    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private AudioTrackService audioTrackService;
    @Autowired
    private RoundEntityFieldsMapper roundEntityFieldsMapper;

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

    public void setModelFields(Round round) {
        roundEntityFieldsMapper.loadEntityFieldsFromStorage(round);
    }

    public Set<String> getPlayedArtists(Round round) {
        return round.getPlayedAudiotracks().stream()
                .map(AudioTrack::getArtist)
                .collect(Collectors.toSet());
    }

    public String getNextArtist(Round round) {
        return Stream.of(round.getNextTrack())
                .map(AudioTrack::getArtist)
                .findFirst()
                .orElse(null);
    }
}
