package Client.View;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class BoardView {
    private char[][] board = new char[10][10];
    private AtomicBoolean isMoved = new AtomicBoolean(false);
    private char symbol;
    public static final int DRAW = 0;
    public static final int X_WIN = 1;
    public static final int O_WIN = 2;
    public static final int CONTINUE = 3;

    public BoardView(){
        synchronized(this) {
            for (int i = 0; i < 10; i++)
                for (int j = 0; j < 10; j++)
                    board[i][j] = '.';
        }
    }

    public void setIsMoved(boolean state){
        isMoved.set(state);
    }
    public boolean getIsMoved (){
        return isMoved.get();
    }

    public char getSymbol(){
        return symbol;
    }
    public synchronized void setSymbol(char s){
        symbol = s;
    }

    public void printBoard(){
        for (int i = 0; i < 10; i++) {
            System.out.print("\n");
            if(i == 0){
                System.out.print("    ");
                for(int k = 0; k < 10; k++)
                    System.out.print(k + "   ");
                System.out.print("\n");
            }
            System.out.print(i + " ");
            System.out.print(" ");

            for (int j = 0; j < 10; j++) {
                System.out.print("[" + board[i][j] + "] ");
            }
        }
        System.out.print("\n");
    }

    public synchronized void setBoard(int x, int y, char s){
        board[x][y] = s;
    }

}
