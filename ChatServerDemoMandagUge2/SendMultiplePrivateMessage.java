package ChatServerDemoMandagUge2;

public class SendMultiplePrivateMessage implements ChatCommand{
    @Override
    public void execute(String[] args, ClientHandler client) {
        client.sendPrivateMessage(args);
    }
}
