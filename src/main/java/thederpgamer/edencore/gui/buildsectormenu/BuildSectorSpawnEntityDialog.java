package thederpgamer.edencore.gui.buildsectormenu;

import api.network.packets.PacketUtil;
import api.utils.gui.GUIInputDialog;
import org.schema.game.common.data.player.catalog.CatalogPermission;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.network.client.exchange.RequestSpawnEntryPacket;
import thederpgamer.edencore.network.client.misc.RequestClientCacheUpdatePacket;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/01/2022]
 */
public class BuildSectorSpawnEntityDialog extends GUIInputDialog {
	private final BuildSectorMenuPanel menuPanel;
	public BuildSectorData sectorData;
	public CatalogPermission catalogPermission;

	public BuildSectorSpawnEntityDialog(BuildSectorMenuPanel menuPanel) {
		this.menuPanel = menuPanel;
	}

	@Override
	public BuildSectorSpawnEntityPanel createPanel() {
		return new BuildSectorSpawnEntityPanel(getState(), this);
	}

	@Override
	public BuildSectorSpawnEntityPanel getInputPanel() {
		return (BuildSectorSpawnEntityPanel) super.getInputPanel();
	}

	@Override
	public void callback(GUIElement callingElement, MouseEvent mouseEvent) {
		if(!isOccluded() && mouseEvent.pressedLeftMouse()) {
			switch((String) callingElement.getUserPointer()) {
				case "X":
				case "CANCEL":
					PacketUtil.sendPacketToServer(new RequestClientCacheUpdatePacket());
					deactivate();
					break;
				case "OK":
					if(sectorData != null && catalogPermission != null) {
						String spawnName = (getInputPanel().getSpawnName().isEmpty()) ? catalogPermission.getUid() : getInputPanel().getSpawnName();
						PacketUtil.sendPacketToServer(new RequestSpawnEntryPacket(spawnName, catalogPermission.getUid(), getInputPanel().spawnDocked(), getInputPanel().spawnAsFaction(), sectorData.sector));
						deactivate();
					}
					break;
			}
		}
	}
}
