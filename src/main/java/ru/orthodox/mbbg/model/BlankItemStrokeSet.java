package ru.orthodox.mbbg.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.orthodox.mbbg.enums.StrokeType;

import java.util.List;

@Getter
@AllArgsConstructor
public class BlankItemStrokeSet {
    private StrokeType strokeType;
    private int index;
    List<BlankItem> blankItems;
}
