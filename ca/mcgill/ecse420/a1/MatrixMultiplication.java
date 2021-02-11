package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Arrays;

public class MatrixMultiplication {
	
	private static final int NUMBER_THREADS = 23;
	private static final int MATRIX_SIZE = 3;

	public static void main(String[] args) {

		// Generate two random matrices, same size
		double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		double[][] b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);

		// Multiply Matrices
		double[][] result_s = sequentialMultiplyMatrix(a, b);
		double[][] result_p = parallelMultiplyMatrix(a, b);
		System.out.println(Arrays.deepToString(result_s));
		System.out.println(Arrays.deepToString(result_p));

		// Get Matrix Multiplication Durations
//		long duration_s = sequentialMultiplyMatrixTimed(a, b);
//		long duration_p = parallelMultiplyMatrixTimed(a, b);
//		System.out.print(duration_s/1000000000.0);
//		System.out.print("\n");
//		System.out.print(duration_p/1000000000.0);
	}


	/**
	 * Returns the duration of a sequential matrix multiplication
	 * The two matrices are randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the duration of method execution
	 * */
	public static long sequentialMultiplyMatrixTimed(double[][] a, double[][] b) {
		long startTime = System.nanoTime();
		sequentialMultiplyMatrix(a, b);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		return duration;
	}


	/**
	 * Returns the duration of a parallel matrix multiplication
	 * The two matrices are randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the duration of method execution
	 * */
	public static long parallelMultiplyMatrixTimed(double[][] a, double[][] b) {
		long startTime = System.nanoTime();
		parallelMultiplyMatrix(a,b);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		return duration;
	}


	/**
	 * Returns the result of a sequential matrix multiplication
	 * The two matrices are randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 * */
	public static double[][] sequentialMultiplyMatrix(double[][] a, double[][] b)
	{
		double[][] result = new double[a[0].length][b.length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < b[0].length; j++) {
				for (int k = 0; k < a[0].length; k++) {
					result[i][j] += a[i][k]*b[k][j];
				}
			}
		}
		return result;
	}


	/**
	 * Returns the result of a concurrent matrix multiplication
	 * The two matrices are randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 * */
	public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) {
		ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);

		double[][] result = new double[a[0].length][b.length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < b[0].length; j++) {
				double[] v_a = a[i];
				executor.execute(new VectorMultiplication(v_a, b, result, i, j));
			}
		}
		executor.shutdown();
		while (!executor.isTerminated()) {}
		return result;
	}


	static class VectorMultiplication implements Runnable {
		private final double[] v_a;
		private final double[][] b;
		private final double[][] matrix;
		private final int i;
		private final int j;
		private double res;

		public VectorMultiplication(
				double[] v_a,
				double[][] b,
				double[][] matrix,
				int i,
				int j
		) {
			this.v_a = v_a;
			this.b = b;
			this.matrix = matrix;
			this.i = i;
			this.j = j;
			this.res = 0.0;
		}

		@Override
		public void run() {
			// Multiply vectors
			for (int k = 0; k < b.length; k++) {
				this.res += this.v_a[k]*b[k][j];
			}
			this.matrix[i][j] = this.res; // Insert the result into the matrix
		}
	}

	/**
	 * Populates a matrix of given size with randomly generated integers between 0-10.
	 * @param numRows number of rows
	 * @param numCols number of cols
	 * @return matrix
	 */
	private static double[][] generateRandomMatrix (int numRows, int numCols) {

		double[][] matrix = new double[numRows][numCols];
        for (int row = 0 ; row < numRows ; row++ ) {
            for (int col = 0 ; col < numCols ; col++ ) {
                matrix[row][col] = (int) (Math.random() * 10.0);
            }
        }
        return matrix;
    }
	
}