package ru.orthodox.mbbg.utils.screen;

import javafx.scene.Parent;
import lombok.Data;
import ru.orthodox.mbbg.configuration.ControllersConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class ScreenViewResource {

    private String name;
    private ControllersConfig.View view;
    private List<String> styleSheetUrls;

    public ScreenViewResource(String name, ControllersConfig.View view, String styleSheetUrl) {
        this.name = name;
        this.view = view;
        this.styleSheetUrls = Collections.singletonList(styleSheetUrl);
    }

    public ScreenViewResource(String name, ControllersConfig.View view, List<String> styleSheetUrls) {
        this.name = name;
        this.view = view;
        this.styleSheetUrls = new ArrayList<>(styleSheetUrls);
    }

    public Parent getParentNode() {
        return this.getView().getParentNode();
    }

    public boolean isNamedAs(String name) {
        return this.getName().equals(name);
    }
}
