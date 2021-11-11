package thederpgamer.edencore.gui.eventsmenu;

import api.common.GameClient;
import api.network.packets.PacketUtil;
import api.utils.gui.GUIControlManager;
import thederpgamer.edencore.network.client.RequestClientCacheUpdatePacket;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/08/2021]
 */
public class EventsMenuControlManager extends GUIControlManager {

    public EventsMenuControlManager() {
        super(GameClient.getClientState());
        PacketUtil.sendPacketToServer(new RequestClientCacheUpdatePacket());
    }

    @Override
    public EventsMenuPanel createMenuPanel() {
        return new EventsMenuPanel(getState());
    }
}
