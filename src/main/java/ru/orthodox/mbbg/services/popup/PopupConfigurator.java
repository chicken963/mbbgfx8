package ru.orthodox.mbbg.services.popup;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.utils.screen.ScreenService;
import ru.orthodox.mbbg.utils.hierarchy.ElementFinder;

@Service
public class PopupConfigurator {

    @Autowired
    private ScreenService screenService;

    public Parent configure(Parent popupTemplate, String mainMessage, String detailsMessage) {
        Label mainMessageLabel = ElementFinder.findElementById(popupTemplate, "headingLabel");
        Label detailsLabel = ElementFinder.findElementById(popupTemplate, "detailsLabel");
        mainMessageLabel.setText(mainMessage);
        if (Strings.isEmpty(detailsMessage)) {
            ElementFinder.findElementById(popupTemplate, "detailsLabel");
        } else {
            detailsLabel.setText(detailsMessage);
        }
        return popupTemplate;
    }
}
