package thederpgamer.edencore.commands;

import api.mod.StarMod;
import api.utils.game.chat.CommandInterface;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;

import javax.annotation.Nullable;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class GuideCommand implements CommandInterface {
	@Override
	public String getCommand() {
		return "guide";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"guide, glossar, glossary"};
	}

	@Override
	public String getDescription() {
		return "Opens the guide menu.";
	}

	@Override
	public boolean isAdminOnly() {
		return false;
	}

	@Override
	public boolean onCommand(PlayerState playerState, String[] strings) {
		EdenCore.getInstance().activateGuideMenuForPlayer(playerState);
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
