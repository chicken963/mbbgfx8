package ru.orthodox.mbbg.utils.ui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.services.ScreenService;

@Slf4j
@Service
public class PopupAlerter {
    @Autowired
    private ScreenService screenService;
    @Autowired
    private PopupConfigurator popupConfigurator;

    public void invoke(Window ownerWindow, String mainMessage, String detailedMessage, int tracksCount) {

        Parent popupTemplate = screenService.getParentNode("popup");

        popupConfigurator.configure(popupTemplate, mainMessage, detailedMessage);

        invokeStage(popupTemplate, ownerWindow, tracksCount);
    }

    private void invokeStage(Parent popup, Window sourceWindow, int tracksCount) {
        final Stage popupStage = new Stage();
        popupStage.setTitle("Oops...");
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(sourceWindow);
        Scene dialogScene = null;
        try{
            dialogScene = new Scene(popup, 350, 140 + Math.max(tracksCount, 3) * 20);

        } catch (NullPointerException e) {
            log.warn("что-то непонятное с реюзом сцены");
        }
        popupStage.setScene(dialogScene);
        popupStage.show();
    }
}
