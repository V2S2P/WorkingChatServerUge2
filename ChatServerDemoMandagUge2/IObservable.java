package ChatServerDemoMandagUge2;

public interface IObservable {
    void broadcast(String message);
    void sendPrivateMessage(String message, String username);

}
