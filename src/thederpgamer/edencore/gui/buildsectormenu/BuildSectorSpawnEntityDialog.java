package thederpgamer.edencore.gui.buildsectormenu;

import api.network.packets.PacketUtil;
import api.utils.gui.GUIInputDialog;
import org.schema.game.common.data.player.catalog.CatalogPermission;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.network.client.RequestClientCacheUpdatePacket;
import thederpgamer.edencore.network.client.RequestSpawnEntryPacket;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/01/2022]
 */
public class BuildSectorSpawnEntityDialog extends GUIInputDialog {

  public BuildSectorData sectorData;
  public CatalogPermission catalogPermission;

  @Override
  public BuildSectorSpawnEntityPanel createPanel() {
    return new BuildSectorSpawnEntityPanel(getState(), this);
  }

  @Override
  public void callback(GUIElement callingElement, MouseEvent mouseEvent) {
    if (!isOccluded() && mouseEvent.pressedLeftMouse()) {
      switch ((String) callingElement.getUserPointer()) {
        case "X":
        case "CANCEL":
          PacketUtil.sendPacketToServer(new RequestClientCacheUpdatePacket());
          deactivate();
          break;
        case "OK":
          if (sectorData != null
              && catalogPermission != null
              && !getInputPanel().getSpawnName().isEmpty()) {
            PacketUtil.sendPacketToServer(
                new RequestSpawnEntryPacket(
                    getInputPanel().getSpawnName(),
                    catalogPermission.getUid(),
                    getInputPanel().spawnDocked(),
                    getInputPanel().spawnAsFaction()));
            deactivate();
          }
          break;
      }
    }
  }

  @Override
  public BuildSectorSpawnEntityPanel getInputPanel() {
    return (BuildSectorSpawnEntityPanel) super.getInputPanel();
  }
}
