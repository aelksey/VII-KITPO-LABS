package test.java.test.perfomance;

import core.CustomList;
import inface.SortStrategyInterface;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sort.MergeSortStrategy;
import sort.QuickSortStrategy;

import java.util.Comparator;

public class SortingPerformanceTest {

    // Тестовые размерности структуры данных (от малых к масштабным нагрузкам)
    private final int[] DATA_SIZES = {100, 1000, 5000, 10000};
    private final Comparator<Integer> integerComparator = Integer::compare;

    @Test
    @DisplayName("Performance and memory profiling for QuickSortStrategy")
    public void profileQuickSort() throws InterruptedException {
        runProfilingLifecycle(new QuickSortStrategy());
    }

    @Test
    @DisplayName("Performance and memory profiling for MergeSortStrategy")
    public void profileMergeSort() throws InterruptedException {
        runProfilingLifecycle(new MergeSortStrategy());
    }

    /**
     * Архитектурный жизненный цикл изолированного замера ресурсов ОС
     */
    private void runProfilingLifecycle(SortStrategyInterface sortingStrategy) throws InterruptedException {
        PerformanceReportLogger.printHeader(sortingStrategy.strategyName());

        for (int size : DATA_SIZES) {
            // Шаг 1: Изоляция памяти. Вызываем сборщик мусора и даем JVM стабилизироваться
            System.gc();
            Runtime.getRuntime();
            Thread.sleep(150); // Короткая пауза для завершения циклов GC

            Runtime runtime = Runtime.getRuntime();
            long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

            // Шаг 2: Генерация исходного случайного набора данных средней плотности
            CustomList<Integer> list = new CustomList<>(4); // Емкость блоков = 4
            for (int i = 0; i < size; i++) {
                list.add((int) (Math.random() * size * 10));
            }

            // Фиксируем потребление памяти строго после наполнения СД вспомогательными объектами и нодами
            long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
            long heapUsedKb = (memoryAfter - memoryBefore) / 1024;
            if (heapUsedKb < 0) heapUsedKb = 0; // Защита от дельты спонтанных внутренних процессов JVM

            // Шаг 3: Точный замер "грязного" времени выполнения алгоритма (Wall-Clock Time)
            long startTime = System.nanoTime();
            sortingStrategy.sort(list, integerComparator);
            long endTime = System.nanoTime();

            long durationMs = (endTime - startTime) / 1_000_000;

            // Шаг 4: Логирование результатов и расчет коэффициентов
            PerformanceReportLogger.printRow(size, durationMs, heapUsedKb);
        }

        // PerformanceReportLogger.printFooter(sortingStrategy.strategyName());
    }
}
