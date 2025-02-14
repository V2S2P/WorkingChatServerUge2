package ChatServerDemoMandagUge2;

import java.io.IOException;
import ChatServerDemoMandagUge2.ClientHandler;

public class PrivateMessageCommand implements ChatCommand{
    @Override
    public void execute(String[] args, ClientHandler client){
        if (args.length < 3){
            client.notify("Usage: #PRIVATE <username> <message>");
            return;
        }
        String recipient = args[1];
        StringBuilder privateMessageBuilder = new StringBuilder();

        for (int i = 2; i < args.length; i++) {
            privateMessageBuilder.append(args[i]).append(" ");
        }

        String privateMessage = privateMessageBuilder.toString().trim();

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
