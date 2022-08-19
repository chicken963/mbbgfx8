package ru.orthodox.mbbg.services.play.blank;

import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.Blank;
import ru.orthodox.mbbg.model.basic.BlankItem;
import ru.orthodox.mbbg.model.proxy.play.BlankItemStrokeSet;

import java.util.List;
import java.util.Set;

@Service
public class ItemStrokeSetService {

    public Set<BlankItem> findBlankItemsForStrokeSet(Blank blank, BlankItemStrokeSet blankItemStrokeSet) {
        return findBlankItemsForStrokeSet(blank.getBlankItems(), blankItemStrokeSet, blank.getHeight());
    }

    public Set<BlankItem> findBlankItemsForStrokeSet(Set<BlankItem> blankItems, BlankItemStrokeSet blankItemStrokeSet, int blankSize) {
        switch (blankItemStrokeSet.getStrokeType()) {

            case ROW:
                int rowIndex = blankItemStrokeSet.getIndex();
                return BlankService.getRowItems(blankItems, rowIndex);

            case COLUMN:
                int columnIndex = blankItemStrokeSet.getIndex();
                return BlankService.getColumnItems(blankItems, columnIndex);

            default:
                return blankItemStrokeSet.getIndex() == 0
                        ? BlankService.getMainDiagonalItems(blankItems)
                        : BlankService.getSecondaryDiagonalItems(blankItems, blankSize);
        }
    }
}
