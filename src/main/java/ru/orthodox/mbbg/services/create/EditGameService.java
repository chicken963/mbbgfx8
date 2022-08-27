package ru.orthodox.mbbg.services.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.services.model.GameService;

import java.util.Optional;

@Service
public class EditGameService extends NewGameService {
    @Autowired
    private GameService gameService;

    public void editGame(Game game) {
        gameService.setModelFields(game);
        super.renderGame(Optional.of(game));

        screenService.activate("newGame");
    }
}
