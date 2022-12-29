package thederpgamer.edencore.commands;

import api.mod.StarMod;
import api.utils.game.chat.CommandInterface;
import org.jetbrains.annotations.Nullable;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.manager.EventManager;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class EditEventCommand implements CommandInterface {
	@Override
	public String getCommand() {
		return "edit_event";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"edit_event"};
	}

	@Override
	public String getDescription() {
		return "Starts the event editor.\n" +
				"- /%COMMAND% <type> <pvp|pve> <event_name> : Starts the event editor for a new event of the specified type and name.\n" +
				"- /%COMMAND% <event_name> : Starts the event editor for the specified event.";
	}

	@Override
	public boolean isAdminOnly() {
		return true;
	}

	@Override
	public boolean onCommand(PlayerState sender, String[] args) {
		if(args.length == 1) {
			EventData eventData = EventManager.getEventByName(args[0]);
			if(eventData == null) return false;
			else EventManager.startEventEditor(sender, eventData);
		} else if(args.length == 3) {
			EventData eventData = EventManager.createEvent(args[0], args[1], args[2]);
			if(eventData == null) return false;
			else EventManager.startEventEditor(sender, eventData);
		} else return false;
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
