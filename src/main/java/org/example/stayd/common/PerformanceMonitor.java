package org.example.stayd.common;

import java.util.function.Supplier;

/**
 * 성능 측정 유틸리티 클래스
 */
public class PerformanceMonitor {

    /**
     * 작업 실행 시간 측정 (반환값 없는 작업)
     */
    public static void measureTime(String operation, Runnable task) {
        long startTime = System.currentTimeMillis();

        try {
            task.run();
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            printPerformanceResult(operation, duration);
        }
    }

    /**
     * 작업 실행 시간 측정 (반환값 있는 작업)
     */
    public static <T> T measureTimeWithResult(String operation, Supplier<T> task) {
        long startTime = System.currentTimeMillis();

        try {
            T result = task.get();
            return result;
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            printPerformanceResult(operation, duration);
        }
    }

    /**
     * 성능 측정 결과 출력
     */
    private static void printPerformanceResult(String operation, long duration) {
        System.out.println("==========================================");
        System.out.println("[PERFORMANCE] " + operation);
        System.out.println("Time: " + duration + "ms");

        if (duration > 2000) {
            System.out.println("Status: VERY SLOW (over 2 seconds)");
        } else if (duration > 1000) {
            System.out.println("Status: SLOW (over 1 second)");
        } else if (duration > 500) {
            System.out.println("Status: NORMAL (over 500ms)");
        } else {
            System.out.println("Status: FAST (under 500ms)");
        }

        System.out.println("==========================================");
    }
}