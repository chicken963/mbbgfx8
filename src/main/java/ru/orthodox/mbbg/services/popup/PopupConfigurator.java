package ru.orthodox.mbbg.services.popup;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.utils.screen.ScreenService;

import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findElementByTypeAndStyleclass;

@Service
public class PopupConfigurator {

    @Autowired
    private ScreenService screenService;

    public void configure(
            Parent popupTemplate,
            String message,
            EventHandler<ActionEvent> onSubmit,
            EventHandler<ActionEvent> onDecline) {
        configure(popupTemplate, message, onSubmit);
        Button cancelButton = findElementByTypeAndStyleclass(popupTemplate, "cancel-button");
        cancelButton.setOnAction(onDecline);
    }

    public void configure(
            Parent popupTemplate,
            String message,
            EventHandler<ActionEvent> onSubmit) {
        configure(popupTemplate, message);
        Button okButton = findElementByTypeAndStyleclass(popupTemplate, "ok-button");
        okButton.setOnAction(onSubmit);
    }

    public Parent configure(Parent popupTemplate, String mainMessage) {
        Label mainMessageLabel = findElementByTypeAndStyleclass(popupTemplate, "heading-label");
        mainMessageLabel.setText(mainMessage);
        return popupTemplate;
    }

}
