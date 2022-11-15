package Client.GetandSend;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import Client.View.*;

public class NetworkManager {

    private Socket ClientSocket;
    private BoardView view;


    public NetworkManager() throws IOException {
        ClientSocket = new Socket("localhost", 2055);
        view = new BoardView();
    }

    public void start() throws IOException {
       // ClientSendThread sendThread = new ClientSendThread(ClientSocket, view);
        //ClientReceiveThread receiveThread = new ClientReceiveThread(ClientSocket, view);
       // sendThread.start();
       // receiveThread.start();

        ReceiveAndSendThread thread = new ReceiveAndSendThread(ClientSocket, view);
        thread.start();
    }
}
