package util;

public class MatrixSizeException extends Exception {

	public static final String ADD_SUBTRACT_MESSAGE = "Matrices are not the same size.";
	public static final String MULTIPLY_MESSAGE = "Matrix B does not have the same number of rows as Matrix A's columns.";

	public MatrixSizeException(String message) {
		super(message);
	}
}
