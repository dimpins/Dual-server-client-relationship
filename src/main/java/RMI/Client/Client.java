package RMI.Client;

import RMI.Server.ServerService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            ServerService server = (ServerService) registry.lookup("ChatService");

            ClientBack back = new ClientBackImpl();
            String assignedId = server.registerClient(back); // получаем ID от сервера
            System.out.println("Клиент подключён: " + assignedId);

            while (true) {
                int number = new java.util.Random().nextInt(1000);
                server.sendMessageToServer(number, assignedId);
                System.out.println("[Клиент -> Сервер] Отправлено: " + number);
                Thread.sleep(2000);
            }

        } catch (Exception e) {
            System.err.println("Ошибка клиента: " + e.getMessage());
        }
    }
}