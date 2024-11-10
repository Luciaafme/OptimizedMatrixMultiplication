package org.example;

public class MatrixMultiplicationLoopUnrolling {

    public double[][] execute(double[][] a, double[][] b) {
        int n = a.length;
        double[][] c = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double sum = 0;
                int k = 0;

                int limit = n - 4;
                for (; k <= limit; k += 4) {
                    sum += a[i][k] * b[k][j]
                            + a[i][k + 1] * b[k + 1][j]
                            + a[i][k + 2] * b[k + 2][j]
                            + a[i][k + 3] * b[k + 3][j];
                }

                for (; k < n; k++) {
                    sum += a[i][k] * b[k][j];
                }

                c[i][j] = sum;
            }
        }
        return c;
    }
}
