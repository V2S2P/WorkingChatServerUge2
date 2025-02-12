package ChatServerDemoMandagUge2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
            if (clientHandler.name.equals(username)){
                clientHandler.notify(filterMessage);
                return;
            }
        }
    }
    @Override
    public void sendPrivateMessage(String message, String[] usernames){
        String filterMessage = filterMessage(message);
        boolean atLeastOneSent = false;

        for (String username : usernames) {
            username = username.trim();

            for (ClientHandler clientHandler : clients){
                if (clientHandler.name != null && clientHandler.name.equals(username)){
                    clientHandler.notify("Private message: " + filterMessage);
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
            if (clientHandler.name != null) {
                clientList.append(clientHandler.name).append(", ");
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
    public void showAllBannedWords(){

    }
    static class ClientHandler implements Runnable, IObserver {
        private IObservable server;
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String name = null;
        private int violations = 0;
        public ClientHandler(Socket socket,IObservable server) throws IOException {
            this.clientSocket = socket;
            this.server = server;
            out = new PrintWriter(clientSocket.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
        public void setName(String name){
            this.name = name;
        }
        public String getName(){
            return name;
        }
        public IObservable getServer(){
            return server;
        }
        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    String[] messageParts = message.split(" ", 2);
                    String commandName = messageParts[0];

                    ChatCommand command = ChatCommandFactory.getCommand(commandName);

                    if (command != null) {
                        String[] args = message.split(" ");
                        command.execute(args, this);
                    } else {
                        if (server.containsBannedWord(message)) {
                            handleViolations();
                            continue;
                        }
                        server.broadcast("Message from " + name + ": " + message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void helpCommands(){
            out.println("These are the commands you can use.");
            out.println("#PRIVATE");
            out.println("#SUB");
            out.println("#LEAVE");
            out.println("#BANNEDWORDS");
            out.println("GETLIST");
            out.println("#HELP");
        }
        public void handleViolations()throws IOException{
            violations++;
            out.println("Warning: You used a banned word! Strike " + violations + "/3");

            if (violations>=3){
                out.println("You have been banned for using banned words too many times.");
                server.broadcast(name + " has been banned for violating chat rules.");
                shutdown();
            }
        }
        public void shutdown() throws IOException {
            in.close();
            out.close();
            clientSocket.close();
        }
        @Override
        public void notify(String msg){
            out.println(msg);
        }
    }
    public String filterMessage(String message){
        for (String word : bannedWords){
            message = message.replaceAll("(?i)\\b" + word + "\\b", "**");
        }
        return message;
    }
}