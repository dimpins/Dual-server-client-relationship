package UPD.Server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.lang.management.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static final int PORT = 8080;
    private static final int STATS_INTERVAL_SEC = 5;

    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(PORT);
        System.out.println("Сервер запущен на порту " + PORT);

        Map<SocketAddress, String> clients = new HashMap<>();
        AtomicInteger totalMessages = new AtomicInteger();

        startMonitoring(totalMessages);

        new Thread(() -> {
            byte[] buffer = new byte[1024];
            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String received = new String(packet.getData(), 0, packet.getLength());
                    SocketAddress clientAddress = packet.getSocketAddress();

                    String[] parts = received.split(":");
                    if (parts.length >= 2) {
                        long sendTime = Long.parseLong(parts[0]);
                        int number = Integer.parseInt(parts[1]);

                        String clientId = getClientId(clientAddress, clients);
                        double delayMs = (System.nanoTime() - sendTime) / 1_000_000.0;
                        System.out.printf("[Получено от %s] %d | Задержка: %.3f мс%n", clientId, number, delayMs);
                        totalMessages.incrementAndGet();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            clients.forEach((address, clientId) -> {
                try {
                    int number = new Random().nextInt(1000);
                    String message = System.nanoTime() + ":" + number;
                    DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), address);
                    socket.send(packet);
                    System.out.printf("[Сервер -> %s] Отправлено: %d%n", clientId, number);
                    totalMessages.incrementAndGet();
                } catch (IOException e) {
                    System.err.println("Ошибка отправки клиенту " + clientId + ": " + e.getMessage());
                }
            });
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    private static String getClientId(SocketAddress address, Map<SocketAddress, String> clients) {
        return clients.computeIfAbsent(address, k -> "Клиент #" + (clients.size() + 1));
    }

    private static void startMonitoring(AtomicInteger totalMessages) {
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

            int count = totalMessages.getAndSet(0);
            System.out.printf("Сообщений за %d сек: %d%n", STATS_INTERVAL_SEC, count);

        }, 0, STATS_INTERVAL_SEC, TimeUnit.SECONDS);
    }
}