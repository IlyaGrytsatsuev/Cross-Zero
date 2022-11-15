package Server.Controller;
import Server.Model.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;


public class ServerManager {
    private ServerSocket serverSocket;
    private Socket FirstPlayerSocket;
    private Socket SecondPlayerSocket;
    private Board board;
    private CopyOnWriteArrayList<String> users = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<String> moves = new CopyOnWriteArrayList<>();

    public ServerManager() throws IOException {
        serverSocket = new ServerSocket(2055);

    }

    public void start() throws IOException {
        FirstPlayerSocket = serverSocket.accept();
        SecondPlayerSocket = serverSocket.accept();
        board = new Board();
        ClientThread firstClientThread = new ClientThread(FirstPlayerSocket, SecondPlayerSocket, board, moves, users, "first");
        ClientThread secondClientThread = new ClientThread(SecondPlayerSocket, FirstPlayerSocket, board, moves, users, "second");
        firstClientThread.start();
        secondClientThread.start();

    }
}
