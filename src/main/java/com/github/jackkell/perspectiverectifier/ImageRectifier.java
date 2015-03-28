package com.github.jackkell.perspectiverectifier;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import util.Matrix;
import util.MatrixSizeException;
import util.Pair;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Random;

public class ImageRectifier {

    public enum RectifyMode {
        NONE,
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

    public void create(RectifyMode mode, double rotation, Pair[] points) {
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
		intersectingPoints(points);
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
        //Matrix rotationMatrix = new Matrix(2, 2, new double[] {Math.cos(theta), Math.sin(theta), -Math.sin(theta), Math.cos(theta)});

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
                    System.out.println("We fucked up. " + e.getMessage());
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

	private void intersectingPoints(Pair<Double, Double>[] points) {
		if(points[0] == null) {
			return;
		}

		Line2D.Double topLine = new Line2D.Double(points[0].getX(), points[0].getY(), points[1].getX(), points[1].getY());
		Line2D.Double  bottomLine = new Line2D.Double(points[2].getX(), points[2].getY(), points[3].getX(), points[3].getY());

		System.out.println("TopLine 1st Point: " + topLine.getX1() + ", " + topLine.getY1());
		System.out.println("TopLine 2nd Point: " + topLine.getX2() + ", " + topLine.getY2());

		System.out.println("BotLine 1st Point: " + bottomLine.getX1() + ", " + bottomLine.getY1());
		System.out.println("BotLine 2nd Point: " + bottomLine.getX2() + ", " + bottomLine.getY2());

		double topSlope = (topLine.getY2() - topLine.getY1()) / (topLine.getX2() - topLine.getX1());
		double bottomSlope = (bottomLine.getY2() - bottomLine.getY1()) / (bottomLine.getX2() - bottomLine.getX1());
		double topIntercept = topLine.getY1() - (topSlope * topLine.getX1());
		double bottomIntercept = bottomLine.getY1() - (bottomSlope * bottomLine.getX1());

		System.out.println("TopSlope: " + topSlope);
		System.out.println("TopIntercept: " + topIntercept);
		System.out.println("BottomSlope: " + bottomSlope);
		System.out.println("BottomIntercept: " + bottomIntercept);

		//Matrix system = new Matrix(2, 3, new double[] {-topSlope, 1, topIntercept, -bottomSlope, 1, bottomIntercept});
        Matrix system = new Matrix(2, 3, new double[] {topSlope, -1, -topIntercept, bottomSlope, -1, -bottomIntercept});
		Matrix solution = system.solveSystem();
		System.out.println("Intersection X: " + solution.getElements()[0] + " Y: " + solution.getElements()[1]);
	}

	public Point2D.Double getIntersectionPoint(Line2D.Double line1, Line2D.Double line2) {
		if (! line1.intersectsLine(line2) ) return null;
		double px = line1.getX1(),
				py = line1.getY1(),
				rx = line1.getX2()-px,
				ry = line1.getY2()-py;
		double qx = line2.getX1(),
				qy = line2.getY1(),
				sx = line2.getX2()-qx,
				sy = line2.getY2()-qy;

		double det = sx*ry - sy*rx;
		if (det == 0) {
			return null;
		} else {
			double z = (sx*(qy-py)+sy*(px-qx))/det;
			if (z==0 ||  z==1) return null;  // intersection at end point!
			return new Point2D.Double(
					(double)(px+z*rx), (double)(py+z*ry));
		}
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
