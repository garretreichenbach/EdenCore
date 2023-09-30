package thederpgamer.edencore.commands.events;

import api.mod.StarMod;
import api.utils.game.chat.CommandInterface;
import org.jetbrains.annotations.Nullable;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.gui.eventeditor.EventEditor;

/**
 * Admin command for opening the Event Editor window.
 *
 * @author TheDerpGamer
 */
public class EventEditorCommand implements CommandInterface {
	@Override
	public String getCommand() {
		return "event_editor";
	}

	@Override
	public String[] getAliases() {
		return new String[] {
				"event_editor"
		};
	}

	@Override
	public String getDescription() {
		return "Opens the event editor.\n" +
				"- /%COMMAND% : Opens the event editor.\n" +
				"- /%COMMAND% <event_name> : Opens the editor for a specific event.";
	}

	@Override
	public boolean isAdminOnly() {
		return true;
	}

	@Override
	public boolean onCommand(PlayerState playerState, String[] strings) {
		if(strings.length <= 1) {
			if(strings.length == 1) EventEditor.open(strings[0], playerState);
			else EventEditor.open(playerState);
			return true;
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
