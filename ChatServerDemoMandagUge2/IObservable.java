package ChatServerDemoMandagUge2;

import java.io.IOException;

public interface IObservable {
    void broadcast(String message);
    void sendPrivateMessage(String message, String username)throws IOException;

}
