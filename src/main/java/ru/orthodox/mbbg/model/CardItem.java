package ru.orthodox.mbbg.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardItem {

    @Column(name = "xIndex")
    private int xIndex;

    @Column(name = "yIndex")
    private int yIndex;

    @Column(name = "artist")
    private String artist;
}
