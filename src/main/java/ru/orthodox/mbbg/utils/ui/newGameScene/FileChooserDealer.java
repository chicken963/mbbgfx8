package ru.orthodox.mbbg.utils.ui.newGameScene;

import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.services.AudioTrackService;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileChooserDealer {

    @Autowired
    private AudioTrackService audioTrackService;

    @Value("${music.localpath.startfolder}")
    private String localStartFolder;

    public FileChooser preconfigureFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Please select audio files");
        //TODO: uncomment and delete the subsequent line
//        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialDirectory(new File("F:\\myDocs\\Моя музыка"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Music files", "*.mp3", "*.aac", "*.wav", "*.flac")
        );
        return fileChooser;
    }

    public List<AudioTrack> mapFilesToAudioTracks(List<File> files) {
        return files.stream()
                .map(File::getAbsolutePath)
                .map(absPath -> audioTrackService.generateFromFile(absPath))
                .collect(Collectors.toList());
    }

}
