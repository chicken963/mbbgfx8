package ru.orthodox.mbbg.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.Game;
import ru.orthodox.mbbg.model.Round;
import ru.orthodox.mbbg.services.LocalFilesService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GamesRepository {

    @Autowired
    private LocalFilesService localFilesService;

    @Autowired
    private RoundRepository roundRepository;

    @Value("${games.json.filepath}")
    private String gamesInfoFilePath;

    private File gamesFile;

    @PostConstruct
    private void init() {
        this.gamesFile = new File(gamesInfoFilePath);
    }

    public List<Game> findAllGames() {
        return localFilesService.readEntityListFromFile(gamesFile, Game.class);
    }

    public Game findById(UUID id) {
        return findAllGames().stream()
                .filter(game -> game.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Game> findByIds(List<UUID> ids) {
        return findAllGames().stream()
                .filter(game -> ids.contains(game.getId()))
                .collect(Collectors.toList());
    }

    public void save(Game game) {
        game.setRoundIds(game.getRounds().stream()
                .map(Round::getId)
                .collect(Collectors.toList()));

        localFilesService.write(game, gamesFile);
    }

    public List<Round> findRoundsByGameId(UUID gameId) {
        Game game = this.findById(gameId);
        return game != null
                ? roundRepository.findByIds(game.getRoundIds())
                : null;
    }

    public List<Round> findRoundsByGame(Game game) {
        return roundRepository.findByIds(game.getRoundIds());
    }

}
