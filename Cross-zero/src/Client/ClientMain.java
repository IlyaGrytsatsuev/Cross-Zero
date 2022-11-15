package Client;
import Client.GetandSend.*;

public class ClientMain {
    public static void main(String[] args){
        try {
            NetworkManager manager = new NetworkManager();
            manager.start();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
