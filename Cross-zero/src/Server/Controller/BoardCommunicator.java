package Server.Controller;

import Server.Model.Board;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class BoardCommunicator {

    protected Socket FirstClientSocket ;
    protected Socket SecondClientSocket ;
    protected DataInputStream FirstIn;
    protected DataInputStream SecondIn;
    protected DataOutputStream FirstOut;
    protected DataOutputStream SecondOut;
    protected Board board;
    protected final char symbol1 = 'X';
    protected final char symbol2 = 'O';
    protected String inMessage;
    protected ConcurrentHashMap<String, String> usersPairs ;
    protected ConcurrentHashMap <String, Socket> usersSockets ;
    protected CopyOnWriteArrayList<String> moves = new CopyOnWriteArrayList<>();
    protected String nickname;
    protected boolean isOver = false;

    public BoardCommunicator(ConcurrentHashMap<String, String> pairs, ConcurrentHashMap<String, Socket> users, String name) throws IOException {
        usersPairs = pairs;
        usersSockets = users;
        nickname = name;
        FirstClientSocket = usersSockets.get(nickname);

        FirstIn = new DataInputStream(FirstClientSocket.getInputStream());
        FirstOut = new DataOutputStream(FirstClientSocket.getOutputStream());

    }

    protected void connectSecondUser() throws IOException {

        while(true){
            if(!(usersPairs.get(nickname).equals("null"))) {
                SecondClientSocket = usersSockets.get(usersPairs.get(nickname));
                break;
            }
        }

        SecondIn = new DataInputStream(SecondClientSocket.getInputStream());
        SecondOut = new DataOutputStream(SecondClientSocket.getOutputStream());

        board = new Board();


        FirstOut.writeUTF("@symbol " + symbol1);
        SecondOut.writeUTF("@symbol " + symbol2);

        FirstOut.writeUTF("@start");
        SecondOut.writeUTF("@start");
    }

    protected void move(String name) throws IOException {

        try {
            boolean res;

            while (true) {
                if (name.equals("first"))
                    inMessage = FirstIn.readUTF();
                else
                    inMessage = SecondIn.readUTF();


                if (!(inMessage.startsWith("@move") || inMessage.equals("@isWin"))) {
                    if (name.equals("first"))
                        FirstOut.writeUTF("@WrongCommand");
                    else
                        SecondOut.writeUTF("@WrongCommand");
                }

                if (inMessage.startsWith("@move")) {
                    if (inMessage.length() != 9) {
                        if (name.equals("first"))
                            FirstOut.writeUTF("@WrongCommand");

                        else
                            SecondOut.writeUTF("@WrongCommand");

                        continue;
                    }

                    String tmp = inMessage.substring(6);
                    String[] coordinates = tmp.split(" ");
                    int x = Integer.parseInt(coordinates[0]);
                    int y = Integer.parseInt(coordinates[1]);
                    if (name.equals("first"))
                        res = board.move(x, y, symbol1);
                    else
                        res = board.move(x, y, symbol2);

                    if (res) {
                        moves.add(tmp);

                        if (name.equals("first")) {
                            FirstOut.writeUTF("@MoveSuccess");
                            sendIsWin("first");
                            //sendIsWin("second");
                        } else {
                            SecondOut.writeUTF("@MoveSuccess");
                            sendIsWin("second");
                            // sendIsWin("first");

                        }

                        String s = moves.get(moves.size() - 1);

                        if (name.equals("first")) {
                            SecondOut.writeUTF("@OpponentMove " + s);
                            sendIsWin("second");
                        } else {
                            FirstOut.writeUTF("@OpponentMove " + s);
                            sendIsWin("first");
                        }
                        board.printBoard();
                        break;

                    } else {
                        if (name.equals("first"))
                            FirstOut.writeUTF("@MoveFail");
                        else
                            SecondOut.writeUTF("@MoveFail");

                    }

                }
            }
        }
        catch(IOException e){
            usersSockets.remove(usersPairs.get(nickname));
            usersPairs.remove(nickname);
            usersSockets.remove(nickname);
            FirstClientSocket.close();
            SecondClientSocket.close();
        }
    }

    protected void sendIsWin(String name) throws IOException {
        if (name.equals("first"))
            inMessage = FirstIn.readUTF();
        else
            inMessage = SecondIn.readUTF();

        if(inMessage.equals("@isWin")){
            int r = board.isWin();

            if (name.equals("first"))
                FirstOut.writeUTF("@isWin " + String.valueOf(r));

            else
                SecondOut.writeUTF("@isWin " + String.valueOf(r));

            if(r == 1 || r == 2 || r == 0){
                if(name.equals("first")) {
                    usersSockets.remove(nickname);
                    FirstClientSocket.close();
                }
                else {
                    usersSockets.remove(usersPairs.get(nickname));
                    usersPairs.remove(nickname);
                    SecondClientSocket.close();
                }
                isOver = true;
            }
        }
    }

}
