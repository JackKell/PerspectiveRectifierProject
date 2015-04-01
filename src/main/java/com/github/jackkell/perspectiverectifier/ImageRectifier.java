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

import java.awt.*;
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

    public void create(RectifyMode mode, double rotation, Point2D[] points) {
        if(originalImage == null)
            return;
        
        clear();

        //if(mode == RectifyMode.MIRROR)
            //mirror(rectifiedImage);
        //if(mode == RectifyMode.RANDOM_COLOR)
            //staticfy(rectifiedImage);
        //if(mode == RectifyMode.TINTED_GREEN)
            //tintGreen(rectifiedImage);

        //rotate(rectifiedImage, rotation);
        if(points[0] != null) {
            rectify(rectifiedImage, intersectingPoints(points), points);
        }
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

	public Point2D intersectingPoints(Point2D[] points) {
		if(points[0] == null) {
			return null;
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
        Point2D intersectionPoint = new Point2D.Double(solution.getElements()[0], solution.getElements()[1]);

        return intersectionPoint;
	}

    private void rectify(WritableImage oldImage, Point2D vanishingPoint, Point2D[] points) {
        PixelReader reader = oldImage.getPixelReader();
        int width = (int) oldImage.getWidth();
        int height = (int) oldImage.getHeight();

        WritableImage newImage = new WritableImage(width, height);
        PixelWriter writer = newImage.getPixelWriter();

        double centerX =(points[1].getX() + points[0].getX()) / 2;
		double centerY = vanishingPoint.getY();

		Point2D originPrime = new Point2D.Double(centerX, centerY);
		Point2D vanishingPointPrime = pointToPrime(originPrime, vanishingPoint);

        // p = parameter
        double parameter = 1.0 / vanishingPointPrime.getX();

        Matrix transformationMatrix = new Matrix(4, 4, new double[] {1, 0, 0, parameter, 0, 1, 0, 0, 0, 0, 0, 0, 0, .1, 0, 1});

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color pixelColor = reader.getColor(x, y);
				Point2D originalPoint = new Point2D.Double(x, y);

				Point2D pointPrime = pointToPrime(originPrime, originalPoint);
                Matrix pixelCoord = new Matrix(1,4, new double[] {pointPrime.getX(), pointPrime.getY(), 0, 1});
                Matrix newPixelCoord = null;

               try {
                   newPixelCoord = pixelCoord.multiply(transformationMatrix);
				   double xCoord = newPixelCoord.getElements()[0];
				   double yCoord = newPixelCoord.getElements()[1];

				   newPixelCoord.getElements()[0] = xCoord / (xCoord * parameter + 1);
				   newPixelCoord.getElements()[1] = yCoord / (xCoord * parameter + 1);
                } catch (MatrixSizeException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }

				Point2D newPoint = primeToPoint(originPrime, new Point2D.Double(newPixelCoord.getElements()[0], newPixelCoord.getElements()[1]));

                // checks to make sure x1 and y1 are within the image frame
                if(newPoint.getX() < 0 || newPoint.getY() < 0 || newPoint.getX() > oldImage.getWidth() || newPoint.getY() > oldImage.getHeight())
                    continue;

                // draws our new image
                writer.setColor((int) newPoint.getX(), (int) newPoint.getY(), pixelColor);
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

	private Point2D pointToPrime(Point2D origin, Point2D point) {
		return new Point2D.Double(point.getX() - origin.getX(), point.getY() - origin.getY());
	}

	private Point2D primeToPoint(Point2D origin, Point2D point) {
		return new Point2D.Double(point.getX() + origin.getX(), point.getY() + origin.getY());
	}

    public Image getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(Image originalImage) {
        this.originalImage = originalImage;
    }

}
