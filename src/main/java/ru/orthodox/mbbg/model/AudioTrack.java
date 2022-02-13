package ru.orthodox.mbbg.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.File;
import java.util.UUID;

@Entity
@Table(name = "tracks")
@Setter
@Getter
public class AudioTrack {
    @Id
    @Column(name = "local_path")
    private String localPath;

    @Column(name = "song_title")
    private String title;

    @Column(name = "artist")
    private String artist;

    @Column(name = "round_id")
    private UUID roundId;
}
