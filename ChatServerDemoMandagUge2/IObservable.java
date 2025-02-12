package ChatServerDemoMandagUge2;

import java.io.IOException;
import ChatServerDemoMandagUge2.ChatServerDemo.*;

public interface IObservable {
    void broadcast(String message);
    void sendPrivateMessage(String message, String username)throws IOException;
    void sendPrivateMessage(String message, String[] usernames);
    void addBannedWords(String word);
    void removeBannedWords(String word);
    void showAllBannedWords();
    void getClientList(ClientHandler requestingClient);
    boolean containsBannedWord(String msg);
}
