package ru.orthodox.mbbg.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlankItem {

    @Column(name = "xIndex")
    private int xIndex;

    @Column(name = "yIndex")
    private int yIndex;

    @Column(name = "artist")
    private String artist;
}
