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
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void broadcast(String message){
        for (ClientHandler clientHandler : clients){
            clientHandler.notify(message);
        }
    }
    @Override
    public void sendPrivateMessage(String message, String username) throws IOException{
        for (ClientHandler clientHandler : clients){
            if (clientHandler.name.equals(username)){
                clientHandler.notify(message);
                break;
            }
        }
    }
    private static class ClientHandler implements Runnable, IObserver {
        private IObservable server;
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String name = null;
        public ClientHandler(Socket socket,IObservable server) throws IOException {
            this.clientSocket = socket;
            this.server = server;
            out = new PrintWriter(clientSocket.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("#JOIN")) {
                        this.name = message.split(" ")[1];
                        server.broadcast("A new person joined the chat. Welcome to " + name);
                    } else if (message.startsWith("#PRIVATE")) {
                        String[] messagePart = message.split(" ", 3);
                        if (messagePart.length < 3){
                            out.println("Invalid private message format.");
                        }
                        String recipient = messagePart[1];
                        String privateMessage = messagePart[2];
                        server.sendPrivateMessage("Private message from " + name + ": " + privateMessage, recipient);
                    } else if (message.startsWith("#LEAVE")) {
                        server.broadcast(name + " has left the server");
                        shutdown();
                    } else {
                        System.out.println(message);
                        server.broadcast("Message from " + name + ": " + message);
                    }
                }
            }catch (IOException e){

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
}