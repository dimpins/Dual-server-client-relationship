package RMI.Client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientBack extends Remote {
    void receiveFromServer(int number, long sendTime) throws RemoteException;
}