package ru.orthodox.mbbg.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.repositories.AudioTrackRepository;
import ru.orthodox.mbbg.repositories.BlankRepository;
import ru.orthodox.mbbg.services.model.RoundService;

@Service
public class RoundEntityFieldsMapper {
    @Autowired
    private AudioTrackRepository audioTrackRepository;
    @Autowired
    private BlankRepository blankRepository;
    @Autowired
    private RoundService roundService;

    public void loadEntityFieldsFromStorage(Game game) {
        game.setRounds(roundService.findInStorageByIds(game.getRoundIds()));
        game.getRounds().forEach(this::loadEntityFieldsFromStorage);
    }

    private void loadEntityFieldsFromStorage(Round round) {
        round.setAudioTracks(audioTrackRepository.findByIds(round.getTracksIds()));
        round.setBlanks(blankRepository.findByIds(round.getBlanksIds()));
    }
}
