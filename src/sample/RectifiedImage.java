package sample;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.Random;

public class RectifiedImage {

    public enum RectifyMode {
        MIRROR,
        RANDOM_COLOR
    }

    private Image image;
    private WritableImage writableImage;
    private RectifyMode mode;

    public RectifiedImage(Image image) {
        this.image = image;
        writableImage = getWritableImage();
    }

    public void setRectifyMode(RectifyMode mode) {
        this.mode = mode;
    }

    public WritableImage getModifiedImage() {
        switch(mode) {
            case MIRROR:
                return getMirror();
            case RANDOM_COLOR:
                return getRandomColored();
            default:
                return getWritableImage();
        }
    }

    private WritableImage getMirror() {
        PixelReader reader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        PixelWriter writer = writableImage.getPixelWriter();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color trueColor = reader.getColor(x, y);
                if (y < (height) /2) {
                    writer.setColor(x, y, trueColor);
                    writer.setColor(x, height - 1 - y, trueColor);
                }
            }
        }
        return writableImage;
    }

    private WritableImage getRandomColored() {
        PixelReader reader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        PixelWriter writer = writableImage.getPixelWriter();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                //Mod Color can be used to change true color to a different value if need be
                Color trueColor = reader.getColor(x, y);
                Color modColor = new Color(
                        new Random().nextDouble() * 1,
                        new Random().nextDouble() * 1,
                        new Random().nextDouble() * 1,
                        trueColor.getOpacity()
                );
                writer.setColor(x, y, modColor);
            }
        }
        return writableImage;
    }

    private WritableImage getWritableImage() {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        writableImage = new WritableImage(width, height);
        return writableImage;
    }

    public Image getOriginalImage() {
        return image;
    }
}
