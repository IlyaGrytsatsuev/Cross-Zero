package Server.Controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ClientThread extends ServerToClientStartCommunicator implements Runnable{

    private Socket ClientSocket ;
    private ConcurrentHashMap <String, String> usersPairs ;
    private ConcurrentHashMap <String, Socket> usersSockets ;
    private DataOutputStream out ;
    private DataInputStream in;
    private String nickname;
    private String inMessage;
    private String sendMessage;

    public ClientThread(Socket socket, ConcurrentHashMap<String, Socket> sockets, ConcurrentHashMap<String, String> pairs ) throws IOException {
        super(socket, sockets, pairs);

    }

    public void run(){
        try {
            receiveNick();
            chooseOpponent();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
