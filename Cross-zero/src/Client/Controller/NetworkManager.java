package Client.Controller;

import Client.View.BoardView;

import java.io.IOException;
import java.net.Socket;

public class NetworkManager {

    private Socket ClientSocket;
    private BoardView view;


    public NetworkManager() throws IOException {
        ClientSocket = new Socket("localhost", 2055);
        view = new BoardView();
    }

    public void start() throws IOException {
        Runnable runnable = new ReceiveAndSendThread(ClientSocket, view);
        Thread thread = new Thread(runnable);
        thread.start();
    }

}
