package org.example;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.example.CSCMatrix.convertToCSC;
import static org.example.CSRMatrix.convertToCSR;

@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class MatrixMultiplicationBenchmarking {

    @State(Scope.Thread)
    public static class Operands {

        @Param({"10","100", "300","500", "1000"})
        private int n;

        @Param({"0.0","0.2", "0.5", "0.8"})
        private double zeroPercentage;

        private double[][] a;
        private double[][] b;
        private long initialMemory;

        @Setup(Level.Trial)
        public void setup() {
            a = initializeMatrix(n, zeroPercentage);
            b = initializeMatrix(n, zeroPercentage);


            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            initialMemory = runtime.totalMemory() - runtime.freeMemory();
        }

        @TearDown(Level.Trial)
        public void tearDown() {
            Runtime runtime = Runtime.getRuntime();
            long finalMemory = runtime.totalMemory() - runtime.freeMemory();
            long memoryUsed = finalMemory - initialMemory;

            System.out.println("\nMatrix size: " + n + "x" + n + "  Total Memory used: " + memoryUsed / 1024 + " KB");

        }

        private double[][] initializeMatrix(int size, double zeroPercentage) {
            double[][] matrix = new double[size][size];
            Random random = new Random();
            int totalElements = size * size;
            int zeroCount = (int) (totalElements * zeroPercentage);

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = 0.1 + (9.8 * random.nextDouble()); // Números entre 0.1 y 9.9
                }
            }

            int placedZeros = 0;
            while (placedZeros < zeroCount) {
                int randomRow = random.nextInt(size);
                int randomCol = random.nextInt(size);

                if (matrix[randomRow][randomCol] != 0.0) {
                    matrix[randomRow][randomCol] = 0.0;
                    placedZeros++;
                }
            }

            return matrix;
        }

    }

    @Benchmark
    public void multiplication(Operands operands) {
        new MatrixMultiplication().execute(operands.a, operands.b);
    }

    @Benchmark
    public void loopUnrollingMultiplication(Operands operands) {
        new MatrixMultiplicationLoopUnrolling().execute(operands.a, operands.b);
    }

    @Benchmark
   public void cacheBlockingMultiplication(Operands operands) {
      new MatrixMultiplicationCacheBlocking().execute(operands.a, operands.b);
    }

    @Benchmark
    public void sparseCSR_CSRMultiplication(SparceMatrixBenchmarking.Operands operands) {
        CSRMatrix cscA = convertToCSR(operands.a);
        CSRMatrix cscB = convertToCSR(operands.b);
        cscA.multiply(cscB);
    }

    @Benchmark
    public void sparseCSC_CSCMultiplication(SparceMatrixBenchmarking.Operands operands) {
        CSCMatrix cscA = convertToCSC(operands.a);
        CSCMatrix cscB = convertToCSC(operands.b);
        cscA.multiply(cscB);
    }
}
