package Server;
import Server.Controller.*;

public class ServerMain {

    public static void main(String[] args){
        try {
            ServerManager manager = new ServerManager();
            manager.start();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
