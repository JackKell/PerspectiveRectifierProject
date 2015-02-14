package util;

public class Matrix {

	private int rows;
	private int cols;

	private float[] matrix;

	public Matrix(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;

		matrix = new float[rows * cols];
	}

	public Matrix add(Matrix matrix) {
		if(rows != matrix.getRows() || cols != matrix.getCols()) {
			//
		}

		return null;
	}

	public int getCols() {
		return cols;
	}

	public int getRows() {
		return rows;
	}
}
