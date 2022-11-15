package Server.Controller;
import Server.Model.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientThread extends Thread{
    private Socket ClientSocket ;
    private DataInputStream in;
    private DataOutputStream out;
    private Board board;
    private char symbol;
    private String inMessage;
    private String outMessage;
    private CopyOnWriteArrayList<String> moves;
    private CopyOnWriteArrayList<String> users;
    private String name;

    public ClientThread(Socket socket, Board board_, CopyOnWriteArrayList<String> moves_, CopyOnWriteArrayList<String> users_, String name_) throws IOException {
        ClientSocket = socket;
        board = board_;
        moves = moves_;
        users = users_;
        name = name_;
        in = new DataInputStream(ClientSocket.getInputStream());
        out = new DataOutputStream(ClientSocket.getOutputStream());

    }

    public void run(){
        try{
            if(name.equals("first"))
                symbol = 'X';
            else
                symbol = 'O';

            users.add(name);
            out.writeUTF("@symbol " + symbol);

            while(true){
                if(users.size() == 2)
                    break;
            }
            out.writeUTF("@start");


            while(true){
               inMessage = in.readUTF();

               if(inMessage.startsWith("@move")){
                   String tmp = inMessage.substring(6);
                   String [] coordinates = tmp.split(" ");
                   int x = Integer.parseInt(coordinates[0]);
                   int y = Integer.parseInt(coordinates[1]);
                   boolean res = board.move(x, y, symbol);
                   if(res) {
                       moves.add(tmp);
                       out.writeUTF("@MoveSuccess");
                   }
                   else
                       out.writeUTF("@MoveFail");

               }

               if(inMessage.equals("@isWin")){
                   int r = board.isWin();
                   out.writeUTF("@isWin " + String.valueOf(r));
                   if(r == 1 || r == 2){
                       ClientSocket.close();
                       break;
                   }
               }

                if(inMessage.equals("@getOpponentMove")){
                    String tmp = moves.get(moves.size()-1);
                    out.writeUTF("@OpponentMove " + tmp);
                }


            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


}
