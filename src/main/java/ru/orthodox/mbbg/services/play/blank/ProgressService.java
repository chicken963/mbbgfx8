package ru.orthodox.mbbg.services.play.blank;

import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.enums.StrokeType;
import ru.orthodox.mbbg.enums.WinCondition;
import ru.orthodox.mbbg.model.basic.Blank;
import ru.orthodox.mbbg.model.basic.BlankItem;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.model.proxy.play.BlankItemStrokeSet;
import ru.orthodox.mbbg.services.model.RoundService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.orthodox.mbbg.services.play.blank.BlankService.getMainDiagonalItems;
import static ru.orthodox.mbbg.services.play.blank.BlankService.getSecondaryDiagonalItems;

@Setter
@Service
public class ProgressService {
    @Autowired
    private ItemStrokeSetService itemStrokeSetService;
    @Autowired
    private RoundService roundService;
    @Autowired
    private BlankService blankService;


    public double recalculateRoundProgress(Round round) {

        if (round.getNextTrack() == null) {
            recalculateProgress(round, roundService.getPlayedArtists(round), round.getCurrentTrack().getArtist());
            return 1;
        }

        Set<String> playedArtists = roundService.getPlayedArtists(round);
        String nextArtist = roundService.getNextArtist(round);

        recalculateProgress(round, playedArtists, nextArtist);

        return round.getBlanks().stream()
                .map(Blank::getProgress)
                .mapToDouble(Double::doubleValue)
                .max()
                .getAsDouble();
    }

    private void recalculateProgress(Round round, Set<String> playedArtists, String nextArtist) {
        Set<String> artistsIncludingNext = Stream.concat(playedArtists.stream(), Stream.of(nextArtist))
                .collect(Collectors.toSet());

        round.getBlanks().forEach(blank -> {

            ProgressResult currentProgress = this.countProgress(round, blank, playedArtists);
            blank.setProgress(currentProgress.getProgress());
            if (currentProgress.getProgress() >= 1) {
                blank.setWinningSet(currentProgress.getWinningSet());
            } else {
                blank.setWinningSet(new HashSet<>());
                double nextProgress = this.countProgress(round, blank, artistsIncludingNext).getProgress();
                blank.setNextProgress(nextProgress);
            }
        });
    }



    private ProgressResult countProgress(Round round, Blank blank, Set<String> playedArtists) {
        WinCondition targetWinCondition = round.getCurrentTargetWinCondition();

        Set<BlankItem> strokeBlankItems = blank.getBlankItems().stream()
                .filter(blankItem -> playedArtists.contains(blankItem.getArtist()))
                .collect(Collectors.toSet());

        List<BlankItemStrokeSet> sortedStrokedItems = prepareStrokeItemsForAllTypes(round, strokeBlankItems);
        if (sortedStrokedItems.isEmpty()) {
            return new ProgressResult(0, Collections.emptySet());
        }

        Set<BlankItem> winningItemsSet;
        int targetValue;
        int currentValue;

        switch (targetWinCondition) {
            case ONE_LINE_STRIKE:
                targetValue = blank.getHeight();
                BlankItemStrokeSet closestToWinStrokeSet = sortedStrokedItems.stream()
                        .max(Comparator.comparing(item -> item.getBlankItems().size()))
                        .orElseThrow(() -> new IllegalArgumentException("Failed to find any stroke set"));
                winningItemsSet = getWinningSet(blank, Collections.singletonList(closestToWinStrokeSet));
                currentValue = closestToWinStrokeSet.getBlankItems().size();
                break;
            case THREE_LINES_STRIKE:
                List<BlankItemStrokeSet> closestToWinLinesSet = findClosestToWinSetOfItemsLists(blank, sortedStrokedItems, 3);
                Set<BlankItem> closestToWinSetOfItems = findUnstrickenBlankItems(blank, closestToWinLinesSet);

                int numberLeftToWin = closestToWinSetOfItems.size();
                winningItemsSet = getWinningSet(blank, closestToWinLinesSet);
                targetValue = winningItemsSet.size();
                currentValue = targetValue - numberLeftToWin;
                break;
            default:
                winningItemsSet = blank.getBlankItems();
                targetValue = blank.getHeight() * blank.getWidth();
                currentValue = strokeBlankItems.size();
                break;
        }
        return new ProgressResult(currentValue / (double) targetValue, winningItemsSet);
    }

