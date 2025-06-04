package RMI.Server;

import RMI.Client.ClientBack;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerServiceImpl extends UnicastRemoteObject implements ServerService {
    private final Map<String, ClientBack> clients = new ConcurrentHashMap<>();
    private int clientIdCounter = 1;

    public ServerServiceImpl() throws RemoteException {
        super();
        startBroadcasting();
    }

    @Override
    public String registerClient(ClientBack back) throws RemoteException {
        String id = "Клиент #" + (clientIdCounter++);
        clients.put(id, back);
        System.out.println("[Сервер] Клиент зарегистрирован: " + id);
        return id;
    }

    @Override
    public void sendMessageToServer(int number, String clientId) throws RemoteException {
        if (clients.containsKey(clientId)) {
            System.out.printf("[Сервер] Получено от %s: %d%n", clientId, number);
        } else {
            System.out.printf("[Сервер] Получено от Неизвестного клиента: %d%n", number);
        }
    }

    // Сервер сам рассылает сообщения всем клиентам раз в секунду
    private void startBroadcasting() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Random random = new Random();

        scheduler.scheduleAtFixedRate(() -> {
            for (Map.Entry<String, ClientBack> entry : clients.entrySet()) {
                String clientId = entry.getKey();
                ClientBack client = entry.getValue();

                try {
                    long timestamp = System.nanoTime();
                    int replyNumber = random.nextInt(1000);
                    client.receiveFromServer(replyNumber, timestamp);
                    System.out.printf("[Сервер -> %s] Отправлено: %d%n", clientId, replyNumber);
                } catch (RemoteException e) {
                    System.err.println("Ошибка связи с " + clientId + ", удаляем...");
                    clients.remove(clientId);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}