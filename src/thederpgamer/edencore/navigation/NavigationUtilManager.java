package thederpgamer.edencore.navigation;

import api.mod.StarLoader;
import api.network.packets.PacketUtil;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.network.client.NavigationMapPacket;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 18.09.2021
 * TIME: 14:57
 * used to inject saved coords into players "saved coords" list, defined by admin commands, stored persistnetly
 * fully serverside
 */
public class NavigationUtilManager {
    public static NavigationUtilManager instance;

    private HashMap<Long,MapMarker> publicMarkers = new HashMap<>();

    public NavigationUtilManager() {
        instance = this;
        loadFromPersistent();
        addAdminCommands();
        NavigationEventManager.serverInit();
    }

    private void loadFromPersistent() {
        NavigationListContainer c = NavigationListContainer.getContainer(true);
        ArrayList<MapMarker> markers = new ArrayList<>();
        c.getPublicMarkers(markers);
        for (MapMarker m: markers) {
            if (m.sector == null)
                continue;
            publicMarkers.put(m.sector.code(),m);
        }
    }

    private void addAdminCommands() {
        StarLoader.registerCommand(new NavigationAdminCommand());
    }

    /**
     * add marker to list.
     * requires synching and saving to have effect
     * @param marker
     */
    public void addCoordinateToList(MapMarker marker) {
        publicMarkers.put(marker.sector.code(),marker);
    }

    /**
     * adds coord to a list that will be removed upon joining from each players list.
     * requires synching and saving to have effect
     * @param sector
     */
    public void removeCoordinateFromList(Vector3i sector) {
        //remove from add list
        publicMarkers.remove(sector.code());

    }

    public void saveListsPersistent() {
        NavigationListContainer c = NavigationListContainer.getContainer(true);
        c.setPublicMarkers(publicMarkers.values());
        c.save();
    }

    public HashMap<Long, MapMarker> getPublicMarkers() {
        return publicMarkers;
    }

    /**
     * synch the public markers that players get displayed with the servers. (only server->client)
     */
    public void synchPlayers() {
        new NavigationMapPacket(publicMarkers.values()).sendToAllServer();
    }
    public void synchPlayer(PlayerState p) {
        PacketUtil.sendPacket(p,new NavigationMapPacket(publicMarkers.values()));
    }
}
