package ChatServerDemoMandagUge2;

import java.io.IOException;
import java.util.List;

public interface IObservable {
    void broadcast(String message);
    void sendPrivateMessage(String message, String username)throws IOException;
    void sendPrivateMessage(String senderName, String message, String[] usernames);
    void addBannedWords(String word);
    void removeBannedWords(String word);
    void getClientList(ClientHandler requestingClient);
    boolean containsBannedWord(String msg);
    List<ClientHandler> getClients();
    List<String> getBannedWords();
}
