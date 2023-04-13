package thederpgamer.edencore.gui.misc;

import api.common.GameClient;
import org.schema.game.client.view.gui.playerstats.PlayerStatisticsScrollableListNew;
import org.schema.game.common.data.player.PlayerState;
import org.schema.schine.graphicsengine.forms.gui.GUIAncor;
import org.schema.schine.input.InputState;
import org.schema.schine.network.RegisteredClientOnServer;
import thederpgamer.edencore.EdenCore;

import java.util.ArrayList;
import java.util.Collection;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class NewPlayerStatisticsScrollableListNew extends PlayerStatisticsScrollableListNew {
	public NewPlayerStatisticsScrollableListNew(InputState inputState, GUIAncor guiAncor) {
		super(inputState, guiAncor);
	}

	@Override
	public Collection<PlayerState> getElementList() {
		ArrayList<PlayerState> playerStates = (ArrayList<PlayerState>) super.getElementList();
		for(RegisteredClientOnServer client : EdenCore.getFakePlayers()) {
			if(client.getPlayerObject() instanceof PlayerState) playerStates.add((PlayerState) client.getPlayerObject());
		}
		return playerStates;
	}
}
