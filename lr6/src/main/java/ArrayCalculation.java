import java.util.Arrays;

public class ArrayCalculation {
    static final int SIZE = 60_000_001;
    static final int THREADS = 4;

    public static void main(String[] args) {
        float[] arr = new float[SIZE];

        for (int i = 0; i < SIZE; i++) {
            arr[i] = 1.0f;
        }

        sequentialCalculation(arr.clone());
        multiThreadedCalculation(arr.clone());
        dynamicThreading(arr.clone(), THREADS);
    }

    public static void sequentialCalculation(float[] arr) {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < SIZE; i++) {
            arr[i] = (float) (arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) *
                    Math.cos(0.4f + i / 2));
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Время выполнения последовательного метода: " + elapsedTime + " мс");
        System.out.println("Первая ячейка: " + arr[0] + ", Последняя ячейка: " + arr[SIZE - 1]);
    }

    public static void multiThreadedCalculation(float[] arr) {
        long startTime = System.currentTimeMillis();
        int half = SIZE / 2;

        float[] a1 = new float[half];
        float[] a2 = new float[SIZE - half];

        System.arraycopy(arr, 0, a1, 0, half);
        System.arraycopy(arr, half, a2, 0, SIZE - half);

        Thread thread1 = new Thread(() -> compute(a1, 0));
        Thread thread2 = new Thread(() -> compute(a2, half));

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.arraycopy(a1, 0, arr, 0, half);
        System.arraycopy(a2, 0, arr, half, SIZE - half);

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Время выполнения многопоточного метода: " + elapsedTime + " мс");
        System.out.println("Первая ячейка: " + arr[0] + ", Последняя ячейка: " + arr[SIZE - 1]);
    }

    private static void compute(float[] arr, int offset) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (float) (arr[i] * Math.sin(0.2f + (i + offset) / 5) * Math.cos(0.2f + (i + offset) / 5) *
                    Math.cos(0.4f + (i + offset) / 2));
        }
    }

    public static void dynamicThreading(float[] arr, int numThreads) {
        long startTime = System.currentTimeMillis();
        int chunkSize = SIZE / numThreads;
        Thread[] threads = new Thread[numThreads];
        float[][] results = new float[numThreads][chunkSize + (SIZE % numThreads)];

        for (int i = 0; i < numThreads; i++) {
            final int threadIndex = i;
            int start = threadIndex * chunkSize;
            int end = (threadIndex == numThreads - 1) ? SIZE : start + chunkSize;

            threads[i] = new Thread(() -> {
                for (int j = start; j < end; j++) {
                    results[threadIndex][j - start] = (float) (arr[j] * Math.sin(0.2f + j / 5) * Math.cos(0.2f + j / 5) *
                            Math.cos(0.4f + j / 2));
                }
//                if (threadIndex == 3)
//                {
//                    System.out.println("Весь массив: " + Arrays.toString(results[3]));
//                }

            });
            threads[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < numThreads; i++) {
            System.arraycopy(results[i], 0, arr, i * chunkSize, results[i].length);
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Время выполнения динамического метода: " + elapsedTime + " мс");
        System.out.println("Первая ячейка: " + arr[0] + ", Последняя ячейка: " + arr[SIZE - 1]);
    }
}
