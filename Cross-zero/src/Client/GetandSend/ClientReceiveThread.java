package Client.GetandSend;
import Client.View.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientReceiveThread extends Thread{
    private Socket ClientSocket;
    private DataInputStream in;
    private String inMessage;
    private char symbol;
    private BoardView view;

    public ClientReceiveThread(Socket socket, BoardView view_) throws IOException {
        ClientSocket = socket;
        view = view_;
        in = new DataInputStream(ClientSocket.getInputStream());
    }

    public void run(){

        try{
            inMessage = in.readUTF();
            if(inMessage.startsWith("@symbol")){
                symbol = inMessage.charAt(8);
                view.setSymbol(symbol);
                System.out.println("Your symbol is " + symbol);
            }

            inMessage = in.readUTF();
            if(inMessage.equals("@start")) {
                System.out.println("The game has been started !");
                view.printBoard();
                System.out.println("Enter your move !");

            }

            synchronized(ClientSocket){
                notify();
            }

            while(true){
                inMessage = in.readUTF();

                if(inMessage.equals("@MoveSuccess")){
                    synchronized (view){
                        notify();
                    }
                    view.setIsMoved(true);
                    synchronized (view){
                        wait();
                    }
                    System.out.println("Wait for opponents move !");

                }

                if(inMessage.equals("@MoveFail")){
                    synchronized (view){
                        notify();
                    }
                    view.setIsMoved(false);
                    System.out.println("Enter another coordinate !");
                }


                    if(inMessage.startsWith("@isWin")){
                    String tmp = inMessage.substring(7);
                    int isWin = Integer.parseInt(tmp);
                }

                if(inMessage.startsWith("@OpponentMove")){
                    String tmp = inMessage.substring(14);
                    String [] coordinates = tmp.split(" ");
                    int x = Integer.parseInt(coordinates[0]);
                    int y = Integer.parseInt(coordinates[1]);
                    view.setBoard(x, y, symbol);
                    view.printBoard();
                    System.out.println("Enter your move !");

                    synchronized(ClientSocket){
                        notify();
                    }

                }
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
