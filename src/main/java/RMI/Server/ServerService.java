package RMI.Server;

import RMI.Client.ClientBack;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerService extends Remote {
    String registerClient(ClientBack back) throws RemoteException;
    void sendMessageToServer(int number, String clientId) throws RemoteException;
}