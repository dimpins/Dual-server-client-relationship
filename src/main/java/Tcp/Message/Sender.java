package Tcp.Message;

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class Sender implements Runnable {
    private final PrintWriter writer;
    private final String recipient;
    private final Random random = new Random();
    private int messageCount = 0;

    public Sender(Socket socket, String recipient) throws IOException {
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        this.recipient = recipient;
    }

    @Override
    public void run() {
        System.out.println("[" + recipient + "] Sender запущен");
        try {
            while (!Thread.interrupted()) {
                int number = random.nextInt(1000);
                long timestamp = System.nanoTime();

                writer.println("MSG:" + timestamp + ":" + number);
                System.out.printf("[Сервер -> %s] Отправлено: %d (№%d)%n", recipient, number, ++messageCount);

                Thread.sleep(500);
            }
        } catch (InterruptedException  e) {
            if (!(e.getMessage().equals("Socket closed"))) {
                System.err.println("Ошибка при отправке в " + recipient + ": " + e.getMessage());
            }
        } finally {
            writer.close();
        }
    }
}