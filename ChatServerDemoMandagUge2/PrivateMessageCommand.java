package ChatServerDemoMandagUge2;

import java.io.IOException;

public class PrivateMessageCommand implements ChatCommand{
    @Override
    public void execute(String[] args, ChatServerDemo.ClientHandler client){
        if (args.length < 3){
            client.notify("Usage: #PRIVATE <username> <message>");
            return;
        }
        String recipient = args[1];
        String privateMessage = args[2];

        if (client.getServer().containsBannedWord(privateMessage)){
            try {
                client.handleViolations();
            }catch (IOException e){
                e.printStackTrace();
            }
            return;
        }

        try {
            client.getServer().sendPrivateMessage("Private message from " + client.getName() + ": " + privateMessage, recipient);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
