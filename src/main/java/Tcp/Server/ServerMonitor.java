package Tcp.Server;

import java.lang.management.*;
import java.util.concurrent.*;

public class ServerMonitor {
    private static final com.sun.management.OperatingSystemMXBean osMxBean =
            (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private static final MemoryMXBean memoryMxBean = ManagementFactory.getMemoryMXBean();
    private static final ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();

    public static void startMonitoring() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("\n--- Сервер: Метрики ---");

            double cpuLoad = osMxBean.getCpuLoad() * 100;
            System.out.printf("CPU Load: %.2f%%%n", cpuLoad);

            MemoryUsage heapUsage = memoryMxBean.getHeapMemoryUsage();
            System.out.printf("Heap Memory Used: %d MB / %d MB%n",
                    heapUsage.getUsed() / 1_000_000,
                    heapUsage.getMax() / 1_000_000);

            System.out.println("Активные потоки: " + threadMxBean.getThreadCount());

            long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            System.out.printf("Использованная память: %d MB%n", usedMemory / 1_000_000);

        }, 0, 5, TimeUnit.SECONDS);
    }
}