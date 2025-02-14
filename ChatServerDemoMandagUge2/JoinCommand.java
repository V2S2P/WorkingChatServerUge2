package ChatServerDemoMandagUge2;
import ChatServerDemoMandagUge2.ClientHandler;

public class JoinCommand implements ChatCommand{
    @Override
    public void execute(String[] args, ClientHandler client){
        if (args.length < 2){
            client.notify("Usage: #JOIN <username>");
            return;
        }
        client.setName(args[1]);
        client.getServer().broadcast("A new person joined the chat. Welcome " + args[1]);
    }
}
