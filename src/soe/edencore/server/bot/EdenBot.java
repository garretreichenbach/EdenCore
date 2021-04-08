package soe.edencore.server.bot;

/**
 * EdenBot.java
 * <Description>
 *
 * @since 03/11/2021
 * @author TheDerpGamer
 */
public class EdenBot {

    public DiscordWebhook chatWebhook;

    public EdenBot() {
        chatWebhook = new DiscordWebhook("");
        //chatWebhook = new DiscordWebhook("https://discord.com/api/webhooks/819708095813910528/yqcQF-Ge9Ijs3uIlSPEgdvQ3auZ4B5cU5FcCGej56AvNGgEim3u4oFrKLeIEENyL4viQ");
    }
}
