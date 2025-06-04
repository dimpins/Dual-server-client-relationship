package RMI.Server;

import java.rmi.registry.LocateRegistry;

public class Server {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);

            ServerServiceImpl service = new ServerServiceImpl();
            LocateRegistry.getRegistry(1099).rebind("ChatService", service);

            System.out.println("RMI-сервер запущен...");

            ServerMonitor.startMonitoring();

        } catch (Exception e) {
            System.err.println("Ошибка сервера: " + e.getMessage());
        }
    }
}