package test.java.test.perfomance;

public class PerformanceReportLogger {

    public static void printHeader(String strategyName) {
        // Принудительно конвертируем русские названия в английские для безопасного вывода в терминал
        String cleanName = sanitizeStrategyName(strategyName);

        System.out.println("\n=========================================================================================");
        System.out.printf("   PERFORMANCE & MEMORY PROFILE REPORT: %s%n", cleanName.toUpperCase());
        System.out.println("=========================================================================================");
        System.out.printf("%-10s | %-18s | %-18s | %-15s | %-15s%n", 
            "Size (N)", "Time (ms)", "Heap Used (Kb)", "K_time (T/NlogN)", "K_mem (Mem/N)");
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    public static void printRow(int size, long durationMs, long heapUsedKb) {
        // Вычисляем экспериментальный коэффициент для времени K = T / (N * log2(N))
        double nLogN = size * (Math.log(size) / Math.log(2));
        double kTime = (durationMs > 0) ? (durationMs / nLogN) * 100000 : 0.0;

        // Вычисляем экспериментальный коэффициент для памяти K = Mem / N
        double kMem = (double) heapUsedKb / size;

        System.out.printf("%-10d | %-18d | %-18d | %-15.5f | %-15.5f%n", 
            size, durationMs, heapUsedKb, kTime, kMem);
    }

    // public static void printFooter(String strategyName) {
    //     String cleanName = sanitizeStrategyName(strategyName);

    //     System.out.println("-----------------------------------------------------------------------------------------");
    //     System.out.printf(" ANALYSIS FOR %s:%n", cleanName.toUpperCase());
    //     System.out.println(" 1. Time Complexity: If K_time remains relatively stable as N grows, it empirically");
    //     System.out.println("    proves the theoretical O(N log N) time dependency for this sorting algorithm.");
    //     System.out.println(" 2. Memory Complexity: If K_mem remains constant, it proves a strict linear O(N)");
    //     System.out.println("    memory dependency due to structural block allocations in CustomList.");
    //     System.out.println("=========================================================================================\n");
    // }

    /**
     * Маппинг русских названий стратегий в английские для предотвращения кракозябр в консоли Windows
     */
    private static String sanitizeStrategyName(String strategyName) {
        if (strategyName == null) return "Unknown Sort";
        if (strategyName.contains("слиянием") || strategyName.toLowerCase().contains("merge")) {
            return "Merge Sort Strategy";
        }
        if (strategyName.contains("Быстрая") || strategyName.toLowerCase().contains("quick")) {
            return "Quick Sort Strategy";
        }
        return strategyName;
    }
}
