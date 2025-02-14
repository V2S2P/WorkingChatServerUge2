package ChatServerDemoMandagUge2;
import ChatServerDemoMandagUge2.ClientHandler;
import ChatServerDemoMandagUge2.ChatServerDemo;
public class ShowBannedWordsCommand implements ChatCommand{
    @Override
    public void execute(String[] args, ClientHandler client) {
        for (String word : client.getServer().getBannedWords()){
            client.notify(word);
        }
    }
}
