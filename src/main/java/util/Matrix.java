package util;

public class Matrix {

	private int rows;
	private int cols;
	private double[] elements;

	// Constructors
	public Matrix(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;

		elements = new double[rows * cols];

		for(int i = 0; i < elements.length; i++) {
			elements[i] = 0;
		}
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
		elements = new double[rows * cols];

		for(int i = 0; i < matrix.elements.length; i++) {
			this.elements[i] = matrix.elements[i];
		}
	}


	// Public Methods
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
                double[] rowArray = getRow(row);
                double[] colArray = matrixB.getCol(col);
				double sum = 0;

				for(int i = 0; i < rowArray.length; i++) {
					sum += rowArray[i] * colArray[i];
				}

				productMatrix.getElements()[elementNumb] = sum;
				elementNumb++;
			}
		}

		return productMatrix;
	}

	public static double[] LUFactorize(Matrix matrixA, double[] vectorB) throws MatrixSizeException {
		if(matrixA.rows != matrixA.cols) {
			throw new MatrixSizeException("Matrix A must be an nxn matrix");
		}

		// Creates matrix L and makes each element on the diagonal a 1
		Matrix matrixL = new Matrix(matrixA.rows, matrixA.cols);
		for(int i = 0; i < matrixL.elements.length; i++) {
			matrixL.elements[i] = i % matrixL.cols == i / matrixL.cols ? 1 : 0;
		}

		// Creates matrix U, which is initially the same as matrix A
		Matrix matrixU = new Matrix(matrixA);

		// Creates matrices L and U
		for(int pivot = 0; pivot < matrixA.rows; pivot++) {
			for(int i = 1 + pivot; i < matrixA.rows; i++) {
				matrixL.setElement(i, pivot, matrixU.getElement(i, pivot) / matrixU.getElement(pivot, pivot));
				matrixU.addRows(i, i, -matrixL.getElement(i, pivot), pivot);
			}
		}

		// Solves Ly = b for y
		Matrix newMatrixL = matrixL.addColumn(vectorB);
		for(int pivot = 0; pivot < newMatrixL.rows; pivot++) {
			for(int i = 1 + pivot; i < newMatrixL.rows; i++) {
				double modifier = newMatrixL.getElement(i, pivot) / newMatrixL.getElement(pivot, pivot);
				newMatrixL.addRows(i, i, -modifier, pivot);
			}
		}
		double[] vectorY = newMatrixL.getCol(newMatrixL.cols - 1);

		// Solves Ux = b for x
		Matrix newMatrixU = matrixU.addColumn(vectorY);

		for(int pivot = newMatrixU.rows - 1; pivot >= 0; pivot--) {
			for(int i = pivot - 1; i >= 0; i--) {
				double modifier = newMatrixU.getElement(i, pivot) / newMatrixU.getElement(pivot, pivot);
				newMatrixU.addRows(i, i, -modifier, pivot);
			}
		}

		for(int pivot = 0; pivot < newMatrixU.rows; pivot++) {
			newMatrixU.mutiplyRow(pivot, 1 / newMatrixU.getElement(pivot, pivot));
		}
		double[] vectorX = newMatrixU.getCol(newMatrixL.cols - 1);

		return vectorX;
	}

	public double[] LUFactorize(double[] vectorB) throws MatrixSizeException {
		return Matrix.LUFactorize(this, vectorB);
	}

	public static double[] solveSystem(Matrix matrixA, double[] vectorB) throws MatrixSizeException {
		if(vectorB.length != matrixA.rows) {
			throw new MatrixSizeException("Vector x contains " + vectorB.length + " elements, and matrix A has " + matrixA.rows + " rows. An augmented matrix cannot be made.");
		}

		Matrix augmentedA = matrixA.addColumn(vectorB);

		int shortestSide = augmentedA.rows <= augmentedA.cols ? augmentedA.rows : augmentedA.cols;


		for(int pivot = 0; pivot < shortestSide; pivot++) {
			for(int i = 1 + pivot; i < shortestSide; i++) {
				double modifier = augmentedA.getElement(i, pivot) / augmentedA.getElement(pivot, pivot);
				augmentedA.addRows(i, i, -modifier, pivot);
			}
		}

		for(int pivot = shortestSide - 1; pivot >= 0; pivot--) {
			for(int i = pivot - 1; i >= 0; i--) {
				double modifier = augmentedA.getElement(i, pivot) / augmentedA.getElement(pivot, pivot);
				augmentedA.addRows(i, i, -modifier, pivot);
			}
		}

		for(int pivot = 0; pivot < augmentedA.rows; pivot++) {
			augmentedA.mutiplyRow(pivot, 1 / augmentedA.getElement(pivot, pivot));
		}

		return augmentedA.getCol(augmentedA.cols - 1);
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

	private Matrix addColumn(double[] vector) {
		double[] elements = new double[this.getElements().length + this.rows];
		int newRow = this.cols + 1;
		for(int i = 1; i <= elements.length; i++) {
			int offset = i / newRow; // This represents the number of elements behind matrix L is compared to the new one.
			if(i % newRow == 0 ) {
				elements[i - 1] = vector[(i / newRow) - 1];
			} else {
				elements[i - 1] = this.getElements()[i - 1 - offset];
			}
		}

		return new Matrix(this.rows, this.cols + 1, elements);
	}


	// Elementary Row Operations
	public void mutiplyRow(int rowIndex, double multiplier) {
		double[] row = getRow(rowIndex);

		for(int i = 0; i < cols; i++) {
			row[i] *= multiplier;
		}

		setRow(rowIndex, row);
	}

	public void swapRows(int row1Index, int row2Index) {
		double[] firstRow = getRow(row1Index);
		double[] secondRow = getRow(row2Index);
		setRow(row1Index, secondRow);
		setRow(row2Index, firstRow);
	}

	public void addRows(int resultRow, int firstRowIndex, double multiplier, int secondRowIndex) {
		double[] newRow = getRow(resultRow);
		double[] firstRow = getRow(firstRowIndex);
		double[] secondRow = getRow(secondRowIndex);

		for(int i = 0; i < cols; i++) {
			secondRow[i] *= multiplier;
		}

		for(int i = 0; i < cols; i++) {
			newRow[i] = firstRow[i] + secondRow[i];
		}

		setRow(resultRow, newRow);
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


	//Private Methods
	private double getElement(int row, int col) {
		return elements[row * cols + col];
	}

	private void setElement(int row, int col, double value) {
		elements[row * cols + col] = value;
	}

	private double[] getRow(int row) {
        double[] rowArray = new double[cols];

		for(int i = 0; i < cols; i++) {
			rowArray[i] = elements[row * cols + i];
		}

		return rowArray;
	}

	private double[] getCol(int col) {
		double[] colArray = new double[rows];

		for(int i = 0; i < rows; i++) {
			colArray[i] = elements[i * cols + col];
		}

		return colArray;
	}

	private void setRow(int row, double[] value) {
		for(int i = row * cols; i < (row * cols) + cols; i++) {
			elements[i] = value[i - row * cols];
		}
	}
}
