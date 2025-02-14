package ChatServerDemoMandagUge2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class ClientHandler implements Runnable, IObserver{
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
            out.println("#GETLIST");
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
        public void sendPrivateMessage(String[] args){
            if (args.length < 3){
                notify("Usage: #SUB username1,username2,username3 message");
                return;
            }
            String[] usernames = args[1].split(",");
            String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

            server.sendPrivateMessage(name, message, usernames);
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