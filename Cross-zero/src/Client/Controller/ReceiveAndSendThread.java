package Client.Controller;

import Client.View.BoardView;

import java.io.IOException;
import java.net.Socket;

public class ReceiveAndSendThread extends ClientToServerCommunicator implements Runnable  {


    public ReceiveAndSendThread(Socket socket,  BoardView view_) throws IOException {
        super(socket, view_);
    }

    public void run(){
        try{
            setNick();
            chooseOpponent();
            getSymbol();
            getGameState();

            int i = 0;
            int j = 0;

          /*  if(symbol == 'X')
                j = 0;
            else
                j = 1;*/

            while(true){

                if(gameEnd) {
                    System.out.println("The end of the game !");
                    break;
                }

                if(isMyTurn) {
                   /* setInput("@move " + i + " " + j);
                    i++;
                    j++;
                    Thread.sleep(500);*/
                    sendMove();
                }
                else if(!isMyTurn)
                    getOpponentMove();

            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


}
