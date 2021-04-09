package soe.edencore.server.bot;

/**
 * BotThread
 * <Description>
 *
 * @author Garret Reichenbach
 * @since 04/09/2021
 */
public class BotThread extends Thread {

    private EdenBot edenBot;

    public BotThread(String token, long channelId) {
        edenBot = new EdenBot(token, channelId);
    }

    @Override
    public void run() {
        edenBot.initialize();
    }

    public EdenBot getBot() {
        return edenBot;
    }
}
