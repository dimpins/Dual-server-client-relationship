package RMI.Server;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.*;

public class ServerMonitor {
    public static void startMonitoring() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        MemoryMXBean memoryMxBean = ManagementFactory.getMemoryMXBean();
        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();

        com.sun.management.OperatingSystemMXBean osMxBean =
                (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("\n--- Сервер: Метрики ---");

            double cpuLoad = osMxBean.getCpuLoad() * 100;
            System.out.printf("CPU Load: %.2f%%%n", cpuLoad);

            MemoryUsage heapUsage = memoryMxBean.getHeapMemoryUsage();
            System.out.printf("Heap Memory Used: %d MB / %d MB%n",
                    heapUsage.getUsed() / 1_000_000,
                    heapUsage.getMax() / 1_000_000);

            System.out.println("Активные потоки: " + threadMxBean.getThreadCount());

        }, 0, 5, TimeUnit.SECONDS);
    }
}