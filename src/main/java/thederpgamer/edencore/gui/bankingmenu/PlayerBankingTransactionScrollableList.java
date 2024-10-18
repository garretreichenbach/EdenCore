package thederpgamer.edencore.gui.bankingmenu;

import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUIElementList;
import org.schema.schine.graphicsengine.forms.gui.newgui.ScrollableTableList;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.playerdata.PlayerData;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class PlayerBankingTransactionScrollableList extends ScrollableTableList<PlayerData.PlayerBankTransactionData> {
	
	private final GUIElement parent;
	private final PlayerData playerData;
	
	public PlayerBankingTransactionScrollableList(InputState state, GUIElement parent, PlayerData playerData) {
		super(state, 100, 100, parent);
		this.parent = parent;
		this.playerData = playerData;
	}

	@Override
	protected Collection<PlayerData.PlayerBankTransactionData> getElementList() {
		return Collections.unmodifiableCollection(playerData.getTransactionHistory());
	}

	@Override
	public void initColumns() {
		
	}
	
	@Override
	public void updateListEntries(GUIElementList guiElementList, Set<PlayerData.PlayerBankTransactionData> set) {

	}
}
