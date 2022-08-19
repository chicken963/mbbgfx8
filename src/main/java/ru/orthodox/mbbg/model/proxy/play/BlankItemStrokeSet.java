package ru.orthodox.mbbg.model.proxy.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.orthodox.mbbg.enums.StrokeType;
import ru.orthodox.mbbg.model.basic.BlankItem;

import java.util.Set;

@Getter
@AllArgsConstructor
public class BlankItemStrokeSet {
    private StrokeType strokeType;
    private int index;
    Set<BlankItem> blankItems;
}
