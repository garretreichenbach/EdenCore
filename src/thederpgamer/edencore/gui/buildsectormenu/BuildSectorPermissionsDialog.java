package thederpgamer.edencore.gui.buildsectormenu;

import api.network.packets.PacketUtil;
import api.utils.gui.GUIInputDialog;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.network.client.RequestClientCacheUpdatePacket;
import thederpgamer.edencore.network.client.UpdateBuildSectorPermissionsPacket;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/30/2021]
 */
public class BuildSectorPermissionsDialog extends GUIInputDialog {

  public BuildSectorData sectorData;
  public String targetName;

  @Override
  public BuildSectorPermissionsPanel createPanel() {
    return new BuildSectorPermissionsPanel(getState(), this);
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
          if (sectorData != null && targetName != null)
            PacketUtil.sendPacketToServer(
                new UpdateBuildSectorPermissionsPacket(sectorData, targetName));
          deactivate();
          break;
      }
    }
  }

  @Override
  public BuildSectorPermissionsPanel getInputPanel() {
    return (BuildSectorPermissionsPanel) super.getInputPanel();
  }
}
