package thederpgamer.edencore.commands;

import api.mod.StarMod;
import api.mod.config.PersistentObjectUtil;
import api.utils.game.chat.CommandInterface;
import org.jetbrains.annotations.Nullable;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.player.PlayerData;
import thederpgamer.edencore.utils.DataUtils;
import thederpgamer.edencore.utils.PlayerDataUtil;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class SetDonatorCommand implements CommandInterface {
	@Override
	public String getCommand() {
		return "set_donator";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"set_donator"};
	}

	@Override
	public String getDescription() {
		return "Sets a player to be a donator. Note this is a temporary solution until the Discord integration is fixed.\n" + " - /%COMMAND% <player> <type> : Sets a player to be a donator.";
	}

	@Override
	public boolean isAdminOnly() {
		return true;
	}

	@Override
	public boolean onCommand(PlayerState playerState, String[] strings) {
		if(strings.length == 2) {
			try {
				String playerName = strings[0];
				String type = strings[1];
				PlayerData playerData = DataUtils.getPlayerDataByName(playerName);
				if(playerData == null) playerData = DataUtils.getPlayerData(PlayerDataUtil.loadControlPlayer(playerName));
				switch(type.toUpperCase()) {
					default:
					case "NONE":
						playerData.donatorType = PlayerData.NONE;
						break;
					case "EXPLORER":
						playerData.donatorType = PlayerData.EXPLORER;
						break;
					case "CAPTAIN":
						playerData.donatorType = PlayerData.CAPTAIN;
						break;
					case "STAFF":
						playerData.donatorType = PlayerData.STAFF;
						break;
				}
				PersistentObjectUtil.save(EdenCore.getInstance().getSkeleton());
				return true;
			} catch(Exception exception) {
				exception.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void serverAction(@Nullable PlayerState playerState, String[] strings) {
	}

	@Override
	public StarMod getMod() {
		return EdenCore.getInstance();
	}
}
