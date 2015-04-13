package com.github.jackkell.perspectiverectifier;

import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import util.Matrix;
import util.MatrixSizeException;

import java.awt.geom.Point2D;

public class RectifiedImage {
	private final Image originalImage; // Required
	private final Point2D[] points; // Required
	private final RectifiedImage.Mode mode;
	private final double rotation;

	private final WritableImage rectifiedImage;

	private RectifiedImage(RectifiedImage.Builder builder) {
		this.originalImage = builder.originalImage;
		this.points = builder.points;
		this.mode = builder.mode;
		this.rotation = builder.rotation;

		rectifiedImage = createImage();
	}

	public WritableImage getImage() {
		return rectifiedImage;
	}

	private WritableImage createImage() {
		// Copy the original image
		WritableImage newImage = copy(originalImage);

		// Changes the image based on the mode
		switch(mode) {
			case MIRROR:
				//newImage = mirror(newImage);
				break;
		}

		newImage = rotate(newImage);

		if(points[0] != null) {
			newImage = changePerspective2(newImage);
		}

		return newImage;
	}

	private WritableImage copy(Image originalImage) {
		int width = (int) originalImage.getWidth();
		int height = (int) originalImage.getHeight();
		WritableImage newImage = new WritableImage(width, height);

		PixelReader reader = originalImage.getPixelReader();
		PixelWriter writer = newImage.getPixelWriter();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color trueColor = reader.getColor(x, y);
				writer.setColor(x, y, trueColor);
			}
		}

		return newImage;
	}

	private WritableImage mirror(WritableImage newImage) {
		PixelReader reader = newImage.getPixelReader();
		int width = (int) newImage.getWidth();
		int height = (int) newImage.getHeight();

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

		return newImage;
	}

	private WritableImage rotate(WritableImage image) {
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();

		WritableImage newImage = new WritableImage((int) width, (int) height);

		PixelReader reader = image.getPixelReader();
		PixelWriter writer = newImage.getPixelWriter();

		double angle = rotation % 360;
		double theta = angle * (Math.PI / 180);

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
					transformedMatrix = rotationMatrix
							.multiply(mat);
				} catch (MatrixSizeException e) {
					System.out.println("[ERROR rotate_001]");
					e.printStackTrace();
				}
				double x1 = transformedMatrix.getElements()[0] + centerX;
				double y1 = transformedMatrix.getElements()[1] + centerY;

				if(x1 < 0 || y1 < 0 || x1 > originalImage.getWidth() || y1 > originalImage.getHeight())
					continue;

				writer.setColor((int) x1, (int) y1, pixelColor);
			}
		}

		return newImage;
	}

	private WritableImage changePerspective(WritableImage image) {
		double width = image.getWidth();
		double height = image.getHeight();

		WritableImage newImage = new WritableImage((int) width, (int) height);
		PixelReader reader = image.getPixelReader();
		PixelWriter writer = newImage.getPixelWriter();

		// We want to origin to be centered to make the math easier
		Point2D origin = new Point2D.Double(width / 2.0, height / 2.0);

		//Convert point coordinates so the origin is in the center of the image
		Point2D[] offsetPoints = new Point2D.Double[points.length];
		for(int i = 0; i < points.length; i++) {
			Point2D point = points[i];
			offsetPoints[i] = new Point2D.Double(point.getX() - origin.getX(), point.getY() - origin.getY());
		}

		Matrix matrixA = new Matrix(8, 8, new double[] {
				points[0].getX(), points[0].getY(), 1, 0, 0, 0, 0, 0,
				points[3].getX(), points[3].getY(), 1, 0, 0, 0, 0, 0,
				points[1].getX(), points[1].getY(), 1, 0, 0, 0, -points[1].getX(), -points[1].getY(),
				0, 0, 0, points[0].getX(), points[0].getY(), 1, 0, 0,
				0, 0, 0, points[1].getX(), points[1].getY(), 1, 0, 0,
				0, 0, 0, points[2].getX(), points[2].getY(), 1, -points[2].getX(), -points[2].getY(),
				0, 0, 0, points[3].getX(), points[3].getY(), 1, -points[3].getX(), -points[3].getY(),
				points[2].getX(), points[2].getY(), 1, 0, 0, 0, -points[2].getX(), -points[2].getY()
		});

		double[] vectorB = new double[] {0, 0, 1, 0, 0, 1, 1, 1};
		double[] vectorX = new double[0];

		try {
			vectorX = matrixA.LUFactorize(vectorB);
		} catch(MatrixSizeException e) {
			System.out.println("[ERROR changePerspective_001]");
			e.printStackTrace();
		}

		double[] newVectorX = new double[vectorX.length + 1];
		newVectorX[0] = vectorX[0];
		newVectorX[1] = vectorX[2];
		newVectorX[2] = vectorX[7];
		newVectorX[3] = vectorX[1];
		newVectorX[4] = vectorX[3];
		newVectorX[5] = vectorX[4];
		newVectorX[6] = vectorX[5];
		newVectorX[7] = vectorX[6];
		newVectorX[8] = 1;

		Matrix matrixH = new Matrix(3, 3, newVectorX);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color trueColor = reader.getColor(x, y);
				Matrix pixelCoord = new Matrix(3, 1, new double[] {x, y, 1});
				double[] newCoords = new double[0];
				try {
					newCoords = matrixH.multiply(pixelCoord).getElements();
				} catch(MatrixSizeException e) {
					System.out.println("[ERROR changePerspective_002]");
					e.printStackTrace();
				}

				double x1 = (newCoords[0] / newCoords[2]);
				double y1 = (newCoords[1] / newCoords[2]);

				if(x1 < 0 || y1 < 0 || x1 > originalImage.getWidth() || y1 > originalImage.getHeight())
					continue;

				writer.setColor((int ) x1, (int) y1, trueColor);
			}
		}
		
		return newImage;
	}

	private WritableImage changePerspective2(WritableImage image) {
		double width = image.getWidth();
		double height = image.getHeight();

		WritableImage newImage = new WritableImage((int) width, (int) height);
		PixelReader reader = image.getPixelReader();
		PixelWriter writer = newImage.getPixelWriter();
		
		// We want to origin to be centered to make the math easier
		Point2D origin = new Point2D.Double(width / 2.0, height / 2.0);
		
		//Convert point coordinates so the origin is in the center of the image
		Point2D[] offsetPoints = new Point2D.Double[points.length];
		for(int i = 0; i < points.length; i++) {
			Point2D point = points[i];
			offsetPoints[i] = new Point2D.Double(point.getX() - origin.getX(), point.getY() - origin.getY());
		}
		
		// This helps us get the elements for our matrix
		double topSlope = (offsetPoints[1].getY() - offsetPoints[0].getY()) / (offsetPoints[1].getX() - offsetPoints[0].getX());
		double bottomSlope = (offsetPoints[3].getY() - offsetPoints[2].getY()) / (offsetPoints[3].getX() - offsetPoints[2].getX());
		double topYInt = offsetPoints[0].getY() + (offsetPoints[0].getX() * topSlope * -1);
		double bottomYInt = offsetPoints[2].getY() + (offsetPoints[2].getX() * bottomSlope * -1);

		// Once we solve the matrix, we have the point where the 2 lines intercept
		Matrix matrixA = new Matrix(2, 2, new double[] {topSlope, 1, bottomSlope, 1});
		double[] vectorB = new double[] {topYInt, bottomYInt};
		double[] vectorX = null;
		try {
			vectorX = Matrix.solveSystem(matrixA, vectorB);
		} catch(MatrixSizeException e) {
			System.out.println("[ERROR changePerspective2_001]");
			e.printStackTrace();
		}
		Point2D vanishingPoint = new Point2D.Double(vectorX[0], vectorX[1]);
		System.out.println("VP: " + vanishingPoint.getX() + ", " + vanishingPoint.getY());

		// Now it's time to transform the image
		double parameter = -(1/vanishingPoint.getX());
		Matrix transformation = new Matrix(4, 4, new double[] {1, 0, 0, parameter, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1});

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color trueColor = reader.getColor(x, y);
				Matrix pixelCoord = new Matrix(1, 4, new double[] {x, y, 0, 1});
				double[] newCoords = null;
				try {
					newCoords = pixelCoord.multiply(transformation).getElements();
				} catch(MatrixSizeException e) {
					System.out.println("[ERROR changePerspective2_002]");
					e.printStackTrace();
				}

				double x1 = newCoords[0] / newCoords[3];
				double y1 = newCoords[1] / newCoords[3];

				if(x1 < 0 || y1 < 0 || x1 > width || y1 > height)
					continue;

				writer.setColor((int ) x1, (int) y1, trueColor);
			}
		}

		return newImage;
	}

	public static class Builder {
		private final Image originalImage;
		private Point2D[] points;
		private RectifiedImage.Mode mode;
		private double rotation;

		public Builder(Image image) {
			this.originalImage = image;
		}

		public Builder points(Point2D[] points) {
			this.points = points;
			return this;
		}

		public Builder mode(RectifiedImage.Mode mode) {
			this.mode = mode;
			return this;
		}

		public Builder rotation(double rotation) {
			this.rotation = rotation;
			return this;
		}

		public RectifiedImage build() {
			return new RectifiedImage(this);
		}
	}

	public enum Mode {
		NONE,
		MIRROR,
		RANDOM_COLOR,
		TINTED_GREEN
	}
}
