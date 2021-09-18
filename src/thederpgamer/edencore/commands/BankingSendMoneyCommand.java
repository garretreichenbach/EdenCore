package thederpgamer.edencore.commands;

import api.mod.StarMod;
import api.utils.game.PlayerUtils;
import api.utils.game.chat.CommandInterface;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.data.other.BankingTransactionLog;

import javax.annotation.Nullable;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 18.09.2021
 * TIME: 00:49
 */
public class BankingSendMoneyCommand implements CommandInterface {
    @Override
    public String getCommand() {
        return "bank send";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"bank send"}; //dont question it, it wont show up in the commands list otherwise
    }

    @Override
    public String getDescription() {
        return "sends money from your account to another player. both players need to be online, and you need to have the amount of money in your account.\n" +
                "/%COMMAND% send Schema 500 \"payback for the beer yesterday\"";
    }

    @Override
    public boolean isAdminOnly() {
        return false;
    }

    @Override
    public boolean onCommand(PlayerState playerState, String[] strings) {
        // /bank send player_name amount mssg
        if (strings.length<3) {
            echoDescription(playerState);
            return false;
        }
        String to = strings[1];
        int amount = 0;
        try {
            amount = Integer.parseInt(strings[2]);
        } catch (NumberFormatException ex) {
            echoDescription(playerState);
            return false;
        }
        StringBuilder mssg = new StringBuilder();
        for (int i = 2; i < strings.length; i++) {
            mssg.append(strings[i]);
        }
        boolean success = BankingTransactionLog.sendMoney(playerState.getName(),to,amount,mssg.toString());
        if (!success) {
            PlayerUtils.sendMessage(playerState,"Unable to complete transaction. Either player does not exist or you dont have enough money.");
        }
        return true;
    }

    private void echoDescription(PlayerState p) {
        PlayerUtils.sendMessage(p,getDescription());
    }
    @Override
    public void serverAction(@Nullable PlayerState playerState, String[] strings) {

    }

    @Override
    public StarMod getMod() {
        return null;
    }
}
