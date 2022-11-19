package Server.Controller;

import Server.Model.Board;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerToClientStartCommunicator {

    protected Socket ClientSocket;
    protected ConcurrentHashMap<String, String> usersPairs;
    protected ConcurrentHashMap<String, Socket> usersSockets;
    protected DataOutputStream out;
    protected DataInputStream in;
    protected String nickname;
    protected String inMessage;
    protected String sendMessage;
    protected Board board;
    protected BoardThread thread;


    public ServerToClientStartCommunicator(Socket socket, ConcurrentHashMap<String, Socket> sockets, ConcurrentHashMap<String, String> pairs) throws IOException {
        ClientSocket = socket;
        usersSockets = sockets;
        usersPairs = pairs;
        in = new DataInputStream(ClientSocket.getInputStream());
        out = new DataOutputStream(ClientSocket.getOutputStream());

    }


    protected void receiveNick() throws IOException {
        try {
            boolean isFree = true;
            String tmp;
            while (true) {
                inMessage = in.readUTF();
                if (inMessage.startsWith("@nick")) {
                    tmp = inMessage.substring(6);
                    if (!usersSockets.isEmpty()) {
                        if (usersSockets.containsKey(tmp)) {
                            isFree = false;
                        }
                    }
                    if (isFree) {
                        out.writeUTF("@nickSuccess");
                        nickname = tmp;
                        usersSockets.put(nickname, ClientSocket);
                        break;
                    } else if (!isFree) {
                        out.writeUTF("@nickFail");
                        isFree = true;
                    }

                }
            }
        } catch (IOException e) {
            if (nickname != null) {
                if (usersSockets.get(nickname) != null)
                    usersSockets.remove(nickname);
            }
            ClientSocket.close();
        }
    }

    protected void chooseOpponent() throws IOException {
            boolean exists = true;
            String tmp;

            while (true) {
                inMessage = in.readUTF();

                if (inMessage.startsWith("@opponent")) {
                    tmp = inMessage.substring(10);
                    if (usersSockets.containsKey(tmp)) {
                        if (usersPairs.containsKey(tmp) && usersPairs.get(tmp).equals("null") && !(usersPairs.containsValue(tmp) && nickname.equals(tmp))) {
                            usersPairs.put(tmp, nickname);
                            //Socket OpponentSocket = usersSockets.get(tmp);
                            //board = new Board();
                            //thread = new BoardThread(usersPairs, usersSockets, nickname);
                            out.writeUTF("@opponentSuccess");
                            break;
                        } else {
                            out.writeUTF("@opponentFail");
                        }
                    } else {
                        out.writeUTF("@opponentFail");
                    }
                }

                if (inMessage.equals("@newBoard")) {
                    usersPairs.put(nickname, "null");
                    Runnable runnable = new BoardThread(usersPairs, usersSockets, nickname);
                    Thread thread = new Thread(runnable);
                    thread.start();
                    out.writeUTF("@newBoardSuccess");

                    break;
                }
            }
        }

}


