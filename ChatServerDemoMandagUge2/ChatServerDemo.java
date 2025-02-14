package ChatServerDemoMandagUge2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServerDemo implements IObservable {
    private ChatServerDemo(){}
    public synchronized static IObservable getInstance(){
        if (server == null){
            server = new ChatServerDemo();
        }
        return server;
    }
    private static volatile IObservable server = getInstance();
    private List<ClientHandler> clients = new ArrayList<>();
    private List<String> bannedWords = new ArrayList<>(List.of("Fuck","Shit","Idiot"));

    public static void main(String[] args) {
        new ChatServerDemo().startServer(8080);
    }
    public void startServer(int port){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket,this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
                clientHandler.notify("Welcome to the chat. Please enter #JOIN <username>");
                clientHandler.helpCommands();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void broadcast(String message){
        String filterMessage = filterMessage(message);
        for (ClientHandler clientHandler : clients){
            clientHandler.notify(filterMessage);
        }
    }
    @Override
    public void sendPrivateMessage(String message, String username) throws IOException{
        String filterMessage = filterMessage(message);
        for (ClientHandler clientHandler : clients){
            if (clientHandler.getName().equals(username)){
                clientHandler.notify(filterMessage);
                return;
            }
        }
    }
    @Override
    public void sendPrivateMessage(String senderName, String message, String[] usernames){
        String filterMessage = filterMessage(message);
        boolean atLeastOneSent = false;

        for (String username : usernames) {
            username = username.trim();

            for (ClientHandler clientHandler : clients){
                if (clientHandler.getName() != null && clientHandler.getName().equals(username)){
                    clientHandler.notify("Private message from " + senderName + ": " + filterMessage);
                    atLeastOneSent = true;
                }
            }
            if (!atLeastOneSent){
                System.out.println("No users found");
            }
        }
    }
    @Override
    public boolean containsBannedWord(String msg){
        for (String word : bannedWords){
            if (msg.toLowerCase().contains(word.toLowerCase())){
                return true;
            }
        }
        return false;
    }
    @Override
    public void addBannedWords(String word){
        bannedWords.add(word);
        System.out.println(word + " has been added to the banned word-list");
    }
    @Override
    public void removeBannedWords(String word){
        bannedWords.remove(word);
        System.out.println(word + " has been removed from the banned word-list");
    }
    @Override
    public void getClientList(ClientHandler requestingClient) {
        StringBuilder clientList = new StringBuilder("Connected Clients: ");
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getName() != null) {
                clientList.append(clientHandler.getName()).append(", ");
            }
        }
        // Remove last ", " if the list is not empty
        if (clientList.length() > 18) {  // "Connected Clients: " has 18 characters
            clientList.setLength(clientList.length() - 2);
        } else {
            clientList.append("No clients connected.");
        }
        requestingClient.notify(clientList.toString()); // Send list to requesting client
    }
    public String filterMessage(String message){
        for (String word : bannedWords){
            message = message.replaceAll("(?i)\\b" + word + "\\b", "**");
        }
        return message;
    }
    @Override
    public List<ClientHandler> getClients(){
        return clients;
    }
    @Override
    public List<String> getBannedWords(){
        return bannedWords;
    }
}