package Tcp.Server;

import Tcp.Message.Reader;
import Tcp.Message.Sender;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        ServerMonitor.startMonitoring(); // Запуск мониторинга

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен на порту " + PORT);

            int clientId = 1;
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Клиент #" + clientId + " подключен");

                ExecutorService executor = Executors.newFixedThreadPool(2);
                executor.submit(new Reader(socket, "Сервер -> Клиент #" + clientId));
                executor.submit(new Sender(socket, "Клиент #" + clientId));

                clientId++;
            }
        } catch (IOException e) {
            System.err.println("Ошибка сервера: " + e.getMessage());
        }
    }
}