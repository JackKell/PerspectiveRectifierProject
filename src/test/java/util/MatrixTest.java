package util;

import org.junit.Test;

import java.awt.geom.Point2D;
import java.util.Arrays;

import static org.junit.Assert.*;

public class MatrixTest{
	// Elementary Row Operations
	@Test
	public void multiplyTest() throws MatrixSizeException {
		Matrix a = new Matrix(2, 2, new double[] {1, 0, 0, 1});
		Matrix b = new Matrix(2, 2, new double[] {2, 0, 0, 2});
		Matrix result = a.multiply(b);
		Matrix expected = new Matrix(2, 2, new double[] {2, 0, 0, 2});

		assertTrue(Arrays.equals(result.getElements(), expected.getElements()));
	}

	@Test
	public void swapRowsTest() {
		Matrix a = new Matrix(2, 2, new double[] {1, 0, 0 ,1});
		Matrix expected = new Matrix(2, 2, new double[] {0, 1, 1, 0});
		a.swapRows(0, 1);

		assertTrue(Arrays.equals(a.getElements(), expected.getElements()));
	}

	@Test
	public void addRowsTest() {
		Matrix a = new Matrix(3, 3, new double[] {1, 1, 1, 2, 2, 2, 3, 3, 3});
		Matrix expected = new Matrix(3, 3, new double[] {1, 1, 1, 5, 5, 5, 3, 3, 3});

		a.addRows(1, 1, 3, 0);

		assertTrue(Arrays.equals(a.getElements(), expected.getElements()));
	}


	@Test
	public void LUFactorizeTest() {
		Matrix a = new Matrix(3, 3, new double[] {3, -7, -2, -3, 5, 1, 6, -4, 0});
		double[] vectorB = new double[] {-7, 5, 2};
		double[] expected = new double[] {3, 4, -6};
		double[] vectorX = new double[0];

		try {
			vectorX = Matrix.LUFactorize(a, vectorB);
		} catch(MatrixSizeException e) {
			e.printStackTrace();
			System.out.println("This shouldn't happen");
		}

		assertTrue(Arrays.equals(vectorX, expected));
	}

	@Test
	public void solveSystemTest() throws MatrixSizeException {
		Matrix a = new Matrix(2, 2, new double[] {2, 4, 3, 12});
		double[] b = new double[] {16, 30};
		double[] c = Matrix.solveSystem(a, b);

		double[] expected = new double[] {6, 1};

		assertTrue(Arrays.equals(c, expected));
	}

	@Test
	public void interceptTest() throws MatrixSizeException {
		Point2D point1 = new Point2D.Double(0, 0);
		Point2D point2 = new Point2D.Double(2, 2);
		Point2D point3 = new Point2D.Double(0, 2);
		Point2D point4 = new Point2D.Double(2, 0);

		double slope1 = (point2.getY() - point1.getY()) / (point2.getX() - point1.getX());
		double slope2 = (point4.getY() - point3.getY()) / (point4.getX() - point3.getX());

		double yInt1 = point1.getY() + (slope1 * point1.getX() * -1);
		double yInt2 = point4.getY() + (slope2 * point4.getX() * -1);

		Matrix system = new Matrix(2, 2, new double[] {-slope1, 1, -slope2, 1});
		double[] vectorB = new double[] {yInt1, yInt2};
		double[] vectorX = Matrix.solveSystem(system, vectorB);

		double[] expected = new double[] {1, 1};

		assertTrue(Arrays.equals(expected, vectorX));
	}
}