package thederpgamer.edencore.data.other;

import org.schema.game.common.data.player.PlayerState;
import org.schema.game.server.data.GameServerState;
import org.schema.schine.common.language.Lng;
import org.schema.schine.network.server.ServerMessage;
import thederpgamer.edencore.utils.DataUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 18.09.2021
 * TIME: 00:21
 * used to log transactions persistently. provides static helper methods for banking
 */
public class BankingTransactionLog implements Serializable {

    public String from;
    public String to;
    public int credits;
    public String mssg;
    public long time;

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
     * @param from player A
     * @param to player B
     * @param credits amount of credits
     * @param mssg mssg for this transaction
     * @return log object
     */
    public static BankingTransactionLog sendMoney(String from, String to, int credits, String mssg) {
        from = from.toLowerCase(Locale.ENGLISH);
        to = to.toLowerCase(Locale.ENGLISH);

        if (!isExistingPlayer(from) || !isExistingPlayer(to)) return null;
        PlayerState fromP = getPlayer(from);
        PlayerState toP = getPlayer(to);

        if (fromP == null || toP == null || fromP.equals(toP))
            return null;

        if (credits <= 0)
            return null;

        if (!hasMoney(fromP,credits))
            return null;

        fromP.setCredits(fromP.getCredits()-credits);
        toP.setCredits(toP.getCredits()+credits);

        //log transaction on success for sender and receiver
        BankingTransactionLog transaction = new BankingTransactionLog(from,to,credits, mssg);
        DataUtils.getPlayerData(fromP).addTransaction(transaction);
        DataUtils.getPlayerData(toP).addTransaction(transaction);

        //notify receiver
        popUpMssg("You have received money: \n"+transaction.toStringPretty(),toP);

        return transaction;
    }

    private static PlayerState getPlayer(String name) {
        return GameServerState.instance.getPlayerFromNameIgnoreCaseWOException(name);
    }

    public String toStringPretty() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM,yyyy HH:mm z");
        Date resultdate = new Date(time);

        return "Banking transaction:" + "\n"+
                "from: '" + from + '\'' + "\n"+
                "to: '" + to + '\'' + "\n"+
                "credits: " + credits + "\n"+
                "time: " +  sdf.format(resultdate) + "\n"+
                "mssg: '" + mssg + '\'';
    }

    public static void popUpMssg(String mssg, PlayerState p) {
        p.sendServerMessage(new ServerMessage(Lng.astr(mssg), ServerMessage.MESSAGE_TYPE_DIALOG, p.getId()));
    }
}
