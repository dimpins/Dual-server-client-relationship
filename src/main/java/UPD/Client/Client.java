package UPD.Client;

import java.io.*;
import java.net.*;
import java.util.Random;

public class Client {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        System.out.println("Клиент запущен на порту " + socket.getLocalPort());

        new Thread(() -> {
            byte[] receiveBuffer = new byte[1024];
            while (true) {
                try {
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    socket.receive(receivePacket);

                    String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    String[] parts = response.split(":");
                    if (parts.length >= 2) {
                        long replyTimestamp = Long.parseLong(parts[0]);
                        int replyNumber = Integer.parseInt(parts[1]);

                        double delayMs = (System.nanoTime() - replyTimestamp) / 1_000_000.0;
                        System.out.printf("[Клиент <- Сервер] Получено: %d | Задержка: %.3f мс%n", replyNumber, delayMs);
                    }

                } catch (IOException e) {
                    System.err.println("Ошибка при получении данных: " + e.getMessage());
                }
            }
        }).start();

        new Thread(() -> {
            Random random = new Random();
            try {
                while (true) {
                    int number = random.nextInt(1000);
                    long timestamp = System.nanoTime();
                    String message = timestamp + ":" + number;

                    DatagramPacket packet = new DatagramPacket(
                            message.getBytes(),
                            message.length(),
                            InetAddress.getByName(SERVER_IP),
                            SERVER_PORT
                    );
                    socket.send(packet);
                    System.out.println("[Клиент -> Сервер] Отправлено: " + number);

                    Thread.sleep(500); // интервал между отправками
                }
            } catch (Exception e) {
                System.err.println("Ошибка клиента: " + e.getMessage());
            }
        }).start();
    }
}