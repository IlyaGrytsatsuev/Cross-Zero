package Server.Controller;
import Server.Model.*;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class BoardThread extends BoardCommunicator implements Runnable{

    private boolean isFirstUserTurn = true;


    public BoardThread(ConcurrentHashMap<String, String> pairs, ConcurrentHashMap<String, Socket> users, String name) throws IOException {
        super(pairs, users, name);
    }

    public void run(){
        try{
            connectSecondUser();

            while(true){

                if(isFirstUserTurn){
                    move("first");
                    isFirstUserTurn = false;
                }

                else if(!isFirstUserTurn) {
                    move("second");
                    isFirstUserTurn = true;
                }

                if(isOver)
                    break;
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }

    }




}
