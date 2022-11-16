package Server.Model;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class Board {
    private char[][] board = new char[10][10];
    private AtomicBoolean isSecondPlayer = new AtomicBoolean(false);


    public static final int DRAW = 0;
    public static final int X_WIN = 1;
    public static final int O_WIN = 2;
    public static final int CONTINUE = 3;

    public Board() {
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                board[i][j] = '.';

    }

    public void setPlayerNumber(){
        isSecondPlayer.set(true);
    }
    public boolean getIsSecondPlayer (){
        return isSecondPlayer.get();
    }



    public synchronized boolean move(int x, int y, char s) {
        if ((x < 0) || (x >= board.length) || (y < 0) || (y >= board[0].length))
            return false;

        if (board[x][y] != '.')
            return false;

        board[x][y] = s;

        return true;
    }

    public char [] [] getBoard(){
        return board;
    }

    public char getCell(int x, int y) {
        return board[x][y];
    }

    public int isWin() {
        int r = 0;
        for (int i = 0; i < 10; i++) {
            r = checkRow(i);
            if (r != CONTINUE)
                return r;

            r = checkColumn(i);
            if (r != CONTINUE)
                return r;
        }
        r = checkDiag();
        if (r != CONTINUE)
            return r;

        int sum = 0;
        for (int i = 0; i < 10; i++)
            for(int j = 0; j < 10; j++)
                if (board[i][j] == '.')
                    sum++;

        if (sum == 0)
            return DRAW;

        return CONTINUE;
    }

    private int checkRow(int x) {
        boolean r = true;
        for( int i = 0; i < 9; i++){
            if(board[x][i] != board[x][i+1]){
                r = false;
                break;
            }
        }

        if (r && board[x][0] == 'X')
            return X_WIN;
        if (r && board[x][0] == 'O')
            return O_WIN;

        return CONTINUE;
    }

    private int checkColumn(int y) {
        boolean r = true;
        for(int i = 0; i < 9; i++){
            if(board[i][y] != board[i+1][y]){
                r = false;
                break;
            }
        }

        if (r && board[0][y] == 'X')
            return X_WIN;
        if (r && board[0][y] == 'O')
            return O_WIN;

        return CONTINUE;
    }


    private int checkDiag() {
        boolean d1 = true;
        boolean d2 = true;

        for(int i = 0; i < 9; i++){
            if(board[i][i]!=board[i+1][i+1]){
                d1 = false;
                break;
            }
        }
        int k = 0;
        for(int j = 9; j > 0; j--){
            if(board[j][k]!=board[j-1][k+1]){
                d2 = false;
                break;
            }
            k++;
        }

        if ((d1 && board[0][0] == 'X') || (d2 && board[9][0] == 'X'))
            return X_WIN;


        if ((d1 && board[0][0] == 'O') || (d2 && board[9][0] == 'O'))
            return O_WIN;

        return CONTINUE;
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


    public int getSize() {
        return board.length;
    }
}
