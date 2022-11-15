package Client;
import Client.GetandSend.NetworkManager;
import Client.View.*;


public class TestMain {
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
