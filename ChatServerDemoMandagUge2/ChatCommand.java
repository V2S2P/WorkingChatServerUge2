package ChatServerDemoMandagUge2;

public interface ChatCommand {
    void execute(String[] args, ChatServerDemo.ClientHandler client);
}