    private Set<BlankItem> getWinningSet(Blank blank, List<BlankItemStrokeSet> closestToWinSetOfItemsLists) {
        return closestToWinSetOfItemsLists.stream()
                .map(blankItemStrokeSet -> itemStrokeSetService.findBlankItemsForStrokeSet(blank, blankItemStrokeSet))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private List<BlankItemStrokeSet> findClosestToWinSetOfItemsLists(Blank blank, List<BlankItemStrokeSet> sortedStrokedItems, int limit) {
        List<BlankItemStrokeSet> closestToWinStrokeSetList = new ArrayList<>();
        double maxProgressAllOverTheSets = 0;

        List<BlankItemStrokeSet> firstLayerBuffer = new ArrayList<>(sortedStrokedItems);
        for (BlankItemStrokeSet firstFixedSet: firstLayerBuffer) {
            List<BlankItemStrokeSet> secondLayerBuffer = new ArrayList<>(firstLayerBuffer);
            secondLayerBuffer.remove(firstFixedSet);
            for (BlankItemStrokeSet secondFixedSet: secondLayerBuffer) {
                List<BlankItemStrokeSet> thirdLayerBuffer = new ArrayList<>(secondLayerBuffer);
                thirdLayerBuffer.remove(secondFixedSet);
                if (thirdLayerBuffer.isEmpty()) {
                    return Stream.concat(
                            sortedStrokedItems.stream(),
                            Stream.of(new BlankItemStrokeSet(StrokeType.DIAGONAL, 0, Collections.emptySet())))
                            .collect(Collectors.toList());
                }
                for (BlankItemStrokeSet thirdFixedSet: thirdLayerBuffer) {
                    List<BlankItemStrokeSet> listToAnalyze = new ArrayList<BlankItemStrokeSet>(){{
                        add(firstFixedSet);
                        add(secondFixedSet);
                        add(thirdFixedSet);
                    }};
                    Set<BlankItem> closestToWinBlankItemsSetCandidate = findUnstrickenBlankItems(blank, listToAnalyze);
                    Set<BlankItem> strickenItems = findStrickenBlankItems(listToAnalyze);
                    double expectedProgress = strickenItems.size() / ((double) strickenItems.size() + closestToWinBlankItemsSetCandidate.size());
                    if (expectedProgress > maxProgressAllOverTheSets && expectedProgress >= blank.getProgress()) {
                        maxProgressAllOverTheSets = expectedProgress;
                        closestToWinStrokeSetList = listToAnalyze;
                    }
                }
            }
        }
        return closestToWinStrokeSetList;

    }

    private Set<BlankItem> findUnstrickenBlankItems(Blank blank, List<BlankItemStrokeSet> listToAnalyze) {
        return listToAnalyze.stream()
            .map(strokeSet -> findUnstrickenItemsForSet(blank, strokeSet))
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }

    private Set<BlankItem> findStrickenBlankItems(List<BlankItemStrokeSet> listToAnalyze) {
        return listToAnalyze.stream()
            .map(BlankItemStrokeSet::getBlankItems)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }

    private List<BlankItem> findUnstrickenItemsForSet(Blank blank, BlankItemStrokeSet strokeSet) {
        switch (strokeSet.getStrokeType()) {
            case ROW:
                return blank.getBlankItems().stream()
                        .filter(item -> item.getYIndex() == strokeSet.getIndex())
                        .filter(rowItem -> !strokeSet.getBlankItems().contains(rowItem))
                        .collect(Collectors.toList());
            case COLUMN:
                return blank.getBlankItems().stream()
                        .filter(item -> item.getXIndex() == strokeSet.getIndex())
                        .filter(columnItem -> !strokeSet.getBlankItems().contains(columnItem))
                        .collect(Collectors.toList());
            default:
                if (blankService.isMainDiagonal(strokeSet)) {
                    return blank.getBlankItems().stream()
                            .filter(item -> item.getYIndex() == item.getXIndex())
                            .filter(diagonalItem -> !strokeSet.getBlankItems().contains(diagonalItem))
                            .collect(Collectors.toList());
                } else {
                    return blank.getBlankItems().stream()
                            .filter(item -> item.getYIndex() == blank.getWidth() - item.getXIndex() - 1)
                            .filter(diagonalItem -> !strokeSet.getBlankItems().contains(diagonalItem))
                            .collect(Collectors.toList());
                }
        }
    }

    private List<BlankItemStrokeSet> prepareStrokeItemsForAllTypes(Round round, Set<BlankItem> strokeBlankItems) {
        List<BlankItemStrokeSet> result = new ArrayList<>();

        List<BlankItemStrokeSet> itemsByRows = sortItemsByRows(strokeBlankItems);
        List<BlankItemStrokeSet> itemsByColumns = sortItemsByColumns(strokeBlankItems);
        List<BlankItemStrokeSet> itemsByDiagonals = sortItemsByDiagonals(round, strokeBlankItems);

        result.addAll(itemsByRows);
        result.addAll(itemsByColumns);
        result.addAll(itemsByDiagonals);

        return result;
    }

    private List<BlankItemStrokeSet> sortItemsByDiagonals(Round round, Set<BlankItem> strokeBlankItems) {

        Set<BlankItem> mainDiagonalItems = getMainDiagonalItems(strokeBlankItems);
        Set<BlankItem> secondaryDiagonalItems = getSecondaryDiagonalItems(strokeBlankItems, round.getHeight());

        List<BlankItemStrokeSet> diagonalStrokeSets = new ArrayList<>();

        if (!mainDiagonalItems.isEmpty()) {
            diagonalStrokeSets.add(new BlankItemStrokeSet(StrokeType.DIAGONAL, 0, mainDiagonalItems));
        }
        if (!secondaryDiagonalItems.isEmpty()) {
            diagonalStrokeSets.add(new BlankItemStrokeSet(StrokeType.DIAGONAL, 1, secondaryDiagonalItems));
        }
        return diagonalStrokeSets;
    }

    private List<BlankItemStrokeSet> sortItemsByColumns(Set<BlankItem> strokeBlankItems) {
        return strokeBlankItems.stream()
                .collect(Collectors.groupingBy(BlankItem::getXIndex, Collectors.toSet()))
                .entrySet()
                .stream()
                .map(entry -> new BlankItemStrokeSet(StrokeType.COLUMN, entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private List<BlankItemStrokeSet> sortItemsByRows(Set<BlankItem> strokeBlankItems) {
        return strokeBlankItems.stream()
                .collect(Collectors.groupingBy(BlankItem::getYIndex, Collectors.toSet()))
                .entrySet()
                .stream()
                .map(entry -> new BlankItemStrokeSet(StrokeType.ROW, entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Data
    private static class ProgressResult {
        private final double progress;
        private final Set<BlankItem> winningSet;
    }
}
