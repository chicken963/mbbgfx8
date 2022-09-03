package ru.orthodox.mbbg.services.popup;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.utils.screen.ScreenService;

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
        invokePopupStage(popupTemplate, title, ownerWindow);
    }

    public void invoke(Window ownerWindow, String title, String message, EventHandler<ActionEvent> handler) {
        Parent popupTemplate = screenService.getParentNode("popup");
        popupConfigurator.configure(popupTemplate, message, handler);
        invokePopupStage(popupTemplate, title, ownerWindow);
    }

    public void invokeOkCancel(Window ownerWindow,
                               String title,
                               String message,
                               EventHandler<ActionEvent> onSubmit,
                               EventHandler<ActionEvent> onDecline
    ) {
        Parent popupTemplate = screenService.getParentNode("popupOkCancel");
        popupConfigurator.configure(popupTemplate, message, onSubmit, onDecline);
        invokePopupStage(popupTemplate, title, ownerWindow);
    }

    private void invokePopupStage(Parent popupTemplate, String title, Window sourceWindow) {
        final Stage popupStage = new Stage();
        popupStage.setTitle(title);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(sourceWindow);
        Scene dialogScene = null;
        Parent popup = createDeepCopy(popupTemplate);
        try{
            dialogScene = new Scene(popup, 600, 210);
            dialogScene.getStylesheets().addAll("styleSheets/popup.css",  "styleSheets/scrollable-table.css");

        } catch (NullPointerException e) {
            log.warn("Scene failed to be reused");
        }
        popupStage.setScene(dialogScene);
        popupStage.setMinHeight(210);
        popupStage.show();
    }
}
