package ru.orthodox.mbbg.utils.screen;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class ScreenService {

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
        startScene.getStylesheets().setAll(resource.getStyleSheetUrls());
        return startScene;
    }

    public Parent getParentNode(String name) {
        ScreenViewResource resourceByName = findByName(name);
        Parent parent = resourceByName.getParentNode();
        parent.getStylesheets().setAll(resourceByName.getStyleSheetUrls());
        return parent;
    }

    private ScreenViewResource findByName(String name) {
        return screenList.stream()
                .filter(screenViewResource -> screenViewResource.isNamedAs(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Failed to find screen with a name " + name));
    }

    public Stage createSeparateStage(Button libraryButton, Scene scene, String title) {
        final Stage popupStage = new Stage();
        popupStage.setTitle(title);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(libraryButton.getScene().getWindow());
        popupStage.setScene(scene);
        return popupStage;
    }
}
