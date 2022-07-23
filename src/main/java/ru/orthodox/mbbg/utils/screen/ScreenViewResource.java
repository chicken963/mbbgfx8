package ru.orthodox.mbbg.utils.screen;

import javafx.scene.Parent;
import lombok.Data;
import ru.orthodox.mbbg.configuration.ControllersConfig;

@Data
public class ScreenViewResource {

    private String name;
    private ControllersConfig.View view;
    private String styleSheetUrl;

    public ScreenViewResource(String name, ControllersConfig.View view, String styleSheetUrl) {
        this.name = name;
        this.view = view;
        this.styleSheetUrl = styleSheetUrl;
    }

    public Parent getParentNode() {
        return this.getView().getParentNode();
    }

    public boolean isNamedAs(String name) {
        return this.getName().equals(name);
    }
}
