package ChatServerDemoMandagUge2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServerDemo implements IObservable {
    private ChatServerDemo(){} // A private constructor ensures that instances of this class can't be created directly from outside. This is also known as "singleton" approach.
    public synchronized static IObservable getInstance(){ // Returns a single instance of ChatServerDemo. Synchronized ensures thread safety (multiple threads will be handled properly)
        if (server == null){
            server = new ChatServerDemo();
        }
        return server;
    }
    private static volatile IObservable server = getInstance(); // The volatile keyword ensures that changes to "server" are visible across different threads.
    private List<ClientHandler> clients = new ArrayList<>(); // Holds all active clients connected to the server.
    private List<String> bannedWords = new ArrayList<>(List.of("Fuck","Shit","Idiot")); // Holds the banned words you can't say on the server.

    public static void main(String[] args) {
        new ChatServerDemo().startServer(8080);
    }
    public void startServer(int port){
        ExecutorService threadPool = Executors.newCachedThreadPool(); // Reuses threads for tasks instead of destroying and creating new ones, also able to create new threads when needed.
        try {
            ServerSocket serverSocket = new ServerSocket(port); // Listens for incoming connections on the specified port number (8080)
            while (true) {
                Socket clientSocket = serverSocket.accept(); // When a client connects, a socket object is created.
                ClientHandler clientHandler = new ClientHandler(clientSocket,this); // A ClientHandler is initialized to handle communication with the client's socket.
                clients.add(clientHandler); // We add each ClientHandler to a list.
                //new Thread(clientHandler).start();
                threadPool.execute(clientHandler); // Instead of creating a new thread manually, ClientHandler as a task is put inside the threadpool and threads sort of share it.

                clientHandler.notify("Welcome to the chat. Please enter #JOIN <username>");
                clientHandler.helpCommands();
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            threadPool.shutdown();
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