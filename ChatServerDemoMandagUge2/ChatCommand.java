package ChatServerDemoMandagUge2;
import ChatServerDemoMandagUge2.ClientHandler;

public interface ChatCommand {
    void execute(String[] args, ClientHandler client);
}
