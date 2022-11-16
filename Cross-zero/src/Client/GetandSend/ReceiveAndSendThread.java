package Client.GetandSend;

import Client.View.BoardView;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ReceiveAndSendThread extends Thread{

    private Socket ClientSocket;
    private DataInputStream in;
    private String inMessage;
    private char symbol ;
    private char OpponentSymbol;

    private BoardView view;
    private DataOutputStream out;
    private String sendMessage;

    private boolean isMyTurn;

    private boolean gameEnd = false;

    public static final int DRAW = 0;
    public static final int X_WIN = 1;
    public static final int O_WIN = 2;
    public static final int CONTINUE = 3;


    public ReceiveAndSendThread(Socket socket,  BoardView view_) throws IOException {
        ClientSocket = socket;
        view = view_;
        in = new DataInputStream(ClientSocket.getInputStream());
        out = new DataOutputStream(ClientSocket.getOutputStream());
    }

    public void run(){
        try{
            System.out.println("Wait for opponent connection ...");
            getSymbol();
            getGameState();

            int i = 0;
            int j = 0;


            if(symbol == 'X')
                j = 0;
            else
                j = 1;

            while(true){

                if(gameEnd) {
                    System.out.println("The end of the game !");
                    break;
                }

                if(isMyTurn) {
                  //  setInput("@move " + i + " " + j);
                    i++;
                    j++;
                  //  Thread.sleep(500);
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

    public String readFromKeyboard(){
        Scanner sc = new Scanner(System.in);
        String text = null;
        text = sc.nextLine();
        return text;
    }


    public boolean sendMove () throws IOException {
        sendMessage = readFromKeyboard();
        out.writeUTF(sendMessage);
        inMessage = in.readUTF();

        if(inMessage.equals("@WrongCommand")) {
            System.out.println("The command is wrong! Enter again !");
            //return false;
        }

        if(inMessage.equals("@MoveSuccess")){
            String tmp = sendMessage.substring(6);
            String [] coordinates = tmp.split(" ");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            view.setBoard(x,y, symbol);
            view.printBoard();
            int r = checkIsWin();

            if(r == X_WIN){
                if(symbol == 'X')
                    System.out.println("You won !");
                else
                    System.out.println("You lost !");

                gameEnd = true;
            }

            if(r == O_WIN){
                if(symbol == 'O')
                    System.out.println("You won !");
                else
                    System.out.println("You lost !");

                gameEnd = true;
            }

            if(r == DRAW) {
                System.out.println("The board is full !");
                gameEnd = true;
            }

            if(r == CONTINUE)
                System.out.println("Wait for opponents move !");

            isMyTurn = false;
        }

        if(inMessage.equals("@MoveFail")){
            System.out.println("Enter another coordinate !");
            return false;
        }

        return true;
    }

    public void getOpponentMove() throws IOException {

        inMessage = in.readUTF();

        if(inMessage.startsWith("@OpponentMove")){
            String tmp = inMessage.substring(14);
            String [] coordinates = tmp.split(" ");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            view.setBoard(x, y, OpponentSymbol);
            view.printBoard();
            int r = checkIsWin();
            if(r == X_WIN){
                if(symbol == 'X')
                    System.out.println("You won !");
                else
                    System.out.println("You lost !");

                gameEnd = true;
            }

            if(r == O_WIN){
                if(symbol == 'O')
                    System.out.println("You won !");
                else
                    System.out.println("You lost !");

                gameEnd = true;

            }

            if(r == DRAW) {
                System.out.println("The board is full !");
                gameEnd = true;

            }

            if(r == CONTINUE)
                System.out.println("Enter your move !");
        }

        isMyTurn = true;
    }


    public int checkIsWin() throws IOException {

        int res = 4;
        out.writeUTF("@isWin");
        inMessage = in.readUTF();
        if(inMessage.startsWith("@isWin")){
            String tmp = inMessage.substring(7);
            res = Integer.parseInt(tmp);
        }

        return res;
    }

    public void getSymbol() throws IOException {
        inMessage = in.readUTF();
        if(inMessage.startsWith("@symbol")){
            symbol = inMessage.charAt(8);
            System.out.println("Your symbol is " + symbol);
            if(symbol == 'X') {
                OpponentSymbol = 'O';
                isMyTurn = true;
            }
            else {
                OpponentSymbol = 'X';
                isMyTurn = false;
            }
        }
    }

    public void getGameState() throws IOException {
        inMessage = in.readUTF();
        if(inMessage.equals("@start")) {
            System.out.println("The game has been started !");
            view.printBoard();
            if(symbol == 'X')
                System.out.println("Enter your move !");
            else
                System.out.println("Wait for opponents move !");
        }

    }

    public void setInput(String str){
        ByteArrayInputStream s = new ByteArrayInputStream(str.getBytes());
        System.setIn(s);

    }
}
