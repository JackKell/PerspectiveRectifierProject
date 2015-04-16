package com.github.jackkell.perspectiverectifier;

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

	private final boolean mirror;
	private final boolean changePerspective;

	private final double rotation;
	private final double vpX;
	private final double shear;
	private final double verticalShift;
	private final double horizontalShift;
	private final double scale;

	private final WritableImage rectifiedImage;

	private RectifiedImage(RectifiedImage.Builder builder) {
		this.originalImage = builder.originalImage;

		this.mirror = builder.mirror;
		this.changePerspective = builder.changePerspective;

		this.rotation = builder.rotation;
		this.vpX = builder.vpX;
		this.shear = builder.shear;
		this.verticalShift = builder.verticalShift;
		this.horizontalShift = builder.horizontalShift;
		this.scale = builder.scale;

		rectifiedImage = createImage();
	}

	public WritableImage getImage() {
		return rectifiedImage;
	}

	private WritableImage createImage() {
		// Copy the original image
		WritableImage newImage = copy(originalImage);

		newImage = rotate(newImage);

		if(mirror)
			newImage = mirror(newImage);

		if(changePerspective)
			newImage = changePerspective(newImage);

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

		//Point2D vanishingPoint = new Point2D.Double(vectorX[0], vectorX[1]);
		Point2D vanishingPoint = new Point2D.Double(vpX, 0);
		System.out.println("VP: " + vanishingPoint.getX() + ", " + vanishingPoint.getY());

		// Now it's time to transform the image
		double parameter = (1/vanishingPoint.getX());
		Matrix transformation = new Matrix(4, 4, new double[] {
				1, shear, 0, parameter,
				0, 1, 0, 0,
				0, 0, 0, 0,
				horizontalShift, verticalShift, 0, 1
		});

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

				double x1 = (newCoords[0] / (newCoords[3] + scale));
				double y1 = (newCoords[1] / (newCoords[3] + scale));

				if(x1 < 0 || y1 < 0 || x1 >= width || y1 >= height)
					continue;

				writer.setColor((int ) x1, (int) y1, trueColor);
			}
		}

		return newImage;
	}

	public static class Builder {
		private final Image originalImage;

		private boolean mirror;
		private boolean changePerspective;

		private double rotation;
		private double vpX;
		private double shear;
		private double verticalShift;
		private double horizontalShift;
		private double scale;

		public Builder(Image image) {
			this.originalImage = image;
			mirror = false;
			changePerspective = false;
		}

		public Builder mirror() {
			this.mirror = true;
			return this;
		}

		public Builder changePerspective() {
			this.changePerspective = true;
			return this;
		}

		public Builder rotation(double rotation) {
			this.rotation = rotation;
			return this;
		}

		public Builder vpX(double vpX) {
			this.vpX = vpX;
			return this;
		}

		public Builder shear(double shear) {
			this.shear = shear;
			return this;
		}

		public Builder verticalShift(double verticalShift) {
			this.verticalShift = verticalShift;
			return this;
		}

		public Builder horizontalShift(double horizontalShift) {
			this.horizontalShift = horizontalShift;
			return this;
		}

		public Builder scale(double scale) {
			this.scale = scale;
			return this;
		}

		public RectifiedImage build() {
			return new RectifiedImage(this);
		}
	}
}
