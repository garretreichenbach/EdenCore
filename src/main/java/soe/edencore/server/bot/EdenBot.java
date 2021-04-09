package soe.edencore.server.bot;

import api.common.GameServer;
import api.mod.StarLoader;
import api.utils.game.chat.CommandInterface;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import soe.edencore.server.bot.commands.DiscordCommand;
import soe.edencore.utils.ImageUtils;
import javax.security.auth.login.LoginException;
import java.io.IOException;

/**
 * EdenBot.java
 * <Description>
 *
 * @since 03/11/2021
 * @author TheDerpGamer
 */
public class EdenBot extends ListenerAdapter {

    private JDA bot;
    private String token;
    private long channelId;

    public EdenBot(String token, long channelId) {
        this.token = token;
        this.channelId = channelId;
    }

    public void initialize() {
        ImageUtils.getImage("https://i.imgur.com/2Prc2ke.jpg");

        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setActivity(Activity.playing("StarMade"));
        builder.addEventListeners(this);
        try {
            bot = builder.build();
        } catch(LoginException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String sender, String message) {
        //Todo: Change bot avatar to user avatar if they have linked accounts
        if(sender.equals("EdenBot")) {
            try {
                bot.getSelfUser().getManager().setAvatar(Icon.from(ImageUtils.getImage("https://i.imgur.com/2Prc2ke.jpg"))).queue();
            } catch(IOException exception) {
                exception.printStackTrace();
            }
        }
        bot.getSelfUser().getManager().setName(sender).queue();
        bot.getTextChannelById(channelId).sendMessage(message).queue();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        sendMessage("EdenBot", ":white_check_mark: Server Started");
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        sendMessage("EdenBot", ":octagonal_sign: Server Stopped");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String content = event.getMessage().getContentDisplay();
        if(content.length() > 0 && event.getChannel().getIdLong() == channelId) {
            if(content.charAt(0) == '/') {
                String[] split = content.replace("/", "").split(" ");
                CommandInterface commandInterface = StarLoader.getCommand(split[0]);
                if(commandInterface instanceof DiscordCommand) ((DiscordCommand) commandInterface).execute(event);
            } else {
                GameServer.getServerState().getChat().addToVisibleChat(event.getAuthor().getName(), content, true);
            }
            //Todo: Discord <-> Player link
            //Todo: Emote and Color support
        }
    }
}
