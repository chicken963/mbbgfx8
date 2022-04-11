package ru.orthodox.mbbg.services;

import javafx.scene.Scene;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.orthodox.mbbg.configuration.ControllersConfig;
import ru.orthodox.mbbg.enums.OpenSceneMode;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Component
public class ScreenService {

    private HashMap<String, ControllersConfig.View> screenMap = new HashMap<>();

    @Setter
    @Getter
    private Scene main;

    @Autowired
    @Qualifier("mainView")
    private ControllersConfig.View mainView;

    @Autowired
    @Qualifier("startMenuView")
    private ControllersConfig.View startMenuView;

    @Autowired
    @Qualifier("newGameView")
    private ControllersConfig.View newGameView;

    @PostConstruct
    public void addScenesToTray() {
        main = new Scene(startMenuView.getParentNode());

        this.addScreen("startmenu", startMenuView);
        this.addScreen("main", mainView);
        this.addScreen("newgame", newGameView);
    }

    private void addScreen(String name, ControllersConfig.View pane) {
        screenMap.put(name, pane);
    }

    private void removeScreen(String name) {
        screenMap.remove(name);
    }

    public Scene activate(String name) {
        main.setRoot(screenMap.get(name).getParentNode());
        return main;
    }
}
