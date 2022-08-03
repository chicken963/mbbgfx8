package ru.orthodox.mbbg.ui.modelExtensions.audioTracksLibrary;

import javafx.scene.Node;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UITemplatesService {
    @Getter
    private List<Node> templates = new ArrayList<>();

    public void cacheTemplates(List<Node> templates) {
        this.templates.addAll(templates);
    }
}
