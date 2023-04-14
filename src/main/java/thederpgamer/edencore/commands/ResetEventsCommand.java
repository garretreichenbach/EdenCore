package thederpgamer.edencore.commands;

import api.mod.StarMod;
import api.utils.game.chat.CommandInterface;
import org.jetbrains.annotations.Nullable;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.manager.EventManager;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class ResetEventsCommand implements CommandInterface {
	@Override
	public String getCommand() {
		return "reset_events";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"reset_events"};
	}

	@Override
	public String getDescription() {
		return "Regenerates all server events.\n" + "- /%COMMAND% : Regenerates all server events.";
	}

	@Override
	public boolean isAdminOnly() {
		return true;
	}

	@Override
	public boolean onCommand(PlayerState playerState, String[] strings) {
		EventManager.forceGen();
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
