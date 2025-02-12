package ChatServerDemoMandagUge2;

import java.io.IOException;

public class LeaveCommand implements ChatCommand{
    public void execute(String[] args, ChatServerDemo.ClientHandler client){
        client.getServer().broadcast(client.getName() + " has left the server");
        try {
            client.shutdown();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
