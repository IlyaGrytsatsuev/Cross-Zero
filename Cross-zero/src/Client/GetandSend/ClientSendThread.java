package Client.GetandSend;
import Client.View.*;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientSendThread extends Thread{
    private Socket ClientSocket;
    private DataOutputStream out;
    private String sendMessage;
    private BoardView view;

    public ClientSendThread(Socket socket, BoardView view_) throws IOException {
        ClientSocket = socket;
        view = view_;
        out = new DataOutputStream(ClientSocket.getOutputStream());
    }

    @Override
    public void run() {
        try{
            while (true){

                synchronized (ClientSocket){
                    wait();
                }

                sendMessage = readFromKeyboard();

                if(sendMessage.equals("@quit")){
                    ClientSocket.close();
                    break;
                }

                out.writeUTF(sendMessage);

                if(sendMessage.startsWith("@move")){
                    synchronized (view){
                        wait();
                    }
                    if(view.getIsMoved()){
                        String tmp = sendMessage.substring(6);
                        String [] coordinates = tmp.split(" ");
                        int x = Integer.parseInt(coordinates[0]);
                        int y = Integer.parseInt(coordinates[1]);
                        view.setBoard(x,y, view.getSymbol());
                        view.printBoard();
                        view.setIsMoved(false);
                        synchronized (view){
                            notify();
                        }
                    }

                }
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public String readFromKeyboard(){
        Scanner sc = new Scanner(System.in);
        String text = null;
        text = sc.nextLine();
        return text;
    }

}
