package Client.Controller;

import Client.View.BoardView;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientToServerCommunicator {

    protected Socket ClientSocket;
    protected DataInputStream in;
    protected String inMessage;
    protected char symbol ;
    protected char OpponentSymbol;

    protected BoardView view;
    protected DataOutputStream out;
    protected String sendMessage;

    protected boolean isMyTurn;

    protected boolean gameEnd = false;

    protected static final int DRAW = 0;
    protected static final int X_WIN = 1;
    protected static final int O_WIN = 2;
    protected static final int CONTINUE = 3;


    public ClientToServerCommunicator(Socket socket,  BoardView view_) throws IOException {
        ClientSocket = socket;
        view = view_;
        in = new DataInputStream(ClientSocket.getInputStream());
        out = new DataOutputStream(ClientSocket.getOutputStream());
    }


    protected String readFromKeyboard(){
        Scanner sc = new Scanner(System.in);
        String text = null;
        text = sc.nextLine();
        return text;
    }


    protected boolean sendMove () throws IOException {
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

    protected void getOpponentMove() throws IOException {

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


    protected int checkIsWin() throws IOException {

        int res = 4;
        out.writeUTF("@isWin");
        inMessage = in.readUTF();
        if(inMessage.startsWith("@isWin")){
            String tmp = inMessage.substring(7);
            res = Integer.parseInt(tmp);
        }

        return res;
    }

    protected void getSymbol() throws IOException {
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

    protected void getGameState() throws IOException {
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

    protected void setInput(String str){
        ByteArrayInputStream s = new ByteArrayInputStream(str.getBytes());
        System.setIn(s);

    }

    protected void setNick() throws IOException {
        System.out.println("Enter your nickname: ");
        while(true) {
            String name = readFromKeyboard();
            out.writeUTF("@nick " + name);
            inMessage = in.readUTF();
            if (inMessage.equals("@nickSuccess")) {
                break;
            }

            if (inMessage.equals("@nickFail")) {
                System.out.println("This nickname already exists ! Enter another one: ");
            }
        }
    }

    protected void chooseOpponent() throws IOException {

        boolean isChosen = false;
        int state = 0;

        Scanner sc = new Scanner(System.in);

        while(true) {
            System.out.println("Enter (1) to connect to another player's board or (2) to make a new board: ");
            String tmp = sc.nextLine();
            if(tmp.matches("[1-2]+"))
                state = Integer.parseInt(tmp);

            else {
                System.out.println("The input is incorrect ! Try again!");
                continue;
            }


            if(state == 1) {
                while (true) {
                    System.out.println("Enter nickname of the user you want to play with: ");
                    String opponent = sc.nextLine();
                    out.writeUTF("@opponent " + opponent);
                    inMessage = in.readUTF();

                    if (inMessage.equals("@opponentSuccess")) {
                        isChosen = true;
                        System.out.println("Opponent has been found !");
                        break;
                    }

                    if (inMessage.equals("@opponentFail")) {
                        System.out.println("User doesn't exist or is already playing !");
                    }
                }
                if(isChosen)
                    break;
            }

            if(state == 2){
                out.writeUTF("@newBoard");
                inMessage = in.readUTF();
                if(inMessage.equals("@newBoardSuccess")){
                    System.out.println("Waiting for opponent ...");
                    break;
                }

            }
        }
    }
}

