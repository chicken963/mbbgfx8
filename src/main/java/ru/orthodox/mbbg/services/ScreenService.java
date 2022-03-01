package ru.orthodox.mbbg.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.configuration.ControllersConfig;

import javax.annotation.PostConstruct;
import java.io.IOException;
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

    @PostConstruct
    public void addScenesToTray() {
        main = new Scene(startMenuView.getView());

        this.addScreen("startmenu", startMenuView);
        this.addScreen("main", mainView);
/*            screenController.addScreen("create", FXMLLoader.load(getClass().getResource( "create.fxml" )));
            screenController.addScreen("edit", FXMLLoader.load(getClass().getResource( "edit.fxml" )));*/

    }

    private void addScreen(String name, ControllersConfig.View pane) {
        screenMap.put(name, pane);
    }

    private void removeScreen(String name) {
        screenMap.remove(name);
    }

    public Scene activate(String name) {
        main.setRoot(screenMap.get(name).getView());
        return main;
    }
}
