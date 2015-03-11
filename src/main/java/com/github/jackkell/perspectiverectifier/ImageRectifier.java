package com.github.jackkell.perspectiverectifier;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import util.Matrix;
import util.MatrixSizeException;

import java.util.Random;

public class ImageRectifier {

    public enum RectifyMode {
        MIRROR,
        RANDOM_COLOR,
        TINTED_GREEN
    }

    private Image originalImage;
    private WritableImage rectifiedImage;

    public ImageRectifier() {}

    public WritableImage getRectifiedImage() {
        return rectifiedImage;
    }

    public void create(RectifyMode mode, double rotation) {
        if(originalImage == null)
            return;
        
        clear();

        if(mode == RectifyMode.MIRROR)
            mirror(rectifiedImage);
        if(mode == RectifyMode.RANDOM_COLOR)
            staticfy(rectifiedImage);
        if(mode == RectifyMode.TINTED_GREEN)
            tintGreen(rectifiedImage);

        rotate(rectifiedImage, rotation);
    }

    private void mirror(WritableImage oldImage) {
        PixelReader reader = oldImage.getPixelReader();
        int width = (int) oldImage.getWidth();
        int height = (int) oldImage.getHeight();

        WritableImage newImage = new WritableImage(width, height);
        PixelWriter writer = newImage.getPixelWriter();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color trueColor = reader.getColor(x, y);
                if (y < (height) /2) {
                    writer.setColor(x, y, trueColor);
                    writer.setColor(x, height - 1 - y, trueColor);
                }
            }
        }

        rectifiedImage = newImage;
    }

    private void staticfy(WritableImage oldImage) {
        PixelReader reader = oldImage.getPixelReader();
        int width = (int) oldImage.getWidth();
        int height = (int) oldImage.getHeight();

        WritableImage newImage = new WritableImage(width, height);
        PixelWriter writer = newImage.getPixelWriter();

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

        rectifiedImage = newImage;
    }

    private void tintGreen(WritableImage oldImage) {
        PixelReader reader = oldImage.getPixelReader();
        int width = (int) oldImage.getWidth();
        int height = (int) oldImage.getHeight();

        WritableImage newImage = new WritableImage(width, height);
        PixelWriter writer = newImage.getPixelWriter();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                //Mod Color can be used to change true color to a different value if need be
                Color trueColor = reader.getColor(x, y);
                Color modColor = new Color(
                        trueColor.getRed() * .5,
                        trueColor.getGreen() * 1,
                        trueColor.getBlue() * .5,
                        trueColor.getOpacity()
                );
                writer.setColor(x, y, modColor);
            }
        }

        rectifiedImage = newImage;
    }

    private void rotate(WritableImage oldImage, double angle) {
        PixelReader reader = oldImage.getPixelReader();
        int width = (int) oldImage.getWidth();
        int height = (int) oldImage.getHeight();

        WritableImage newImage = new WritableImage(width, height);
        PixelWriter writer = newImage.getPixelWriter();

        angle = angle % 360;
        double theta = angle * (Math.PI / 180);

        Matrix verticalShear = new Matrix(2, 2, new double[] {1, -Math.tan(theta/2), 0, 1});
        Matrix horizontalShear = new Matrix(2, 2, new double[] {1, 0, Math.sin(theta), 1});
        Matrix rotationMatrix = new Matrix(2, 2, new double[] {Math.cos(theta), Math.sin(theta), -Math.sin(theta), Math.cos(theta)});

        double centerX = width / 2;
        double centerY = height / 2;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color pixelColor = reader.getColor(x, y);
                double xt = x - centerX;
                double yt = y - centerY;

                Matrix mat = new Matrix(2, 1, new double[] {xt, yt});
                Matrix transformedMatrix = null;
                try {
                    transformedMatrix = verticalShear
                        .multiply(horizontalShear)
                        .multiply(verticalShear)
                        .multiply(mat);
                } catch (MatrixSizeException e) {
                    e.printStackTrace();
                }
                double x1 = transformedMatrix.getElements()[0] + centerX;
                double y1 = transformedMatrix.getElements()[1] + centerY;

                if(x1 < 0 || y1 < 0 || x1 > oldImage.getWidth() || y1 > oldImage.getHeight())
                    continue;

                writer.setColor((int) x1, (int) y1, pixelColor);
            }
        }

        rectifiedImage = newImage;
    }

    private void clear() {
        PixelReader reader = originalImage.getPixelReader();
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        WritableImage newImage = new WritableImage(reader, width, height);
        rectifiedImage = newImage;
    }

    public Image getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(Image originalImage) {
        this.originalImage = originalImage;
    }

}
