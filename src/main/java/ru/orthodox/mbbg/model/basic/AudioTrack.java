package ru.orthodox.mbbg.model.basic;

import lombok.*;
import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AudioTrack implements MarkedWithId {

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Exclude
    private UUID id;

    @Column(name = "localPath")
    private String localPath;

    @Column(name = "title")
    private String title;

    @Column(name = "artist")
    private String artist;

    @Column(name = "startInSeconds")
    private double startInSeconds;

    @Column(name = "finishInSeconds")
    private double finishInSeconds;

    @Column(name = "lengthInSeconds")
    private double lengthInSeconds;

    @Override
    public String toString() {
        return this.title + " - " + this.artist;
    }

}
