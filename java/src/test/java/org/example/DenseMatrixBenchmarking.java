package org.example;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;


@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class DenseMatrixBenchmarking {

    @State(Scope.Thread)
    public static class Operands {

        @Param({"10","100", "300","500", "1000", "1024", "2000"})
        private int n;

        private double[][] a;
        private double[][] b;
        private long initialMemory;

        @Setup(Level.Trial)
        public void setup() {
            a = initializeMatrix(n);
            b = initializeMatrix(n);

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

        public double[][] initializeMatrix(int size) {
            double[][] matrix = new double[size][size];
            Random random = new Random();

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = 0.1 + (9.8 * random.nextDouble());
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
}
