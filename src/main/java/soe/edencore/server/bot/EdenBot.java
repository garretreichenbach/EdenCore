package soe.edencore.server.bot;

import api.common.GameServer;
import api.mod.StarLoader;
import api.utils.game.PlayerUtils;
import api.utils.game.chat.CommandInterface;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.schema.game.common.data.player.PlayerState;
import soe.edencore.data.player.PlayerData;
import soe.edencore.server.ServerDatabase;
import soe.edencore.server.bot.commands.DiscordCommand;
import soe.edencore.utils.ImageUtils;
import soe.edencore.utils.LogUtils;
import soe.edencore.utils.MessageType;
import javax.security.auth.login.LoginException;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;

/**
 * EdenBot.java
 * <Description>
 *
 * @since 03/11/2021
 * @author TheDerpGamer
 */
public class EdenBot extends ListenerAdapter {

    public JDA bot;
    private String token;
    private long chatChannelId;
    private long commandChannelId;
    private HashMap<PlayerData, Integer> linkRequestMap;

    public EdenBot(String token, long chatChannelId, long commandChannelId) {
        this.token = token;
        this.chatChannelId = chatChannelId;
        this.commandChannelId = commandChannelId;
        this.linkRequestMap = new HashMap<PlayerData, Integer>();
    }

    public enum MessageMode {DISCORD, SERVER, BOTH}

    public void initialize() {

        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setActivity(Activity.playing("StarMade"));
        builder.addEventListeners(this);
        try {
            bot = builder.build();
            bot.getSelfUser().getManager().setAvatar(Icon.from(ImageUtils.getAvatarImage(bot.getSelfUser().getIdLong()))).queue();
            LogUtils.logMessage(MessageType.INFO, "Successfully initialized bot.");
        } catch(LoginException | IOException exception) {
            exception.printStackTrace();
        }
    }

    public void sendMessage(String sender, String message, MessageMode mode) {
        StringBuilder builder = new StringBuilder();
        char[] array = message.toCharArray();
        boolean emoteMode = false;
        for(int i = 0; i < array.length; i ++) {
            if(array[i] == '&') i ++;
            else if(array[i] == ':') {
                if(emoteMode) emoteMode = false;
                else {
                    builder.append('\\');
                    emoteMode = true;
                }
                builder.append(array[i]);
            } else builder.append(array[i]);
        }
        message = builder.toString();

        if(mode.equals(MessageMode.DISCORD) || mode.equals(MessageMode.BOTH)) {
            if(sender.equals("EdenBot")) {
                try {
                    bot.getSelfUser().getManager().setAvatar(Icon.from(ImageUtils.getAvatarImage(bot.getSelfUser().getIdLong()))).queue();
                } catch(IOException exception) {
                    exception.printStackTrace();
                    LogUtils.logMessage(MessageType.ERROR, "Failed to get avatar of user " + sender + ".");
                }
            } else {
                for(PlayerData playerData : ServerDatabase.getAllPlayerData()) {
                    if(playerData.getDiscordId() != -1 && playerData.getPlayerName().equals(sender)) {
                        try {
                            bot.getSelfUser().getManager().setAvatar(Icon.from(ImageUtils.getAvatarImage(bot.getUserById(playerData.getDiscordId()).getIdLong()))).queue();
                        } catch(IOException exception) {
                            exception.printStackTrace();
                            try {
                                bot.getSelfUser().getManager().setAvatar(Icon.from(ImageUtils.getAvatarImage(bot.getSelfUser().getIdLong()))).queue();
                            } catch(IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                }
            }

            bot.getSelfUser().getManager().setName(sender).queue();
            bot.getTextChannelById(chatChannelId).sendMessage(message).queue();
        }

        if(mode.equals(MessageMode.SERVER) || mode.equals(MessageMode.BOTH)) {
            GameServer.getServerState().getChat().addToVisibleChat("[DISCORD]: [" + sender + "]", message.replace("\\", ""), true);
        }
    }

    public void sendTimedMessage(String sender, String message, int seconds) {
        if(sender.equals("EdenBot")) {
            try {
                bot.getSelfUser().getManager().setAvatar(Icon.from(ImageUtils.getAvatarImage(bot.getSelfUser().getIdLong()))).queue();
            } catch(IOException exception) {
                exception.printStackTrace();
            }
        }
        bot.getSelfUser().getManager().setName(sender).queue();
        final MessageAction action = bot.getTextChannelById(commandChannelId).sendMessage(message);
        action.queue();
        TimerTask task = new TimerTask() {
            public void run() {
                ((Message) action).delete();
            }
        };
        Timer timer = new Timer("message-timer_" + ((Message) action).getIdLong());
        timer.schedule(task, seconds * 10000L);
    }

    public void addLinkRequest(PlayerState playerState) {
        final PlayerData playerData = ServerDatabase.getPlayerData(playerState.getName());
        if(linkRequestMap.containsKey(playerData)) linkRequestMap.remove(playerData);
        linkRequestMap.put(playerData, (new Random()).nextInt(9999 - 1000) + 1000);
        PlayerUtils.sendMessage(playerState, "Use /link " + linkRequestMap.get(playerData) + " in #bot-commands to link your account. This code will expire in 15 minutes.");
        TimerTask task = new TimerTask() {
            public void run() {
                removeLinkRequest(playerData);
            }
        };
        Timer timer = new Timer("link-timer_" + linkRequestMap.get(playerData));
        timer.schedule(task, 900000);
    }

    public void removeLinkRequest(PlayerData playerData) {
        linkRequestMap.remove(playerData);
    }

    public PlayerData getLinkRequest(int linkId) {
        for(Map.Entry<PlayerData, Integer> entry : linkRequestMap.entrySet()) {
            if(entry.getValue() == linkId) return entry.getKey();
        }
        return null;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        sendMessage("EdenBot", ":white_check_mark: Server Started", MessageMode.BOTH);
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        sendMessage("EdenBot", ":octagonal_sign: Server Stopped", MessageMode.BOTH);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String content = event.getMessage().getContentDisplay();
        if(content.length() > 0) {
            if(!content.startsWith("[DISCORD]: ")) {
                if(event.getChannel().getIdLong() == chatChannelId) {
                    if(content.charAt(0) == '/') event.getMessage().delete().queue();
                    else sendMessage(event.getAuthor().getName(), event.getMessage().getContentDisplay(), MessageMode.SERVER);
                } else if(event.getChannel().getIdLong() == commandChannelId) {
                    if(content.charAt(0) == '/') {
                        String[] split = content.replace("/", "").split(" ");
                        CommandInterface commandInterface = StarLoader.getCommand(split[0]);
                        if(commandInterface instanceof DiscordCommand) ((DiscordCommand) commandInterface).execute(event);
                    } else event.getMessage().delete().queue();
                }
            }
        }
    }
}
