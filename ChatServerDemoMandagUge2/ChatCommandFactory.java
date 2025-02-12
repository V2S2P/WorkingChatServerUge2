package ChatServerDemoMandagUge2;

import java.util.HashMap;
import java.util.Map;

public class ChatCommandFactory {
    private static final Map<String, ChatCommand> commands = new HashMap<>();

    static {
        commands.put("#JOIN", new JoinCommand());
        commands.put("#PRIVATE", new PrivateMessageCommand());
        commands.put("#LEAVE", new LeaveCommand());
    }
    public static ChatCommand getCommand(String command){
        return commands.getOrDefault(command, null);
    }
}
