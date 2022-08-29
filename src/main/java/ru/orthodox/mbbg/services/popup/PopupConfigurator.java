package ru.orthodox.mbbg.services.popup;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.utils.screen.ScreenService;
import ru.orthodox.mbbg.utils.hierarchy.ElementFinder;

import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findElementByTypeAndStyleclass;

@Service
public class PopupConfigurator {

    @Autowired
    private ScreenService screenService;

    public Parent configure(Parent popupTemplate, String mainMessage) {
        Label mainMessageLabel = ElementFinder.findElementById(popupTemplate, "headingLabel");
        mainMessageLabel.setText(mainMessage);
        return popupTemplate;
    }

    public void configure(Parent popupTemplate, String message, EventHandler<ActionEvent> handler) {
        configure(popupTemplate, message);
        Button okButton = findElementByTypeAndStyleclass(popupTemplate, "ok-button");
        okButton.setOnAction(handler);
    }
}
