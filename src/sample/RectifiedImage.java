package sample;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class RectifiedImage {

    public enum RectifyMode {
        MIRROR
    }

    private Image image;
    private WritableImage writableImage;
    private RectifyMode mode;

    public RectifiedImage(Image image) {
        this.image = image;

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        writableImage = new WritableImage(width, height);

        mode = RectifyMode.MIRROR;
    }
}
