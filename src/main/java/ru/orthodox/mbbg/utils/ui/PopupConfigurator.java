package ru.orthodox.mbbg.utils.ui;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.orthodox.mbbg.services.ScreenService;

import static ru.orthodox.mbbg.utils.ui.newGameScene.ElementFinder.findElementById;

@Service
public class PopupConfigurator {

    @Autowired
    private ScreenService screenService;

    public Parent configure(Parent popupTemplate, String mainMessage, String detailsMessage) {
        Label mainMessageLabel = findElementById(popupTemplate, "headingLabel");
        Label detailsLabel = findElementById(popupTemplate, "detailsLabel");
        mainMessageLabel.setText(mainMessage);
        if (Strings.isEmpty(detailsMessage)) {
            findElementById(popupTemplate, "detailsLabel");
        } else {
            detailsLabel.setText(detailsMessage);
        }
        return popupTemplate;
    }
}
