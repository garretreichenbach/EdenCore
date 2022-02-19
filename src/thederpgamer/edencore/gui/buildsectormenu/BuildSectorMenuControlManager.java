package thederpgamer.edencore.gui.buildsectormenu;

import api.common.GameClient;
import api.network.packets.PacketUtil;
import api.utils.gui.GUIControlManager;
import thederpgamer.edencore.network.client.RequestClientCacheUpdatePacket;
import thederpgamer.edencore.utils.DataUtils;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/22/2021]
 */
public class BuildSectorMenuControlManager extends GUIControlManager {

  public BuildSectorMenuControlManager() {
    super(GameClient.getClientState());
    DataUtils.getBuildSector(GameClient.getClientPlayerState().getName());
    PacketUtil.sendPacketToServer(new RequestClientCacheUpdatePacket());
  }

  @Override
  public BuildSectorMenuPanel createMenuPanel() {
    return new BuildSectorMenuPanel(getState());
  }
}
