package ru.orthodox.mbbg.mediaPlayer;

import com.sun.jna.NativeLibrary;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.co.caprica.vlcj.binding.RuntimeUtil;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
@RequiredArgsConstructor
public class PlayerComponent {

    private final AudioPlayerComponent audioPlayerComponent;

    private MediaPlayer mediaPlayer;
    @Getter
    private boolean started;

    @Value("${music.folder}")
    private String musicLocation;

    private List<File> musicQueue = new LinkedList<>();
    private ListIterator<File> queueIterator;
    private String currentFile;

    @PostConstruct
    private void postConstruct() {
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files\\VideoLAN\\VLC");
        this.mediaPlayer = audioPlayerComponent.mediaPlayer();
        Path musicPath = Paths.get(musicLocation);
        File musicSourcePath = musicPath.toFile();
        for (final File fileEntry : musicSourcePath.listFiles()) {
            if (!fileEntry.isDirectory()) {
                musicQueue.add(fileEntry);
            }
        }
        queueIterator = musicQueue.listIterator();
        currentFile = fileToString(queueIterator.next());
    }

    public void play() {
        mediaPlayer.media().play(currentFile);
        this.started = true;
    }

    public void pause() {
        mediaPlayer.controls().pause();
    }

    public void resume() {
        mediaPlayer.controls().play();
    }

    public void next() {
        if (queueIterator.hasNext()) {
            String oldState = currentFile;
            while (currentFile.equals(oldState)) {
                currentFile = fileToString(queueIterator.next());
            }
            mediaPlayer.media().play(currentFile);
        }
    }

    public void previous() {
        if (queueIterator.hasPrevious()) {
            String oldState = currentFile;
            while (currentFile.equals(oldState)) {
                currentFile = fileToString(queueIterator.previous());
            }
            mediaPlayer.media().play(currentFile);
        }
    }

    private String fileToString(File path) {
        try {
            return path.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
