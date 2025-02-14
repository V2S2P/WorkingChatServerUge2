package ChatServerDemoMandagUge2;
import ChatServerDemoMandagUge2.ClientHandler;
public class GetHelpCommand implements ChatCommand{
    @Override
    public void execute(String[] args, ClientHandler client) {
        client.helpCommands();
    }
}
