package Tcp.Client;

import Tcp.Message.Reader;
import Tcp.Message.Sender;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT)) {
            System.out.println("Подключено к серверу");

            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor.submit(new Reader(socket, "Клиент"));
            executor.submit(new Sender(socket, "Сервер"));

            Thread.currentThread().join(); // держим клиентский процесс живым

        } catch (IOException | InterruptedException e) {
            System.err.println("Ошибка клиента: " + e.getMessage());
        }
    }
}