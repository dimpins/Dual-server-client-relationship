package Tcp.Message;

import java.io.*;
import java.net.Socket;

public class Reader implements Runnable {
    private final BufferedReader reader;
    private final String role;

    public Reader(Socket socket, String role) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.role = role;
    }

    @Override
    public void run() {
        System.out.println(role + ": Reader запущен");
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals("MSG")) {
                    long sendTime = Long.parseLong(parts[1]);
                    int number = Integer.parseInt(parts[2]);

                    double delayMs = (System.nanoTime() - sendTime) / 1_000_000.0;
                    System.out.printf("[%s] Получено: %d | Задержка: %.3f мс%n",
                            role, number, delayMs);
                }
            }
        } catch (IOException e) {
            if (!(e instanceof java.net.SocketException && e.getMessage().equals("Socket closed"))) {
                System.err.println(role + ": Ошибка при чтении: " + e.getMessage());
            }
        } finally {
            try {
                reader.close();
            } catch (IOException ignored) {}
        }
    }
}