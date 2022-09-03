package ru.orthodox.mbbg.services.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.enums.BlanksStatus;
import ru.orthodox.mbbg.mappers.RoundEntityFieldsMapper;
import ru.orthodox.mbbg.model.basic.Blank;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.repositories.GamesRepository;
import ru.orthodox.mbbg.services.play.blank.BlankService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService {

    @Autowired
    private GamesRepository gamesRepository;
    @Autowired
    private RoundService roundService;
    @Autowired
    private RoundEntityFieldsMapper roundEntityFieldsMapper;
    @Autowired
    private BlankService blankService;

    public List<Game> findAllGames() {
        List<Game> games = gamesRepository.findAllGames();
        games.forEach(game -> {
            game.setRounds(roundService.findByIds(game.getRoundIds()));
        });
        return games;
    }

    public void deepSave(Game game) {
        game.getRounds().forEach(roundService::save);
        gamesRepository.save(game);
    }

    public void save(Game game) {
        gamesRepository.save(game);
    }

    public void delete(Game game) {
        gamesRepository.deleteGame(game);
        roundService.delete(game.getRounds());
    }

    public void setModelFields(Game game) {
        roundEntityFieldsMapper.loadEntityFieldsFromStorage(game);
    }

    public void generateBlanks(Game targetGame) {
        List<Round> rounds = targetGame.getRounds();
        List<Blank> previousBlanks = targetGame.getRounds().stream()
                .map(Round::getBlanks)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        blankService.delete(previousBlanks);
        for (Round round : rounds) {
            roundService.generateBlanks(round, rounds.indexOf(round));
        }
        targetGame.setBlanksStatus(BlanksStatus.ACTUALIZED);
        this.save(targetGame);
    }

}
