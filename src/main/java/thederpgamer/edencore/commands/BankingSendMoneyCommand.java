package thederpgamer.edencore.commands;

import api.mod.StarMod;
import api.utils.game.PlayerUtils;
import api.utils.game.chat.CommandInterface;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
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
		return "bank_send";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"bank_send"}; //dont question it, it wont show up in the commands list otherwise
	}

	@Override
	public String getDescription() {
		return "Sends money from your account to another player. Both players need to be online, and you need to have enough money in your account.\n" +
				"- /%COMMAND% <player> 500 <\"description\"> : Transfers funds from your account to another player.";
	}

	@Override
	public boolean isAdminOnly() {
		return false;
	}

	@Override
	public boolean onCommand(PlayerState sender, String[] strings) {
		// /bank send player_name amount mssg
		if(strings.length != 3) return false;
		String to = strings[0];
		int amount = 0;
		try {
			amount = Integer.parseInt(strings[1]);
		} catch(NumberFormatException ex) {
			return false;
		}
		String mssg = strings[2];
		BankingTransactionLog transactionLog = BankingTransactionLog.sendMoney(sender.getName(), to, amount, mssg);
		if(transactionLog == null) PlayerUtils.sendMessage(sender, "Unable to complete transaction. Either player does not exist or you don't have enough money.");
		else PlayerUtils.sendMessage(sender, transactionLog.toStringPretty());
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
