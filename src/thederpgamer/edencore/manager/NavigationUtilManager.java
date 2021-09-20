package thederpgamer.edencore.manager;

import api.mod.StarLoader;
import api.network.packets.PacketUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.SavedCoordinate;
import org.schema.game.server.controller.EntityNotFountException;
import org.schema.game.server.data.GameServerState;
import org.schema.schine.resource.tag.ListSpawnObjectCallback;
import org.schema.schine.resource.tag.Tag;
import thederpgamer.edencore.commands.NavigationAdminCommand;
import thederpgamer.edencore.data.NavigationListContainer;
import thederpgamer.edencore.data.PlayerDataUtil;

import java.io.*;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.*;

import static org.schema.common.util.linAlg.Vector3i.*;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 18.09.2021
 * TIME: 14:57
 * used to inject saved coords into players "saved coords" list, defined by admin commands, stored persistnetly
 */
public class NavigationUtilManager {
    public static NavigationUtilManager instance;

    private HashMap<Long,SavedCoordinate> coordsAddList = new HashMap<>();
    private HashSet<Long> coordsRemoveList = new HashSet<>();

    public NavigationUtilManager() {
        instance = this;
        NavigationListContainer c = NavigationListContainer.getContainer();
        if (c.coordsAddList != null) {
            coordsAddList = c.coordsAddList;
        }

        if (c.coordsRemoveList != null) {
            coordsRemoveList = c.coordsRemoveList;
        }

        addAdminCommands();
    }

    private void addAdminCommands() {
        StarLoader.registerCommand(new NavigationAdminCommand());
    }

    /**
     * will update all existing playerfiles by adding in the addList, removing the removeList, updating the names of already
     * existing addList coords.
     */
    public void updateAllPlayerFiles() {
        try {
            ArrayList<String> names = PlayerDataUtil.getAllPlayerNamesEver();
            for (String playerName: names) {
                NavigationUtilManager.instance.updatePlayerCoordsInSaveFile(playerName, coordsAddList,coordsRemoveList);
            }
            coordsRemoveList.clear(); //clears list, bc all players have been cleared.
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addCoordinateToList(Vector3i sector, String name) {
        name = "[p]"+name;
        coordsAddList.put(sector.code(),new SavedCoordinate(sector,name, false));
    }

    /**
     * adds coord to a list that will be removed upon joining from each players list.
     * @param sector
     * @param name exact name as logged for the players
     */
    public void removeCoordinateFromList(Vector3i sector, String name) {
        name = "[p]"+name;
        //remove from add list
        coordsAddList.remove(sector.code());
        //save into remove list
        coordsRemoveList.add(sector.code());
    }

    public void saveListsPersistent() {
        NavigationListContainer c = NavigationListContainer.getContainer();
        c.coordsAddList = this.coordsAddList;
        c.coordsRemoveList = this.coordsRemoveList;
        c.save();
    }

    public HashMap<Long, SavedCoordinate> getCoordsAddList() {
        return coordsAddList;
    }

    public HashSet<Long> getCoordsRemoveList() {
        return coordsRemoveList;
    }

    /**
     * merges the adminCoords into the playerCoords, no doubles allowed, admin overwrites player.
     * mutates playerCoords.
     * @param adminCoords source to be merged into the second list
     * @param playerCoords receiver of merge
     */
    private void mergeLists(HashMap<Long,SavedCoordinate> adminCoords, Collection<SavedCoordinate> playerCoords) {
        HashMap<Long,SavedCoordinate> adminMap = new HashMap<>(adminCoords);

        Long sectorCode;
        for (SavedCoordinate coordinate: playerCoords) {
            sectorCode = coordinate.getSector().code();
            if (!adminMap.containsKey(sectorCode)) {
                //coord is not in admin map, add to map
                adminMap.put(sectorCode,coordinate);
            }
        }

        //admin map now contains all admin set coords + all non admin coords from player
        playerCoords.clear();
        playerCoords.addAll(adminMap.values());
    }

    /**
     * maps sector code vs saved coord
     * @param coords
     * @return
     */
    private HashMap<Long,SavedCoordinate> listToSectorCodeMap(Collection<SavedCoordinate> coords) {
        HashMap<Long,SavedCoordinate> map = new HashMap<>(coords.size());
        for (SavedCoordinate c: coords) {
            map.put(c.getSector().code(),c);
        }
        return map;
    }

    /**
     * will add given coordinates to the savefile of this player. merges given newCoords into existing savefile.
     * existing newCoords in savefile will be overwritten.
     * @param playername
     */
    public void updatePlayerCoordsInSaveFile(String playername, HashMap<Long,SavedCoordinate> newCoords, HashSet<Long> blacklist) {
    //get tag
        Tag tag;
        try {
            tag = PlayerDataUtil.getTagFromFile(playername);
        } catch (IOException e) {
            e.printStackTrace();return;
        } catch (EntityNotFountException e) {
            e.printStackTrace();return;
        }

    //edit tag
        if (!(tag.getValue() instanceof Tag[]))
            return;

        Tag[] arr = (Tag[]) tag.getValue();

        final ObjectArrayList<SavedCoordinate> playersCoords = new ObjectArrayList<SavedCoordinate>();

        //save existing coords into "savedCoordinates" list
        Tag.listFromTagStructSPElimDouble(playersCoords, arr[23], new ListSpawnObjectCallback<SavedCoordinate>() {
            @Override
            public SavedCoordinate get(Object fromValue) {
                SavedCoordinate d = new SavedCoordinate();
                d.fromTagStructure((Tag) fromValue);
                return d;
            }
        });

        mergeLists(newCoords,playersCoords);
        for (SavedCoordinate c: playersCoords) {
            if (blacklist.contains(c.getSector().code()))
                playersCoords.remove(c);
        }

        arr[23] = Tag.listToTagStruct(playersCoords,null);

    //write tag
        try {
            PlayerDataUtil.writeTagForPlayer(tag,playername);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

    //control debug
        PlayerState schema = PlayerDataUtil.loadControlPlayer(playername);
    }
}
