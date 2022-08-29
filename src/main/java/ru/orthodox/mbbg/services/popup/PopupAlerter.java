package ru.orthodox.mbbg.services.popup;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.utils.screen.ScreenService;

import java.awt.*;

import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findElementByTypeAndStyleclass;
import static ru.orthodox.mbbg.utils.hierarchy.NodeDeepCopyProvider.createDeepCopy;

@Slf4j
@Service
public class PopupAlerter {
    @Autowired
    private ScreenService screenService;
    @Autowired
    private PopupConfigurator popupConfigurator;


    public void invoke(Window ownerWindow, String title, String message) {
        Parent popupTemplate = screenService.getParentNode("popup");
        popupConfigurator.configure(popupTemplate, message);
        invokeTracksValidationPopupStage(popupTemplate, title, ownerWindow);
    }

    public void invoke(Window ownerWindow, String title, String message, EventHandler<ActionEvent> handler) {
        Parent popupTemplate = screenService.getParentNode("popup");
        popupConfigurator.configure(popupTemplate, message, handler);
        invokeTracksValidationPopupStage(popupTemplate, title, ownerWindow);
    }

    private void invokeTracksValidationPopupStage(Parent popupTemplate, String title, Window sourceWindow) {
        final Stage popupStage = new Stage();
        popupStage.setTitle(title);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(sourceWindow);
        Scene dialogScene = null;
        Parent popup = createDeepCopy(popupTemplate);
        try{
            dialogScene = new Scene(popup, 600, 210);
            dialogScene.getStylesheets().add("styleSheets/popup.css");

        } catch (NullPointerException e) {
            log.warn("что-то непонятное с реюзом сцены");
        }
        popupStage.setScene(dialogScene);
        popupStage.setMinHeight(210);
        popupStage.show();
    }
}
