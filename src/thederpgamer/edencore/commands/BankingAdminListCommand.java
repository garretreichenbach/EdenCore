package thederpgamer.edencore.commands;

import api.mod.StarMod;
import api.utils.game.PlayerUtils;
import api.utils.game.chat.CommandInterface;
import org.luaj.vm2.ast.Str;
import org.newdawn.slick.command.Command;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.server.data.GameServerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.PlayerData;
import thederpgamer.edencore.data.other.BankingTransactionLog;
import thederpgamer.edencore.utils.DataUtils;

import javax.annotation.Nullable;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 18.09.2021
 * TIME: 02:35
 */
public class BankingAdminListCommand implements CommandInterface {
    @Override
    public String getCommand() {
        return "bank_show";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"bank_show"};
    }

    @Override
    public String getDescription() {
        return "show a players transaction log";
    }

    @Override
    public boolean isAdminOnly() {
        return true;
    }

    @Override
    public boolean onCommand(PlayerState admin, String[] strings) {
        PlayerState target = GameServerState.instance.getPlayerFromNameIgnoreCaseWOException(strings[0]);
        if (target == null) {
            PlayerUtils.sendMessage(admin,"player not found");
            return false;
        }
        PlayerData data = DataUtils.getPlayerData(target);
        StringBuilder out = new StringBuilder();
        out.append("Transaction log for ").append(target.getName()).append("(SM: ").append(target.getStarmadeName()).append(")\n");
        for (BankingTransactionLog t: data.getTransactions()) {
            out.append(t.toStringPretty()).append("\n-----------\n");
        }
        out.append("end of list");
        PlayerUtils.sendMessage(admin,out.toString());
        return true;
    }

    @Override
    public void serverAction(@Nullable PlayerState playerState, String[] strings) {

    }

    @Override
    public StarMod getMod() {
        return EdenCore.getInstance();
    }
}
