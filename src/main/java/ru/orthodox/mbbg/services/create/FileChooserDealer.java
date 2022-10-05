package ru.orthodox.mbbg.services.create;

import javafx.stage.FileChooser;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.services.model.AudioTrackService;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class FileChooserDealer {

    @Autowired
    private AudioTrackService audioTrackService;

    @Value("${music.localpath.startfolder}")
    private String localStartFolder;

    @Setter
    private Round round;

    public FileChooser provideFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Please select audio files");
        //TODO: uncomment and delete the subsequent line
//        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialDirectory(new File("C:\\Users\\Aleksei_Andreichuk\\Music"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Music files", "*.mp3", "*.aac", "*.wav", "*.flac")
        );
        return fileChooser;
    }

    public List<AudioTrack> mapFilesToAudioTracks(List<File> files) {
        ExecutorService threadPoolToLoadMediaInfo = Executors.newCachedThreadPool();
        audioTrackService.setRound(round);
        return files.stream()
                .map(File::getAbsolutePath)
                .map(absPath -> audioTrackService.generateFromFile(absPath, threadPoolToLoadMediaInfo))
                .collect(Collectors.toList());
    }

}
