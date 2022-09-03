package ru.orthodox.mbbg.services.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.mappers.RoundEntityFieldsMapper;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.basic.Blank;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.repositories.RoundRepository;
import ru.orthodox.mbbg.services.play.blank.BlankService;

import java.util.*;
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
    @Autowired
    private BlankService blankService;

    public void save(Round round) {
        if (round.getId() == null) {
            round.setId(UUID.randomUUID());
        }
        round.setTracksIds(round.getAudioTracks().stream()
                .map(AudioTrack::getId)
                .collect(Collectors.toList()));
        roundRepository.save(round);
        audioTrackService.save(round.getAudioTracks());
    }

    public void shiftCurrentTargetWinCondition(Round round) {
        if (round.getCurrentTargetWinCondition().equals(round.getFirstStrikeCondition())) {
            round.setCurrentTargetWinCondition(round.getSecondStrikeCondition());
        } else if (round.getCurrentTargetWinCondition().equals(round.getSecondStrikeCondition())) {
            round.setCurrentTargetWinCondition(round.getThirdStrikeCondition());
        }
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

    public void delete(Round round) {
        blankService.delete(round.getBlanks());
        roundRepository.delete(round);
    }

    public void delete(List<Round> rounds) {
        List<Blank> blanksToDelete = rounds.stream()
                .map(Round::getBlanks)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        blankService.delete(blanksToDelete);
        roundRepository.delete(rounds);
    }

    public List<Round> findByIds(List<UUID> roundIds) {
        List<Round> rounds = roundRepository.findByIds(roundIds);
        rounds.forEach(round -> {
            round.setAudioTracks(audioTrackService.findByIds(round.getTracksIds()));
            List<UUID> blanksIds = round.getBlanksIds();
            if (blanksIds != null) {
                round.setBlanks(blankService.findByIds(round.getBlanksIds()));
            } else {
                round.setBlanks(new ArrayList<>());
            }
        });
        return rounds;
    }

    public void generateBlanks(Round round, int roundNumber) {
        List<Blank> roundBlanks = blankService.generateBlanks(round, roundNumber);
        round.setBlanksIds(roundBlanks.stream()
                .map(Blank::getId)
                .collect(Collectors.toList()));
        roundRepository.save(round);
    }
}
