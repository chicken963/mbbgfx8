package ru.orthodox.mbbg.services;

import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.orthodox.mbbg.configuration.ControllersConfig;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Component
public class ScreenService {

    private HashMap<String, ControllersConfig.View> screenMap = new HashMap<>();

    @Setter
    @Getter
    private Scene startScene;

    @Autowired
    @Qualifier("mainView")
    private ControllersConfig.View mainView;

    @Autowired
    @Qualifier("startMenuView")
    private ControllersConfig.View startMenuView;

    @Autowired
    @Qualifier("newGameView")
    private ControllersConfig.View newGameView;

    @Autowired
    @Qualifier("popupView")
    private ControllersConfig.View popupView;

    @Autowired
    @Qualifier("viewTicketsView")
    private ControllersConfig.View viewTicketsView;

    @PostConstruct
    public void addScenesToTray() {
        startScene = new Scene(startMenuView.getParentNode());

        this.addScreen("startmenu", startMenuView);
        this.addScreen("main", mainView);
        this.addScreen("newgame", newGameView);
        this.addScreen("popup", popupView);
        this.addScreen("viewTickets", viewTicketsView);
    }

    private void addScreen(String name, ControllersConfig.View pane) {
        screenMap.put(name, pane);
    }

    private void removeScreen(String name) {
        screenMap.remove(name);
    }

    public Scene activate(String name) {
        startScene.setRoot(screenMap.get(name).getParentNode());
        return startScene;
    }


    public Parent getParentNode(String name) {
        return screenMap.get(name).getParentNode();
    }
}
