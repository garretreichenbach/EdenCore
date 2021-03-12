package soe.edencore.server.logger;

import api.listener.events.network.ClientLoginEvent;
import api.listener.events.player.PlayerChatEvent;
import soe.edencore.EdenCore;
import soe.edencore.server.ServerDatabase;
import soe.edencore.server.bot.EdenBot;
import java.io.IOException;

/**
 * ChatLogger.java
 * Handles chat message stuff
 * ==================================================
 * Created 03/10/2021
 * @author TheDerpGamer
 */
public class ChatLogger {

    public static void handlePlayerJoinEvent(ClientLoginEvent event) {
        EdenBot edenBot = EdenCore.instance.edenBot;
        edenBot.chatWebhook.setUsername("EdenBot");
        edenBot.chatWebhook.setAvatarUrl("https://i.imgur.com/2Prc2ke.jpg");
        if(ServerDatabase.getPlayerData(event.getPlayerName()) == null) {
            edenBot.chatWebhook.setContent(":confetti_ball: Everyone welcome " + event.getPlayerName() + " to Skies of Eden!");
            ServerDatabase.addNewPlayerData(event.getPlayerName());
        } else {
            edenBot.chatWebhook.setContent(":arrow_right: " + event.getPlayerName() + " has joined the server");
        }
        try {
            edenBot.chatWebhook.execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void handleCommand(String sender, String command) {
        EdenBot edenBot = EdenCore.instance.edenBot;
        edenBot.logWebhook.setUsername("EdenBot");
        edenBot.logWebhook.setAvatarUrl("https://i.imgur.com/2Prc2ke.jpg");
        edenBot.logWebhook.setContent(sender + " used command " + command);
        try {
            edenBot.logWebhook.execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void handleMessage(PlayerChatEvent event) {
        EdenBot edenBot = EdenCore.instance.edenBot;
        edenBot.chatWebhook.setUsername(event.getMessage().sender);
        edenBot.chatWebhook.setAvatarUrl("https://i.imgur.com/2Prc2ke.jpg");
        edenBot.chatWebhook.setContent(event.getText());
        try {
            edenBot.chatWebhook.execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
