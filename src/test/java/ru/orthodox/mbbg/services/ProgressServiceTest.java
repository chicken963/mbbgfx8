package ru.orthodox.mbbg.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import ru.orthodox.mbbg.enums.WinCondition;
import ru.orthodox.mbbg.model.basic.Blank;
import ru.orthodox.mbbg.model.basic.BlankItem;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.model.proxy.play.BlankItemStrokeSet;
import ru.orthodox.mbbg.services.model.RoundService;
import ru.orthodox.mbbg.services.play.blank.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {
        ProgressService.class,
        ItemStrokeSetService.class,
        RoundService.class,
        MiniaturesGridService.class,
        AnimationBackgroundService.class,
        BlankService.class
})
public class ProgressServiceTest {

    private static final String ARTIST00 = "Eminem";
    private static final String ARTIST01 = "Evanescence";
    private static final String ARTIST02 = "Slipknot";
    private static final String ARTIST03 = "Linkin Park";
    private static final String ARTIST10 = "Crazy town";
    private static final String ARTIST11 = "Bonch bru bonch";
    private static final String ARTIST12 = "Гио Пика";
    private static final String ARTIST13 = "BTS";
    private static final String ARTIST20 = "Алиса";
    private static final String ARTIST21 = "Fall out boy";
    private static final String ARTIST22 = "Stigmata";
    private static final String ARTIST23 = "Rock Privet";
    private static final String ARTIST30 = "Кино";
    private static final String ARTIST31 = "Аквариум";
    private static final String ARTIST32 = "Земфира";
    private static final String ARTIST33 = "Сплин";

    private static final String ARTIST_NOT_FROM_BLANK = "Korn";

    private static final List<String> BLANK_ARTISTS = Arrays.asList(
            ARTIST00, ARTIST01, ARTIST02, ARTIST03,
            ARTIST10, ARTIST11, ARTIST12, ARTIST13,
            ARTIST20, ARTIST21, ARTIST22, ARTIST23,
            ARTIST30, ARTIST31, ARTIST32, ARTIST33);

    private static final int blankSize = 4;

    private Blank blank;
    private Round round;
    @InjectMocks
    ProgressService progressService;
    @Mock
    private ItemStrokeSetService itemStrokeSetService;
    @Mock
    private RoundService roundService;
    @Mock
    private MiniaturesGridService miniaturesGridService;
    @Mock
    private AnimationBackgroundService animationProvider;
    @Mock
    private BlankService blankService;


    @BeforeEach
    public void setup() {
        Set<BlankItem> blankItems = BLANK_ARTISTS.stream().map(artist -> {
            int currentIndex = BLANK_ARTISTS.indexOf(artist);
            return new BlankItem(currentIndex % blankSize, currentIndex / blankSize, artist);
        }).collect(Collectors.toSet());
        blank = Blank.builder()
                .id(UUID.randomUUID())
                .blankItems(blankItems)
                .progress(0.0)
                .height(blankSize)
                .width(blankSize)
                .number("A101")
                .build();

        Round round = new Round();
        round.setWidth(blankSize);
        round.setHeight(blankSize);
        round.setBlanks(Collections.singletonList(blank));
        this.round = round;
    }

    @ParameterizedTest
    @MethodSource("oneLineStrikeTestSet")
    public void oneLineStrikeTest(Set<String> playedArtists, Double expectedProgress) {
        if (playedArtists.isEmpty()) {
            playedArtists = Collections.singleton(ARTIST_NOT_FROM_BLANK);
        } else {
            playedArtists.add(ARTIST_NOT_FROM_BLANK);
        }
        round.setCurrentTargetWinCondition(WinCondition.ONE_LINE_STRIKE);
        ReflectionTestUtils.invokeMethod(progressService, "recalculateProgress",
                round, playedArtists, ARTIST_NOT_FROM_BLANK);
        Assertions.assertEquals(expectedProgress, blank.getProgress());
    }

