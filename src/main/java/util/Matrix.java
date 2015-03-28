package util;

public class Matrix {

	private int rows;
	private int cols;

	private double[] elements;

	public Matrix(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;

		elements = new double[rows * cols];
	}

	public Matrix(int rows, int cols, double[] elements) {
		this(rows, cols);

		if(rows * cols == elements.length) {
			for(int i = 0; i < rows * cols; i++) {
				this.elements[i] = elements[i];
			}
		}
	}

	public Matrix(Matrix matrix) {
		this.rows = matrix.rows;
		this.cols = matrix.cols;

		for(int i = 0; i < matrix.elements.length; i++) {
			this.elements[i] = matrix.elements[i];
		}
	}

	public Matrix add(Matrix matrixB) throws MatrixSizeException {
		if(rows != matrixB.getRows() || cols != matrixB.getCols()) {
			throw new MatrixSizeException(MatrixSizeException.ADD_SUBTRACT_MESSAGE);
		}

		Matrix addMatrix = new Matrix(rows, cols);
		for(int i = 0; i < rows * cols; i++) {
			addMatrix.getElements()[i] = elements[i] + matrixB.getElements()[i];
		}

		return addMatrix;
	}

	public Matrix subtract(Matrix matrixB) throws MatrixSizeException {
		if(rows != matrixB.getRows() || cols != matrixB.getCols()) {
			throw new MatrixSizeException(MatrixSizeException.ADD_SUBTRACT_MESSAGE);
		}

		Matrix diffMatrix = new Matrix(rows, cols);
		for(int i = 0; i < rows * cols; i++) {
			diffMatrix.getElements()[i] = elements[i] - matrixB.getElements()[i];
		}

		return diffMatrix;
	}

	public Matrix multiply(Matrix matrixB) throws MatrixSizeException {
		if(cols != matrixB.getRows()) {
			throw new MatrixSizeException(MatrixSizeException.MULTIPLY_MESSAGE);
		}

		Matrix productMatrix = new Matrix(rows, matrixB.getCols());
		int elementNumb = 0;
		for(int row = 0; row < rows; row++) {
			for(int col = 0; col < matrixB.getCols(); col++) {
                double[] rowArray = getRow(row + 1);
                double[] colArray = matrixB.getCol(col + 1);
				float sum = 0;

				for(int i = 0; i < rowArray.length; i++) {
					sum += rowArray[i] * colArray[i];
				}

				productMatrix.getElements()[elementNumb] = sum;
				elementNumb++;
			}
		}

		return productMatrix;
	}

	public Matrix solveSystem() {
		for(int currRowIndex = 1; currRowIndex <= rows; currRowIndex++) {
			//divide entire row by pivot value
			double[] currRow = getRow(currRowIndex);
			double pivot = currRow[currRowIndex - 1];

			for(int rowElement = 0; rowElement < currRow.length; rowElement++) {
				currRow[currRowIndex] /= pivot;
			}

			//make zeros under pivot
			double multiple = 0;
			for(int otherRowIndex = currRowIndex + 1; otherRowIndex <= rows; otherRowIndex++) {
				double[] otherRow = getRow(otherRowIndex);
				for(int rowElement = 0; rowElement < currRow.length; rowElement++) {
					if(rowElement == 0) {
						multiple = (-otherRow[rowElement] / currRow[rowElement]);
					} else {
						otherRow[rowElement] += currRow[rowElement] * multiple;
					}
				}
			}
		}

		return new Matrix(rows, 1, this.getCol(cols));
	}

	public int getCols() {
		return cols;
	}

	public int getRows() {
		return rows;
	}

	public double[] getElements() {
		return elements;
	}

	private double getElement(int row, int col) {
		return elements[(row - 1) * cols + (col - 1)];
	}

	private double[] getRow(int row) {
        double[] rowArray = new double[cols];

		for(int i = 0; i < cols; i++) {
			rowArray[i] = elements[(row - 1) * cols + i];
		}

		return rowArray;
	}

	private double[] getCol(int col) {
        double[] colArray = new double[rows];

		for(int i = 0; i < rows; i++) {
			colArray[i] = elements[i * cols + (col - 1)];
		}

		return colArray;
	}

	@Override
	public String toString() {
		String string = "";

		string += "( ";
		for(int i = 0; i < rows * cols; i++) {
			string += elements[i];
			if(i != (rows * cols - 1)) {
				string += ", ";
			}
		}
		string += ")";

		return string;
	}
}
