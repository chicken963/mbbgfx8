package ru.orthodox.mbbg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.Game;
import ru.orthodox.mbbg.repositories.GamesRepository;

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

}
