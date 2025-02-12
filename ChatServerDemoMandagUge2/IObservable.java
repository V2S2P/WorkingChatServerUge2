package ChatServerDemoMandagUge2;

import java.io.IOException;

public interface IObservable {
    void broadcast(String message);
    void sendPrivateMessage(String message, String username)throws IOException;
    void addBannedWords(String word);
    void removeBannedWords(String word);
    void showAllBannedWords();
    void getClientList();
}
