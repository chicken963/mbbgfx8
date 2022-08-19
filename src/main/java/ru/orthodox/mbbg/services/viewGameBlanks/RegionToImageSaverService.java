package ru.orthodox.mbbg.services.viewGameBlanks;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class RegionToImageSaverService {

    public void saveToImage(Region regionToSave, File outputFile) {
        WritableImage snapshot = regionToSave.snapshot(new SnapshotParameters(), null);
        saveImage(snapshot, regionToSave, outputFile);
    }

    private void saveImage(WritableImage snapshot, Region regionToSave, File outputFile) {
        BufferedImage image;
        BufferedImage bufferedImage = new BufferedImage(
                (int) regionToSave.getWidth(),
                (int) regionToSave.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        image = javafx.embed.swing.SwingFXUtils.fromFXImage(snapshot, bufferedImage);
        try {
            Graphics2D gd = (Graphics2D) image.getGraphics();
            gd.translate(regionToSave.getWidth(), regionToSave.getHeight());
            ImageIO.write(image, "png", outputFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
