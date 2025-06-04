package RMI.Client;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class ClientBackImpl extends UnicastRemoteObject implements ClientBack {

    protected ClientBackImpl() throws RemoteException {
        super();
    }

    @Override
    public void receiveFromServer(int number, long sendTime) throws RemoteException {
        double delayMs = (System.nanoTime() - sendTime) / 1_000_000.0;
        System.out.printf("[Клиент <- Сервер] Получено: %d | Задержка: %.3f мс%n", number, delayMs);
    }
}