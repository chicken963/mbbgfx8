package ru.orthodox.mbbg.services;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.enums.StrokeType;
import ru.orthodox.mbbg.enums.WinCondition;
import ru.orthodox.mbbg.model.*;
import ru.orthodox.mbbg.repositories.BlankRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.orthodox.mbbg.ui.hierarchy.NodeDeepCopyProvider.createDeepCopy;

@Setter
@Service
public class BlanksProgressService {
    @Autowired
    private BlankRepository blankRepository;

    private GridPane miniaturesProgressGrid;
    private RowConstraints progressRowsConstraints;
    private Round activeRound;
    private Button blankMiniature;
    private static final double GRID_WIDTH = 5.0;
    private List<Blank> blanks;
    private Map<Blank, Button> blanksAndMiniatures;

    @Setter(AccessLevel.NONE)
    private List<Button> blankMiniatures;

    public void render(Round round) {
        this.activeRound = round;
        this.blanks = blankRepository.findByIds(round.getBlanksIds());

        miniaturesProgressGrid.getRowConstraints().setAll(
                Stream.generate(() -> createDeepCopy(progressRowsConstraints))
                        .limit((int) Math.ceil(activeRound.getBlanksIds().size() / GRID_WIDTH))
                        .collect(Collectors.toList()));
        AtomicInteger counter = new AtomicInteger(0);
        blankMiniatures = blanks.stream()
                .map(Blank::getNumber)
                .map(blankNumber -> {
                    Button button = createDeepCopy(blankMiniature);
                    button.setText(blankNumber);
                    GridPane.setColumnIndex(button, counter.get() % (int) GRID_WIDTH);
                    GridPane.setRowIndex(button, counter.get() / (int) GRID_WIDTH);
                    counter.getAndIncrement();
                    return button;
                })
                .collect(Collectors.toList());
        blanksAndMiniatures = blanks.stream()
                .collect(Collectors.toMap(Function.identity(),
                        blank -> blankMiniatures.stream()
                                .filter(button -> button.getText().equals(blank.getNumber()))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("There is no blank miniature with number " + blank.getNumber()))));
        miniaturesProgressGrid.getChildren().setAll(blankMiniatures);

    }

    public double recalculateProgress(WinCondition targetWinCondition) {
        Set<String> playedArtists = activeRound.getPlayedAudiotracks().stream()
                .map(AudioTrack::getArtist)
                .collect(Collectors.toSet());

        blanks.forEach(blank -> this.updateProgress(blank, playedArtists, targetWinCondition));

        for (Map.Entry<Blank, Button> blankMiniature: blanksAndMiniatures.entrySet()) {
            blankMiniature.getValue().setStyle(new StringBuilder("-fx-background-color: ")
                    .append("linear-gradient(to right, orange ")
                    .append(blankMiniature.getKey().getProgress() * 100)
                    .append("%, #baa ")
                    .append(blankMiniature.getKey().getProgress() * 100)
                    .append("%)")
                    .toString());
        }

        return blanks.stream()
                .map(Blank::getProgress)
                .mapToDouble(Double::doubleValue)
                .max()
                .getAsDouble();
    }

    private void updateProgress(Blank blank, Set<String> playedArtists, WinCondition targetWinCondition) {
        List<BlankItem> strokeBlankItems = blank.getBlankItems().stream()
                .filter(blankItem -> playedArtists.contains(blankItem.getArtist()))
                .collect(Collectors.toList());

        List<BlankItemStrokeSet> sortedStrokedItems = sortStrokedItemsByType(strokeBlankItems);
        int targetValue;
        int currentValue;

        switch (targetWinCondition) {
            case ONE_LINE_STRIKE:
                targetValue = blank.getHeight();
                currentValue = sortedStrokedItems.stream()
                        .sorted(Comparator.comparing(item -> item.getBlankItems().size(), Comparator.reverseOrder()))
                        .map(BlankItemStrokeSet::getBlankItems)
                        .collect(Collectors.toList())
                        .get(0)
                        .size();
                break;
            case THREE_LINES_STRIKE:
                List<BlankItemStrokeSet> closestToWinLinesSet = findClosestToWinSetOfItemsLists(blank, sortedStrokedItems, 3);
                Set<BlankItem> closestToWinSetOfItemsLists = findUnstrickenBlankItems(blank, closestToWinLinesSet);
                int numberLeftToWin = closestToWinSetOfItemsLists.size();
                targetValue = calculateTargetValue(blank, closestToWinLinesSet, 3);
                currentValue = targetValue - numberLeftToWin;
                break;
            default:
                targetValue = blank.getHeight() * blank.getWidth();
                currentValue = strokeBlankItems.size();
                break;
        }
        blank.setProgress(currentValue / (double) targetValue);
    }

    private int calculateTargetValue(Blank blank, List<BlankItemStrokeSet> closestToWinSetOfItemsLists, int numberOfLines) {
        if (containsDiagonalElement(closestToWinSetOfItemsLists)) {
            return blank.getHeight() * numberOfLines - 2;
        } else if (elementsAreFromDifferentDimensions(closestToWinSetOfItemsLists)) {
            return blank.getHeight() * numberOfLines - 1;
        }
        return blank.getHeight() * numberOfLines;
    }

    private boolean elementsAreFromDifferentDimensions(List<BlankItemStrokeSet> closestToWinSetOfItemsLists) {
        return closestToWinSetOfItemsLists.stream()
                .anyMatch(blankItemStrokeSet -> blankItemStrokeSet.getStrokeType().equals(StrokeType.ROW))
        && closestToWinSetOfItemsLists.stream()
                .anyMatch(blankItemStrokeSet -> blankItemStrokeSet.getStrokeType().equals(StrokeType.COLUMN));
    }

    private boolean containsDiagonalElement(List<BlankItemStrokeSet> closestToWinSetOfItemsLists) {
        return closestToWinSetOfItemsLists.stream()
                .anyMatch(blankItemStrokeSet -> blankItemStrokeSet.getStrokeType().equals(StrokeType.DIAGONAL));
    }

    private List<BlankItemStrokeSet> findClosestToWinSetOfItemsLists(Blank blank, List<BlankItemStrokeSet> sortedStrokedItems, int limit) {
        Set<BlankItem> closestToWinBlankItemsSet = new HashSet<>();
        List<BlankItemStrokeSet> closestToWinStrokeSetList = new ArrayList<>();

        List<BlankItemStrokeSet> firstLayerBuffer = new ArrayList<>(sortedStrokedItems);
        for (BlankItemStrokeSet firstFixedSet: firstLayerBuffer) {
            List<BlankItemStrokeSet> secondLayerBuffer = new ArrayList<>(firstLayerBuffer);
            secondLayerBuffer.remove(firstFixedSet);
            for (BlankItemStrokeSet secondFixedSet: secondLayerBuffer) {
                List<BlankItemStrokeSet> thirdLayerBuffer = new ArrayList<>(secondLayerBuffer);
                thirdLayerBuffer.remove(secondFixedSet);
                for (BlankItemStrokeSet thirdFixedSet: thirdLayerBuffer) {
                    List<BlankItemStrokeSet> listToAnalyze = new ArrayList<BlankItemStrokeSet>(){{
                        add(firstFixedSet);
                        add(secondFixedSet);
                        add(thirdFixedSet);
                    }};
                    Set<BlankItem> closestToWinBlankItemsSetCandidate = findUnstrickenBlankItems(blank, listToAnalyze);
                    if (closestToWinBlankItemsSet.isEmpty() || closestToWinBlankItemsSetCandidate.size() < closestToWinBlankItemsSet.size()) {
                        closestToWinBlankItemsSet = closestToWinBlankItemsSetCandidate;
                        closestToWinStrokeSetList = listToAnalyze;
                    }
                }
            }
        }
        return closestToWinStrokeSetList;

    }

    private Set<BlankItem> findUnstrickenBlankItems(Blank blank, List<BlankItemStrokeSet> listToAnalyze) {
        Set<BlankItem> closestToWinBlankItemsSetCandidate = listToAnalyze.stream()
                .map(strokeSet -> findUnstrickenItemsForSet(blank, strokeSet))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        return closestToWinBlankItemsSetCandidate;
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
                if (isMainDiagonal(strokeSet)) {
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

    private boolean isMainDiagonal(BlankItemStrokeSet strokeSet) {
        return strokeSet.getBlankItems().stream()
                .allMatch(blankItem -> blankItem.getXIndex() == blankItem.getYIndex());
    }

    private List<BlankItemStrokeSet> sortStrokedItemsByType(List<BlankItem> strokeBlankItems) {
        List<BlankItemStrokeSet> result = new ArrayList<>();

        List<BlankItemStrokeSet> itemsByRows = sortItemsByRows(strokeBlankItems);
        List<BlankItemStrokeSet> itemsByColumns = sortItemsByColumns(strokeBlankItems);
        List<BlankItemStrokeSet> itemsByDiagonals = sortItemsByDiagonals(strokeBlankItems);

        result.addAll(itemsByRows);
        result.addAll(itemsByColumns);
        result.addAll(itemsByDiagonals);

        return result;
    }

    private List<BlankItemStrokeSet> sortItemsByDiagonals(List<BlankItem> strokeBlankItems) {

        List<BlankItem> mainDiagonalItems = strokeBlankItems.stream()
                .filter(item -> item.getXIndex() == item.getYIndex())
                .collect(Collectors.toList());

        List<BlankItem> secondaryDiagonalItems = strokeBlankItems.stream()
                .filter(item -> item.getXIndex() == activeRound.getHeight() - item.getYIndex() - 1)
                .collect(Collectors.toList());

        return new ArrayList<BlankItemStrokeSet>(){{
            add(new BlankItemStrokeSet(StrokeType.DIAGONAL, 0, mainDiagonalItems));
            add(new BlankItemStrokeSet(StrokeType.DIAGONAL, 1, secondaryDiagonalItems));
        }};
    }

    private List<BlankItemStrokeSet> sortItemsByColumns(List<BlankItem> strokeBlankItems) {
        return strokeBlankItems.stream()
                .collect(Collectors.groupingBy(BlankItem::getXIndex))
                .entrySet()
                .stream()
                .map(entry -> new BlankItemStrokeSet(StrokeType.COLUMN, entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private List<BlankItemStrokeSet> sortItemsByRows(List<BlankItem> strokeBlankItems) {
        return strokeBlankItems.stream()
                .collect(Collectors.groupingBy(BlankItem::getYIndex))
                .entrySet()
                .stream()
                .map(entry -> new BlankItemStrokeSet(StrokeType.ROW, entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
