package soe.edencore;

import api.common.GameCommon;
import api.listener.Listener;
import api.listener.events.faction.FactionCreateEvent;
import api.listener.events.faction.FactionRelationChangeEvent;
import api.listener.events.network.ClientLoginEvent;
import api.listener.events.player.*;
import api.mod.StarLoader;
import api.mod.StarMod;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.faction.FactionRelation;
import soe.edencore.server.bot.EdenBot;
import soe.edencore.server.logger.ChatLogger;
import java.io.IOException;

/**
 * EdenCore.java
 * <Description>
 * ==================================================
 * Created 03/10/2021
 * @author TheDerpGamer
 */
public class EdenCore extends StarMod {

    //Instance
    public EdenCore() {
        instance = this;
    }
    public static EdenCore instance;
    public static void main(String[] args) { }

    //Data
    public final String[] defaultPermissions = {
            "player.chat.general.speak",
            "player.chat.general.read",
            "player.chat.faction.speak",
            "player.chat.faction.read"
    };
    public EdenBot edenBot;

    @Override
    public void onEnable() {
        instance = this;
        registerListeners();
        initialize();
    }

    @Override
    public void onDisable() {
        edenBot.chatWebhook.setUsername("EdenBot");
        edenBot.chatWebhook.setAvatarUrl("https://i.imgur.com/2Prc2ke.jpg");
        edenBot.chatWebhook.setContent(":octagonal_sign: Server Stopped");
        try {
            edenBot.chatWebhook.execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void registerListeners() {
        StarLoader.registerListener(PlayerChatEvent.class, new Listener<PlayerChatEvent>() {
            @Override
            public void onEvent(PlayerChatEvent event) {
                if(event.getText().startsWith("/")) {
                    ChatLogger.handleCommand(event.getMessage().sender, event.getText());
                } else {
                    ChatLogger.handleMessage(event);
                }
            }
        }, this);

        StarLoader.registerListener(PlayerCustomCommandEvent.class, new Listener<PlayerCustomCommandEvent>() {
            @Override
            public void onEvent(PlayerCustomCommandEvent event) {
                ChatLogger.handleCommand(event.getSender().getName(), convertToString(event.getCommand().getCommand(), event.getArgs()));
            }
        }, this);

        StarLoader.registerListener(ClientLoginEvent.class, new Listener<ClientLoginEvent>() {
            @Override
            public void onEvent(ClientLoginEvent event) {
                ChatLogger.handlePlayerJoinEvent(event);
            }
        }, this);

        StarLoader.registerListener(FactionCreateEvent.class, new Listener<FactionCreateEvent>() {
            @Override
            public void onEvent(FactionCreateEvent event) {
                edenBot.chatWebhook.setUsername("EdenBot");
                edenBot.chatWebhook.setAvatarUrl("https://i.imgur.com/2Prc2ke.jpg");
                edenBot.chatWebhook.setContent(":new: " + event.getPlayer().getName() + " has created a new faction called " + event.getFaction().getName());
                try {
                    edenBot.chatWebhook.execute();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }, this);

        StarLoader.registerListener(PlayerJoinFactionEvent.class, new Listener<PlayerJoinFactionEvent>() {
            @Override
            public void onEvent(PlayerJoinFactionEvent event) {
                edenBot.chatWebhook.setUsername("EdenBot");
                edenBot.chatWebhook.setAvatarUrl("https://i.imgur.com/2Prc2ke.jpg");
                edenBot.chatWebhook.setContent(":heavy_plus_sign: " + event.getPlayer().getName() + " has joined the faction " + event.getFaction().getName());
                try {
                    edenBot.chatWebhook.execute();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }, this);

        StarLoader.registerListener(PlayerLeaveFactionEvent.class, new Listener<PlayerLeaveFactionEvent>() {
            @Override
            public void onEvent(PlayerLeaveFactionEvent event) {
                edenBot.chatWebhook.setUsername("EdenBot");
                edenBot.chatWebhook.setAvatarUrl("https://i.imgur.com/2Prc2ke.jpg");
                edenBot.chatWebhook.setContent(":heavy_minus_sign: " + event.getPlayer().getName() + " has left the faction " + event.getFaction().getName());
                try {
                    edenBot.chatWebhook.execute();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }, this);

        StarLoader.registerListener(PlayerDeathEvent.class, new Listener<PlayerDeathEvent>() {
            @Override
            public void onEvent(PlayerDeathEvent event) {
                edenBot.chatWebhook.setUsername("EdenBot");
                edenBot.chatWebhook.setAvatarUrl("https://i.imgur.com/2Prc2ke.jpg");
                if(event.getDamager().isSegmentController()) {
                    SegmentController segmentController = (SegmentController) event.getDamager();
                    edenBot.chatWebhook.setContent(":skull_crossbones: " + event.getPlayer().getName() + " was killed by entity " + segmentController.getName() + " [" + GameCommon.getGameState().getFactionManager().getFaction(segmentController.getFactionId()).getName() + "]");
                } else if(event.getDamager() instanceof PlayerState) {
                    PlayerState playerState = (PlayerState) event.getDamager();
                    if(playerState.getFactionId() != 0) {
                        edenBot.chatWebhook.setContent(":skull_crossbones: " + event.getPlayer().getName() + " was killed by player " + playerState.getName() + " [" + GameCommon.getGameState().getFactionManager().getFaction(playerState.getFactionId()).getName() + "]");
                    } else {
                        edenBot.chatWebhook.setContent(":skull_crossbones: " + event.getPlayer().getName() + " was killed by player " + playerState.getName());
                    }
                } else {
                    edenBot.chatWebhook.setContent(":skull_crossbones: " + event.getPlayer().getName() + " has died");
                }
                try {
                    edenBot.chatWebhook.execute();
                } catch(IOException exception) {
                    exception.printStackTrace();
                }
            }
        }, this);

        StarLoader.registerListener(FactionRelationChangeEvent.class, new Listener<FactionRelationChangeEvent>() {
            @Override
            public void onEvent(FactionRelationChangeEvent event) {
                edenBot.chatWebhook.setUsername("EdenBot");
                edenBot.chatWebhook.setAvatarUrl("https://i.imgur.com/2Prc2ke.jpg");

                if(event.getNewRelation().equals(FactionRelation.RType.FRIEND)) {
                    edenBot.chatWebhook.setContent(":shield: " + event.getFrom().getName() + " is now allied with " + event.getTo().getName());
                } else if(event.getNewRelation().equals(FactionRelation.RType.ENEMY)) {
                    edenBot.chatWebhook.setContent(":crossed_swords: " + event.getFrom().getName() + " is now at war with " + event.getTo().getName());
                } else {
                    return;
                }

                try {
                    edenBot.chatWebhook.execute();
                } catch(IOException exception) {
                    exception.printStackTrace();
                }
            }
        }, this);
    }

    private String convertToString(String command, String... args) {
        StringBuilder builder = new StringBuilder();
        builder.append(command);
        for(String arg : args) builder.append(" ").append(arg);
        return builder.toString();
    }

    private void initialize() {
        edenBot = new EdenBot();
        edenBot.chatWebhook.setUsername("EdenBot");
        edenBot.chatWebhook.setAvatarUrl("https://i.imgur.com/2Prc2ke.jpg");
        edenBot.chatWebhook.setContent(":white_check_mark: Server Started");
        try {
            edenBot.chatWebhook.execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
