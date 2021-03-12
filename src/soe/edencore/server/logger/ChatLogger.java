package soe.edencore.server.logger;

import api.listener.events.player.PlayerChatEvent;
import api.listener.events.player.PlayerJoinWorldEvent;
import api.listener.events.player.PlayerLeaveWorldEvent;
import soe.edencore.EdenCore;
import soe.edencore.server.ServerDatabase;
import soe.edencore.server.bot.EdenBot;
import java.io.IOException;

/**
 * ChatLogger.java
 * Handles chat message stuff
 *
 * @since 03/10/2021
 * @author TheDerpGamer
 */
public class ChatLogger {

    private static EdenBot getBot() {
        return EdenCore.instance.edenBot;
    }

    public static void handlePlayerJoinEvent(PlayerJoinWorldEvent event) {
        EdenBot edenBot = getBot();
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

    public static void handlePlayerLeaveEvent(PlayerLeaveWorldEvent event) {
        EdenBot edenBot = getBot();
        edenBot.chatWebhook.setUsername("EdenBot");
        edenBot.chatWebhook.setAvatarUrl("https://i.imgur.com/2Prc2ke.jpg");
        edenBot.chatWebhook.setContent(":door: " + event.getPlayerName() + " has left the server");
        try {
            edenBot.chatWebhook.execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void handleMessage(PlayerChatEvent event) {
        EdenBot edenBot = getBot();
        edenBot.chatWebhook.setUsername(event.getMessage().sender);
        edenBot.chatWebhook.setAvatarUrl("https://i.imgur.com/2Prc2ke.jpg");
        edenBot.chatWebhook.setContent(event.getText());
        try {
            edenBot.chatWebhook.execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void handleCommand(String sender, String command) {
        EdenBot edenBot = getBot();
        edenBot.logWebhook.setUsername("EdenBot");
        edenBot.logWebhook.setAvatarUrl("https://i.imgur.com/2Prc2ke.jpg");
        edenBot.logWebhook.setContent(sender + " used command " + command);
        try {
            edenBot.logWebhook.execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
