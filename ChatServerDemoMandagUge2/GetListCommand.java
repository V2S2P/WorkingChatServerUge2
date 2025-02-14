package ChatServerDemoMandagUge2;
import ChatServerDemoMandagUge2.ClientHandler;

public class GetListCommand implements ChatCommand{
    @Override
    public void execute(String[] args, ClientHandler client) {
        StringBuilder clientList = new StringBuilder("Connected clients: ");

        for (ClientHandler clientHandler : client.getServer().getClients()){
            if (clientHandler.getName() != null){
                clientList.append(clientHandler.getName()).append(", ");
            }
        }
        if (clientList.length() > 18){
            clientList.setLength(clientList.length() - 2);
        }else {
            clientList.append("No clients connected");
        }

        client.notify(clientList.toString());
    }
}
