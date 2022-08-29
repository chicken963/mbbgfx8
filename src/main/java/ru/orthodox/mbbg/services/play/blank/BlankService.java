package ru.orthodox.mbbg.services.play.blank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.enums.StrokeType;
import ru.orthodox.mbbg.model.basic.*;
import ru.orthodox.mbbg.model.proxy.play.BlankItemStrokeSet;
import ru.orthodox.mbbg.repositories.BlankRepository;
import ru.orthodox.mbbg.repositories.RoundRepository;
import ru.orthodox.mbbg.services.model.RoundService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BlankService {
    @Autowired
    private BlankRepository blankRepository;
    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private RoundService roundService;

    private static int CURRENT_BLANK_NUMBER;

    private final static Map<Integer, String> ROUND_NUMBER_START_LETTERS = new HashMap<Integer, String>() {{
        put(0, "A");
        put(1, "B");
        put(2, "C");
        put(3, "D");
        put(4, "E");
    }};

    public void generateBlanks(Game targetGame) {
        List<Round> rounds = roundRepository.findByIds(targetGame.getRoundIds());
        for (Round round : rounds) {
            List<Blank> blanks = new LinkedList<>();
            CURRENT_BLANK_NUMBER = 100 * (rounds.indexOf(round) + 1);
            List<AudioTrack> roundAudioTracks = roundService.getAudioTracks(round);

            int blankWidth = round.getWidth();
            int blankHeight = round.getHeight();

            List<String> roundArtists = roundAudioTracks.stream()
                    .map(AudioTrack::getArtist)
                    .collect(Collectors.toList());

            for (int i = 0; i < round.getNumberOfBlanks(); i++) {
                Blank blank = Blank.builder()
                        .id(UUID.randomUUID())
                        .blankItems(prepareBlankItems(roundArtists, blankWidth, blankHeight))
                        .progress(0.0)
                        .height(blankHeight)
                        .width(blankWidth)
                        .number(generateBlankNumber(rounds.indexOf(round)))
                        .build();
                blankRepository.save(blank);
                blanks.add(blank);
            }

            round.setBlanksIds(blanks.stream().map(Blank::getId).collect(Collectors.toList()));
            roundRepository.save(round);
        }
    }

    public static Set<BlankItem> getRowItems(Set<BlankItem> blankItems, int rowIndex) {
        return blankItems.stream()
                .filter(blankItem -> blankItem.getYIndex() == rowIndex)
                .collect(Collectors.toSet());
    }

    public static Set<BlankItem> getColumnItems(Set<BlankItem> blankItems, int column) {
        return blankItems.stream()
                .filter(blankItem -> blankItem.getXIndex() == column)
                .collect(Collectors.toSet());
    }

    public static Set<BlankItem> getMainDiagonalItems(Set<BlankItem> blankItems) {
        return blankItems.stream()
                .filter(item -> item.getYIndex() == item.getXIndex())
                .collect(Collectors.toSet());

    }

    public static Set<BlankItem> getSecondaryDiagonalItems(Set<BlankItem> blankItems, int blankSize) {
        int maxBlankItemIndex = blankSize - 1;
        return blankItems.stream()
                .filter(item -> item.getYIndex() == maxBlankItemIndex - item.getXIndex())
                .collect(Collectors.toSet());
    }

    public boolean isMainDiagonal(BlankItemStrokeSet strokeSet) {
        return StrokeType.DIAGONAL.equals(strokeSet.getStrokeType()) && strokeSet.getIndex() == 0;
    }

    private String generateBlankNumber(int roundNumber) {
        return ROUND_NUMBER_START_LETTERS.get(roundNumber) + ++CURRENT_BLANK_NUMBER;
    }

    private Set<BlankItem> prepareBlankItems(List<String> roundArtists, int blankWidth, int blankHeight) {
        Set<BlankItem> blankItems = new HashSet<>();
        Collections.shuffle(roundArtists);
        int blankSize = blankHeight * blankWidth;
        List<String> artistsForBlank = roundArtists.subList(0, blankSize);
        artistsForBlank.forEach(artist -> {
            int index = artistsForBlank.indexOf(artist);
            int x = index % blankWidth;
            int y = index / blankWidth;
            blankItems.add(new BlankItem(x, y, artist));
        });
        return blankItems;
    }
}
