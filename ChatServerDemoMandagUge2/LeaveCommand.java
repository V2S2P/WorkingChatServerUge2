package ChatServerDemoMandagUge2;

import java.io.IOException;
import ChatServerDemoMandagUge2.ClientHandler;

public class LeaveCommand implements ChatCommand{
    public void execute(String[] args, ClientHandler client){
        client.getServer().broadcast(client.getName() + " has left the server");
        try {
            client.shutdown();
            client.getServer().getClients().remove(client);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
