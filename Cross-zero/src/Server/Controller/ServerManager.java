package Server.Controller;
import Server.Model.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class ServerManager {
    private ServerSocket serverSocket;
    private Socket PlayerSocket;
    private Socket FirstPlayerSocket;
    private Socket SecondPlayerSocket;
    private Board board;
    private CopyOnWriteArrayList<String> users = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<String> moves = new CopyOnWriteArrayList<>();

    private ConcurrentHashMap <String, String> usersPairs = new ConcurrentHashMap<>();

    private ConcurrentHashMap <String, Socket> usersSockets = new ConcurrentHashMap<>();

    public ServerManager() throws IOException {
        serverSocket = new ServerSocket(2055);

    }

    public void start() throws IOException {

        while(true) {
            PlayerSocket = serverSocket.accept();
            Runnable runnable = new ClientThread(PlayerSocket, usersSockets, usersPairs);
            Thread startThread = new Thread(runnable);
            startThread.start();
        }
       /* FirstPlayerSocket = serverSocket.accept();
        SecondPlayerSocket = serverSocket.accept();
        board = new Board();
        BoardThread firstClientThread = new BoardThread(FirstPlayerSocket, SecondPlayerSocket, board, moves, users, "first");
        BoardThread secondClientThread = new BoardThread(SecondPlayerSocket, FirstPlayerSocket, board, moves, users, "second");
        firstClientThread.start();
        secondClientThread.start();*/

    }
}
