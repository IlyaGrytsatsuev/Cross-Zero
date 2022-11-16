package Server.Controller;
import Server.Model.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientThread extends Thread{
    private Socket YourClientSocket ;
    private Socket OpponentClientSocket ;
    private DataInputStream in;
    private DataOutputStream YourOut;
    private DataOutputStream OpponentOut;
    private Board board;
    private char symbol;
    private String inMessage;
    private String outMessage;
    private CopyOnWriteArrayList<String> moves;
    private CopyOnWriteArrayList<String> users;
    private String name;

    public ClientThread(Socket socket1, Socket socket2, Board board_, CopyOnWriteArrayList<String> moves_, CopyOnWriteArrayList<String> users_, String name_) throws IOException {
        YourClientSocket = socket1;
        OpponentClientSocket = socket2;
        board = board_;
        moves = moves_;
        users = users_;
        name = name_;
        in = new DataInputStream(YourClientSocket.getInputStream());
        YourOut = new DataOutputStream(YourClientSocket.getOutputStream());
        OpponentOut = new DataOutputStream(OpponentClientSocket.getOutputStream());

    }

    public void run(){
        try{
            if(name.equals("first"))
                symbol = 'X';
            else
                symbol = 'O';

            users.add(name);
            YourOut.writeUTF("@symbol " + symbol);

            while(true){
                if(users.size() == 2)
                    break;
            }
            YourOut.writeUTF("@start");


            while(true){
               inMessage = in.readUTF();

                if(!(inMessage.startsWith("@move") || inMessage.equals("@isWin"))){
                    YourOut.writeUTF("@WrongCommand");
                }


                if(inMessage.startsWith("@move")){
                    if(inMessage.length() != 9){
                        YourOut.writeUTF("@WrongCommand");
                        continue;
                    }
                    String tmp = inMessage.substring(6);
                    String [] coordinates = tmp.split(" ");
                    int x = Integer.parseInt(coordinates[0]);
                    int y = Integer.parseInt(coordinates[1]);
                    boolean res = board.move(x, y, symbol);
                    if(res) {
                       moves.add(tmp);
                       YourOut.writeUTF("@MoveSuccess");
                       String s = moves.get(moves.size()-1);
                       OpponentOut.writeUTF("@OpponentMove " + s);
                       board.printBoard();
                    }
                    else
                       YourOut.writeUTF("@MoveFail");

               }

               if(inMessage.equals("@isWin")){
                   int r = board.isWin();
                   YourOut.writeUTF("@isWin " + String.valueOf(r));
                   if(r == 1 || r == 2){
                       YourClientSocket.close();
                       break;
                   }
               }

            }

        }
        catch(Exception e){
            e.printStackTrace();
        }

    }


}