    public static Stream<Arguments> oneLineStrikeTestSet() {
        return Stream.of(
                Arguments.of(Collections.emptySet(), 0.0),

                //exactly one in any place
                Arguments.of(Stream.of(ARTIST00).collect(Collectors.toSet()), 0.25),
                Arguments.of(Stream.of(ARTIST10).collect(Collectors.toSet()), 0.25),
                Arguments.of(Stream.of(ARTIST01).collect(Collectors.toSet()), 0.25),
                Arguments.of(Stream.of(ARTIST23).collect(Collectors.toSet()), 0.25),

                //exactly two in each of the dimensions
                Arguments.of(Stream.of(ARTIST00, ARTIST02).collect(Collectors.toSet()), 0.5),
                Arguments.of(Stream.of(ARTIST00, ARTIST20).collect(Collectors.toSet()), 0.5),
                Arguments.of(Stream.of(ARTIST00, ARTIST22).collect(Collectors.toSet()), 0.5),
                Arguments.of(Stream.of(ARTIST12, ARTIST21).collect(Collectors.toSet()), 0.5),

                //two in linked dimensions
                Arguments.of(Stream.of(ARTIST00, ARTIST10, ARTIST02).collect(Collectors.toSet()), 0.5),
                Arguments.of(Stream.of(ARTIST11, ARTIST10, ARTIST22).collect(Collectors.toSet()), 0.5),
                Arguments.of(Stream.of(ARTIST11, ARTIST01, ARTIST22).collect(Collectors.toSet()), 0.5),
                Arguments.of(Stream.of(ARTIST11, ARTIST12, ARTIST21).collect(Collectors.toSet()), 0.5),
                Arguments.of(Stream.of(ARTIST02, ARTIST21, ARTIST12).collect(Collectors.toSet()), 0.5),
                Arguments.of(Stream.of(ARTIST00, ARTIST10, ARTIST02, ARTIST33).collect(Collectors.toSet()), 0.5),

                //two in one dimension and one in another - not connected with the first
                Arguments.of(Stream.of(ARTIST00, ARTIST12, ARTIST03).collect(Collectors.toSet()), 0.5),
                Arguments.of(Stream.of(ARTIST00, ARTIST12, ARTIST30).collect(Collectors.toSet()), 0.5),
                Arguments.of(Stream.of(ARTIST00, ARTIST12, ARTIST33).collect(Collectors.toSet()), 0.5),
                Arguments.of(Stream.of(ARTIST30, ARTIST32, ARTIST03).collect(Collectors.toSet()), 0.5),

                //exactly three
                Arguments.of(Stream.of(ARTIST00, ARTIST02, ARTIST03).collect(Collectors.toSet()), 0.75),
                Arguments.of(Stream.of(ARTIST10, ARTIST20, ARTIST30).collect(Collectors.toSet()), 0.75),
                Arguments.of(Stream.of(ARTIST11, ARTIST22, ARTIST33).collect(Collectors.toSet()), 0.75),
                Arguments.of(Stream.of(ARTIST03, ARTIST21, ARTIST30).collect(Collectors.toSet()), 0.75),

                //three in not related dimensions
                Arguments.of(Stream.of(ARTIST10, ARTIST01, ARTIST22).collect(Collectors.toSet()), 0.25),
                //four in not related dimensions
                Arguments.of(Stream.of(ARTIST10, ARTIST23, ARTIST32).collect(Collectors.toSet()), 0.25),

                //two in related dimension, two in not related
                Arguments.of(Stream.of(ARTIST30, ARTIST12, ARTIST33, ARTIST01).collect(Collectors.toSet()), 0.5),
                Arguments.of(Stream.of(ARTIST03, ARTIST21, ARTIST33, ARTIST10).collect(Collectors.toSet()), 0.5),

                //two by two groups
                Arguments.of(Stream.of(ARTIST11, ARTIST22, ARTIST03, ARTIST30).collect(Collectors.toSet()), 0.5),
                Arguments.of(Stream.of(ARTIST11, ARTIST13, ARTIST02, ARTIST32).collect(Collectors.toSet()), 0.5),

                //three in corner, fourth is alone
                Arguments.of(Stream.of(ARTIST10, ARTIST13, ARTIST02, ARTIST32).collect(Collectors.toSet()), 0.5),

                //three in line, fourth is alone
                Arguments.of(Stream.of(ARTIST01, ARTIST03, ARTIST02, ARTIST20).collect(Collectors.toSet()), 0.75),
                Arguments.of(Stream.of(ARTIST10, ARTIST30, ARTIST20, ARTIST02).collect(Collectors.toSet()), 0.75),

                //three in line, fourth in linked dimension
                Arguments.of(Stream.of(ARTIST11, ARTIST13, ARTIST12, ARTIST32).collect(Collectors.toSet()), 0.75),
                Arguments.of(Stream.of(ARTIST11, ARTIST31, ARTIST21, ARTIST23).collect(Collectors.toSet()), 0.75),
                Arguments.of(Stream.of(ARTIST11, ARTIST22, ARTIST33, ARTIST23).collect(Collectors.toSet()), 0.75),
                Arguments.of(Stream.of(ARTIST21, ARTIST12, ARTIST03, ARTIST23).collect(Collectors.toSet()), 0.75),

                //exactly four
                Arguments.of(Stream.of(ARTIST01, ARTIST02, ARTIST03, ARTIST00).collect(Collectors.toSet()), 1.0),
                Arguments.of(Stream.of(ARTIST12, ARTIST22, ARTIST32, ARTIST02).collect(Collectors.toSet()), 1.0),
                Arguments.of(Stream.of(ARTIST00, ARTIST11, ARTIST22, ARTIST33).collect(Collectors.toSet()), 1.0),
                Arguments.of(Stream.of(ARTIST03, ARTIST12, ARTIST21, ARTIST30).collect(Collectors.toSet()), 1.0)
        );
    }

    @ParameterizedTest
    @MethodSource("threeLineStrikeTestSet")
    public void threeLinesStrikeTestWhenFirstLineIsStroke(Set<String> playedArtists, Double expectedProgress) {
        playedArtists.add(ARTIST_NOT_FROM_BLANK);
        playedArtists.add(ARTIST00);
        playedArtists.add(ARTIST01);
        playedArtists.add(ARTIST02);
        playedArtists.add(ARTIST03);
        round.setCurrentTargetWinCondition(WinCondition.THREE_LINES_STRIKE);

        lenient().when(itemStrokeSetService.findBlankItemsForStrokeSet(any(Blank.class), any(BlankItemStrokeSet.class))).thenCallRealMethod();
        lenient().when(itemStrokeSetService.findBlankItemsForStrokeSet(Matchers.anySet(), any(BlankItemStrokeSet.class), anyInt())).thenCallRealMethod();

        ReflectionTestUtils.invokeMethod(progressService, "recalculateProgress",
                round, playedArtists, ARTIST_NOT_FROM_BLANK);
        Assertions.assertEquals(expectedProgress, blank.getProgress());
    }

    public static Stream<Arguments> threeLineStrikeTestSet() {
        return Stream.of(
                //fixed first line
                Arguments.of(Stream.of(ARTIST30).collect(Collectors.toSet()), 5/9.0),
                Arguments.of(Stream.of(ARTIST20).collect(Collectors.toSet()), 5/9.0),
                Arguments.of(Stream.of(ARTIST31).collect(Collectors.toSet()), 5/9.0)
                );
    }
}
