package util;

public class MatrixMain {

	public static void main(String[] args) throws MatrixSizeException {
		Matrix a = new Matrix(2,2, new float[] {0, 1, 1, 0});
		Matrix b = new Matrix(2, 2, new float[] {3, 2, 2, 3});

		Matrix c = a.multiply(b);
		System.out.println(c.toString());
	}
}
