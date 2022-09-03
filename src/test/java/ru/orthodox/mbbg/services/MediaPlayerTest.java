package ru.orthodox.mbbg.services;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.services.common.PlayMediaService;
import ru.orthodox.mbbg.services.model.RoundService;
import ru.orthodox.mbbg.services.play.MediaPlayerService;
import ru.orthodox.mbbg.services.play.PlaylistTableService;
import ru.orthodox.mbbg.services.play.ProgressBarBackgroundService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {
        MediaPlayerService.class,
        RoundService.class,
        ProgressBarBackgroundService.class,
        PlayMediaService.class,
        ApplicationEventPublisher.class,
        PlaylistTableService.class
})
public class MediaPlayerTest {

    @Mock
    private RoundService roundService;
    @Mock
    private ProgressBarBackgroundService progressBarBackgroundService;
    @Mock
    private PlayMediaService playService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private PlaylistTableService playlistTableService;
    @InjectMocks
    private MediaPlayerService uut;

    private Round activeRound;
    private List<AudioTrack> roundQueue;
    private AudioTrack audioTrack0;
    private AudioTrack audioTrack1;
    private AudioTrack audioTrack2;
    private AudioTrack audioTrack3;
    private AudioTrack audioTrack4;

    @BeforeEach
    public void setup() {
        this.roundQueue = prepareRoundQueue();
        this.activeRound = new Round();
        activeRound.setAudioTracks(roundQueue);
        PlatformImpl.startup(() -> {});
        uut.configureUIElements(new Label(), new Label(), new HBox(), new ProgressBar(), new Button(), new Button());
    }

    @Test
    public void whenInitialized_thenShouldUpdateNextTrack() {
        //when
        uut.setActiveRound(activeRound);
        //then
        Assertions.assertEquals(roundQueue.get(0), activeRound.getNextTrack());
        Assertions.assertNull(activeRound.getCurrentTrack());
        Assertions.assertNull(activeRound.getPreviousTrack());
    }

    @Test
    public void whenFirstTimePlayed_thenShouldUpdateNextTrackAndCurrentTrack() {
        //given
        uut.setActiveRound(activeRound);
        Mockito.lenient().when(playlistTableService.findFirstActiveTrack()).thenReturn(Optional.of(audioTrack1));
        //when
        uut.play();
        //then
        Assertions.assertEquals(roundQueue.get(1), activeRound.getNextTrack());
        Assertions.assertEquals(roundQueue.get(0), activeRound.getCurrentTrack());
        Assertions.assertNull(activeRound.getPreviousTrack());
    }

    @Test
    public void whenFirstTimePlayedNotFirstTrack_thenShouldUpdateNextTrackToFirst() {
        //given
        uut.setActiveRound(activeRound);
        activeRound.setNextTrack(audioTrack2);
        Mockito.lenient().when(playlistTableService.findFirstActiveTrack()).thenReturn(Optional.of(audioTrack0));
        //when
        uut.play();
        //then
        Assertions.assertEquals(roundQueue.get(0), activeRound.getNextTrack());
        Assertions.assertEquals(roundQueue.get(2), activeRound.getCurrentTrack());
        Assertions.assertNull(activeRound.getPreviousTrack());
    }

    @Test
    public void whenShiftForward_thenShouldUpdateAllTracks() {
        //given
        uut.setActiveRound(activeRound);
        Mockito.lenient().when(playlistTableService.findFirstActiveTrack()).thenReturn(Optional.of(audioTrack1));
        uut.play();
        Mockito.lenient().when(playlistTableService.findFirstActiveTrack()).thenReturn(Optional.of(audioTrack2));
        //when
        uut.switchToNextTrack();
        //then
        Assertions.assertEquals(roundQueue.get(0), activeRound.getPreviousTrack());
        Assertions.assertEquals(roundQueue.get(1), activeRound.getCurrentTrack());
        Assertions.assertEquals(roundQueue.get(2), activeRound.getNextTrack());
    }

    private List<AudioTrack> prepareRoundQueue() {
        this.audioTrack0 = mockAudioTrack("Dead by April", "Erased");
        this.audioTrack1 = mockAudioTrack("Louna", "1984");
        this.audioTrack2 = mockAudioTrack("Ленина пакет", "карлики два");
        this.audioTrack3 = mockAudioTrack("Linkin park", "Papercut");
        this.audioTrack4 = mockAudioTrack("Король и Шут", "Лесник");
        return Arrays.asList(audioTrack0, audioTrack1, audioTrack2, audioTrack3, audioTrack4);
    }

    private AudioTrack mockAudioTrack(String artist, String title) {
        return AudioTrack.builder()
                .artist(artist)
                .title(title)
                .startInSeconds(20 + 20 * Math.random())
                .finishInSeconds(40 + 20 * Math.random())
                .lengthInSeconds(160 + 20 * Math.random())
                .build();
    }


}
