package ru.orthodox.mbbg.services.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.mappers.RoundEntityFieldsMapper;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.repositories.GamesRepository;

@Service
public class GameService {

    @Autowired
    private GamesRepository gamesRepository;
    @Autowired
    private RoundService roundService;
    @Autowired
    private RoundEntityFieldsMapper roundEntityFieldsMapper;

    public void save(Game game) {
        game.getRounds().stream().forEach(roundService::setIdIfAbsent);
        gamesRepository.save(game);
        game.getRounds().forEach(roundService::save);
    }

    public void setModelFields(Game game) {
        roundEntityFieldsMapper.loadEntityFieldsFromStorage(game);
    }

}
