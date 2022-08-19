package ru.orthodox.mbbg.services.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.repositories.GamesRepository;

import java.util.List;

@Service
public class GameService {

    @Autowired
    private GamesRepository gamesRepository;
    @Autowired
    private RoundService roundService;

    public void save(Game game) {
        gamesRepository.save(game);
        game.getRounds().forEach(roundService::save);
    }

    public List<Round> findRoundsOfGame(Game game) {
        return  gamesRepository.findRoundsByGame(game);
    }

}
