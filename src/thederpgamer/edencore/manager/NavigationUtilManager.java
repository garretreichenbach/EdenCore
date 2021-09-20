package thederpgamer.edencore.manager;

import api.listener.Listener;
import api.listener.events.player.PlayerJoinWorldEvent;
import api.mod.StarLoader;
import api.network.packets.PacketUtil;
import api.utils.StarRunnable;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.SavedCoordinate;
import org.schema.game.server.controller.EntityNotFountException;
import org.schema.game.server.data.GameServerState;
import org.schema.schine.resource.tag.ListSpawnObjectCallback;
import org.schema.schine.resource.tag.Tag;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.commands.NavigationAdminCommand;
import thederpgamer.edencore.data.NavigationListContainer;
import thederpgamer.edencore.data.NavigationUtilPacket;
import thederpgamer.edencore.data.PlayerDataUtil;

import java.io.*;
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

    private HashMap<Long,SavedCoordinate> coordsAddList = new HashMap<>();
    private HashMap<Long,SavedCoordinate> coordsRemoveList = new HashMap<>();

    public NavigationUtilManager() {
        instance = this;
        NavigationListContainer c = NavigationListContainer.getContainer();
        if (c.coordsAddList != null) {
            coordsAddList = c.coordsAddList;
        }

        if (c.coordsRemoveList != null) {
            coordsRemoveList = c.coordsRemoveList;
        }

        PacketUtil.registerPacket(NavigationUtilPacket.class);
    //    addPlayerJoinEH();
        addAdminCommands();
    }

    private void addPlayerJoinEH() {
        StarLoader.registerListener(PlayerJoinWorldEvent.class, new Listener<PlayerJoinWorldEvent>() {
            @Override
            public void onEvent(PlayerJoinWorldEvent event) {
                final String playerName = event.getPlayerName();
                new StarRunnable(){
                    long max = System.currentTimeMillis()+120*1000;
                    PlayerState p;
                    @Override
                    public void run() {
                        if (max < System.currentTimeMillis()) {
                            cancel();
                        }
                        p = GameServerState.instance.getPlayerFromNameIgnoreCaseWOException(playerName);
                        if (p != null) {
                            updatePlayerList(p);
                            cancel();
                        }
                    }
                }.runTimer(EdenCore.getInstance(),10);
            }
        }, EdenCore.getInstance());
    }

    private void addAdminCommands() {
        StarLoader.registerCommand(new NavigationAdminCommand());
    }

    /**
     * can run client or serverside, will update the players saved list to match the managers lists.
     * @param player
     */
    public void updatePlayerList(PlayerState player) {
        if (player==null)
            return;

        //delete playercoords that are in removeList
        HashMap<Long,SavedCoordinate> playerCoords = new HashMap<>();
        for (SavedCoordinate c: player.getSavedCoordinates()) {
            playerCoords.put(c.getSector().code(),c);
        }
        for (SavedCoordinate c: coordsRemoveList.values()) {
            long code = c.getSector().code();
            if (playerCoords.containsKey(code)) {
                player.getSavedCoordinates().remove(code);
            }
        }

        //update existing customCoords
        HashSet<Long> existing = new HashSet<>();
        for (SavedCoordinate c: player.getSavedCoordinates()) {
            if (coordsAddList.containsKey(c.getSector().code())) {
                c.setName(coordsAddList.get(c.getSector().code()).getName());
                existing.add(c.getSector().code());
            }
        }

        //add all coords that dont exist yet in players list
        for (SavedCoordinate c: coordsAddList.values()) {
            if (existing.contains(c.getSector().code()))
                continue;

            player.getSavedCoordinates().add(c);
            existing.add(c.getSector().code());
        }
    }

    private void updateAllPlayers(HashMap<Long,SavedCoordinate> customCoords) {
        for(PlayerState p: GameServerState.instance.getPlayerStatesByName().values()) {
            updatePlayerList(p);
            synchToClient(p);
        }

        savePersistent();
    }

    public void updateAllPlayers() {
        updateAllPlayers(this.coordsAddList);
    }

    public void addCoordinateToList(Vector3i sector, String name) {
        name = "[p]"+name;
        coordsAddList.put(sector.code(),new SavedCoordinate(sector,name, false));

        updateAllPlayers(coordsAddList);
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
        coordsRemoveList.put(sector.code(),new SavedCoordinate(sector,name,true));
    }

    public void savePersistent() {
        NavigationListContainer c = NavigationListContainer.getContainer();
        c.coordsAddList = this.coordsAddList;
        c.coordsRemoveList = this.coordsRemoveList;
        c.save();
    }

    public HashMap<Long, SavedCoordinate> getCoordsAddList() {
        return coordsAddList;
    }

    public void setCoordsAddList(HashMap<Long, SavedCoordinate> coordsAddList) {
        this.coordsAddList = coordsAddList;
    }

    public HashMap<Long, SavedCoordinate> getCoordsRemoveList() {
        return coordsRemoveList;
    }

    public void setCoordsRemoveList(HashMap<Long, SavedCoordinate> coordsRemoveList) {
        this.coordsRemoveList = coordsRemoveList;
    }

    private void synchToClient(PlayerState p) {
        //send a packet
        PacketUtil.sendPacket(p,new NavigationUtilPacket(coordsAddList,coordsRemoveList));
    }

    /**
     * merges the adminCoords into the playerCoords, no doubles allowed, admin overwrites player.
     * mutates playerCoords.
     * @param adminCoords source to be merged into the second list
     * @param playerCoords receiver of merge
     */
    private void mergeLists(Collection<SavedCoordinate> adminCoords, Collection<SavedCoordinate> playerCoords) {
        HashMap<Long,SavedCoordinate> adminMap = listToSectorCodeMap(adminCoords); //=> will become new playerCoords list
        HashMap<Long,SavedCoordinate> playerMap = listToSectorCodeMap(playerCoords);

        Long sectorCode;
        for (Map.Entry<Long,SavedCoordinate> entry: playerMap.entrySet()) {
            sectorCode = entry.getKey();
            if (!adminMap.containsKey(sectorCode)) {
                //coord is not in admin map, add to map
                adminMap.put(entry.getKey(),entry.getValue());
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
    public void updatePlayerCoordsInSaveFile(String playername, Collection<SavedCoordinate> newCoords, HashSet<Long> blacklist) {
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
