package ru.orthodox.mbbg.utils.screen;

import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.orthodox.mbbg.utils.screen.ScreenViewResource;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class ScreenService {

    @Setter
    @Getter
    private Scene startScene;

    @Autowired
    private List<ScreenViewResource> screenList;

    @PostConstruct
    public void addScenesToTray() {
        startScene = new Scene(getParentNode("startMenu"));
    }

    private void removeScreen(String name) {
        ScreenViewResource resourceToDelete =  screenList.stream()
                .filter(screenViewResource -> screenViewResource.isNamedAs(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Failed to find screen with a name " + name));
        screenList.remove(resourceToDelete);
    }

    public Scene activate(String name) {
        ScreenViewResource resource = findByName(name);
        startScene.setRoot(resource.getParentNode());
        startScene.getStylesheets().clear();
        startScene.getStylesheets().setAll(resource.getStyleSheetUrls());
        return startScene;
    }

    public Parent getParentNode(String name) {
        return findByName(name).getParentNode();
    }

    private ScreenViewResource findByName(String name) {
        return screenList.stream()
                .filter(screenViewResource -> screenViewResource.isNamedAs(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Failed to find screen with a name " + name));
    }
}
