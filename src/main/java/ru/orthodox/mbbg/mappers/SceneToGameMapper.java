package ru.orthodox.mbbg.mappers;

import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.controllers.NewGameController;
import ru.orthodox.mbbg.model.basic.Game;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.model.proxy.play.RoundTab;
import ru.orthodox.mbbg.model.proxy.play.RoundsTabPane;
import ru.orthodox.mbbg.services.create.NewGameService;

import java.util.UUID;

@Service
public class SceneToGameMapper {

    @Autowired
    private TabToRoundMapper tabToRoundMapper;

    public Game generateFromScene(NewGameService.GameScene gameScene) {
        TextField gameNameInput = gameScene.getGameNameInput();
        RoundsTabPane roundsTabPane = gameScene.getRoundsTabPane();

        Game game = new Game();
        game.setId(UUID.randomUUID());
        game.setName(gameNameInput.getText());

        for (RoundTab tab : roundsTabPane.getRoundTabs()) {
            Round round = tabToRoundMapper.generateFromUIContent(tab);
            game.getRounds().add(round);
        }
        return game;
    }
}
