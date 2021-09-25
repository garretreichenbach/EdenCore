package thederpgamer.edencore.navigation;

import api.mod.StarLoader;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.player.SavedCoordinate;
import org.schema.game.server.controller.EntityNotFountException;
import org.schema.schine.resource.tag.ListSpawnObjectCallback;
import org.schema.schine.resource.tag.Tag;
import thederpgamer.edencore.data.other.NavigationListContainer;
import thederpgamer.edencore.utils.PlayerDataUtil;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 18.09.2021
 * TIME: 14:57
 * used to inject saved coords into players "saved coords" list, defined by admin commands, stored persistnetly
 */
public class NavigationUtilManager {
    public static NavigationUtilManager instance;

    private HashMap<Long,MapMarker> publicMarkers = new HashMap<>();

    public NavigationUtilManager() {
        instance = this;
        loadFromPersistent();
        addAdminCommands();
    }

    private void loadFromPersistent() {
        NavigationListContainer c = NavigationListContainer.getContainer(true);
        if (c.publicMarkers != null) {
            publicMarkers = c.publicMarkers;
        }
    }

    private void addAdminCommands() {
        StarLoader.registerCommand(new NavigationAdminCommand());
    }

    public void addCoordinateToList(MapMarker marker) {
        publicMarkers.put(marker.sector.code(),marker);
        saveListsPersistent();
        synchPlayers();
    }

    /**
     * adds coord to a list that will be removed upon joining from each players list.
     * @param sector
     * @param name exact name as logged for the players
     */
    public void removeCoordinateFromList(Vector3i sector, String name) {
        name = "[p]"+name;
        //remove from add list
        publicMarkers.remove(sector.code());
        saveListsPersistent();
        synchPlayers();
    }

    public void saveListsPersistent() {
        NavigationListContainer c = NavigationListContainer.getContainer(true);
        c.publicMarkers = this.publicMarkers;
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
}
