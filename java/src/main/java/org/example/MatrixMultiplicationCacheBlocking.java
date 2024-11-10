package org.example;



public class MatrixMultiplicationCacheBlocking {

    private static final int BLOCK_SIZE = 64;

    public double[][] execute(double[][] a, double[][] b) {
        int n = a.length;
        double[][] c = new double[n][n];
        double[][] bT = transpose(b, n);

        for (int i = 0; i < n; i += BLOCK_SIZE) {
            for (int j = 0; j < n; j += BLOCK_SIZE) {
                for (int k = 0; k < n; k += BLOCK_SIZE) {

                    for (int ii = i; ii < Math.min(i + BLOCK_SIZE, n); ii++) {
                        for (int jj = j; jj < Math.min(j + BLOCK_SIZE, n); jj++) {
                            double sum = 0;
                            for (int kk = k; kk < Math.min(k + BLOCK_SIZE, n); kk++) {
                                sum += a[ii][kk] * bT[jj][kk];
                            }
                            c[ii][jj] += sum;
                        }
                    }
                }
            }
        }
        return c;
    }

    private double[][] transpose(double[][] matrix, int n) {
        double[][] transposed = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        return transposed;
    }
}
