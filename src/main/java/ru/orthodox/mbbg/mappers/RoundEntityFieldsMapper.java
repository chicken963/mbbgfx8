package ru.orthodox.mbbg.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.services.model.AudioTrackService;
import ru.orthodox.mbbg.services.model.RoundService;
import ru.orthodox.mbbg.services.play.blank.BlankService;

@Service
public class RoundEntityFieldsMapper {
    @Autowired
    private AudioTrackService audioTrackService;
    @Autowired
    private BlankService blankService;
    @Autowired
    private RoundService roundService;

    public void loadEntityFieldsFromStorage(Game game) {
        game.setRounds(roundService.findByIds(game.getRoundIds()));
        game.getRounds().forEach(this::loadEntityFieldsFromStorage);
    }

    private void loadEntityFieldsFromStorage(Round round) {
        round.setAudioTracks(audioTrackService.findByIds(round.getTracksIds()));
        if (round.getBlanksIds() != null) {
            round.setBlanks(blankService.findByIds(round.getBlanksIds()));
        }
    }
}
