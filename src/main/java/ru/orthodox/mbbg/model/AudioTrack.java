package ru.orthodox.mbbg.model;

import lombok.*;

import javax.persistence.*;
import java.io.File;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AudioTrack {

    @Column(name = "localPath")
    private String localPath;

    @Column(name = "title")
    private String title;

    @Column(name = "artist")
    private String artist;

    @Column(name = "roundId")
    private UUID roundId;
}
