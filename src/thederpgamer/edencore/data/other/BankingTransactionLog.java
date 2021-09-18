package thederpgamer.edencore.data.other;

import org.schema.game.common.data.player.PlayerState;
import org.schema.game.server.data.GameServerState;
import thederpgamer.edencore.utils.DataUtils;

import java.io.Serializable;
import java.util.Locale;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 18.09.2021
 * TIME: 00:21
 * used to log transactions persistently. provides static helper methods for banking
 */
public class BankingTransactionLog implements Serializable {
    String from;
    String to;
    int credits;
    String mssg;
    long time;
    public BankingTransactionLog(String from, String to, int credits, String mssg) {
        time = System.currentTimeMillis();
        this.from = from;
        this.to = to;
        this.mssg = mssg;
        this.credits = credits;
    }

    public static boolean isExistingPlayer(String name) {
        //TODO test if valid player
        return true;
    }

    public static boolean hasMoney(PlayerState player, int credits) {
        return (player.getCredits()>=credits);
    }

    /**
     * will attempt to send money from one player to another. will return false if something fails.
     * both need to be online (existing playerstates) and the sender has to have the money.
     * @param from
     * @param to
     * @param credits
     * @param mssg mssg for this transaction
     * @return
     */
    public static boolean sendMoney(String from, String to, int credits, String mssg) {
        if (!(isExistingPlayer(from) && isExistingPlayer(to)))
            return false;
        PlayerState fromP = getPlayer(from);
        PlayerState toP = getPlayer(to);
        if (fromP == null || toP == null)
            return false;

        if (!hasMoney(fromP,credits))
            return false;
        fromP.setCredits(fromP.getCredits()-credits);
        toP.setCredits(toP.getCredits()+credits);

        //log transaction on success
        BankingTransactionLog transaction = new BankingTransactionLog(from,to,credits, mssg);
        DataUtils.getPlayerData(fromP).addTransaction(transaction);
        return true;
    }

    private static PlayerState getPlayer(String name) {
        return GameServerState.instance.getPlayerFromNameIgnoreCaseWOException(name);
    }
}
