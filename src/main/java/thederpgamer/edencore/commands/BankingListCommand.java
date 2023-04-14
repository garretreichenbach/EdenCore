package thederpgamer.edencore.commands;

import api.mod.StarMod;
import api.utils.game.PlayerUtils;
import api.utils.game.chat.CommandInterface;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.other.BankingTransactionLog;
import thederpgamer.edencore.utils.DataUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 18.09.2021
 * TIME: 01:56
 */
public class BankingListCommand implements CommandInterface {
	@Override
	public String getCommand() {
		return "bank_list";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"bank_list"};
	}

	@Override
	public String getDescription() {
		return "Lists your previous transactions. Up to 10 transactions are saved, and older ones get automatically deleted.\n" + "- /%COMMAND% : Lists up to 10 previous transactions.";
	}

	@Override
	public boolean isAdminOnly() {
		return false;
	}

	@Override
	public boolean onCommand(PlayerState playerState, String[] strings) {
		List<BankingTransactionLog> ts = DataUtils.getPlayerData(playerState).getTransactions();
		StringBuilder out = new StringBuilder();
		out.append("Listing all transactions:\n");
		for(BankingTransactionLog t : ts) {
			out.append(t.toStringPretty()).append("\n\n");
		}
		out.append("end of list");
		PlayerUtils.sendMessage(playerState, out.toString());
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
