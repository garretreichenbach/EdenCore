package thederpgamer.edencore.utils;

import api.common.GameCommon;
import api.common.GameServer;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.server.data.GameServerState;
import org.schema.schine.resource.FileExt;
import org.schema.schine.resource.tag.Tag;
import thederpgamer.edencore.manager.LogManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/19/2021]
 */
public class ServerDatabase {

    public enum EntryType {
        PLAYER_STATE("ENTITY_PLAYERSTATE");

        public String databasePrefix;

        EntryType(String databasePrefix) {
            this.databasePrefix = databasePrefix;
        }
    }

    /**
     * Searches for a player by name on server. If the player isn't online, searches the server database for a matching entry.
     * @param playerName The player's name
     * @return The player state
     */
    public static PlayerState getPlayerByName(String playerName) {
        assert GameCommon.isOnSinglePlayer() || GameCommon.isDedicatedServer();
        PlayerState playerState = GameServer.getServerState().getPlayerStatesByName().get(playerName);
        if(playerState == null) {
            try {
                HashMap<String, Tag> entries = getEntriesOfType(EntryType.PLAYER_STATE);
                if(!entries.isEmpty() && entries.containsKey(playerName)) {
                    playerState = new PlayerState(GameServer.getServerState());
                    playerState.initialize();
                    playerState.fromTagStructure(entries.get(playerName));
                    playerState.setName(playerName);
                }
            } catch(Exception exception) {
                LogManager.logException("Encountered an exception while trying to fetch a player from database", exception);
            }
        }
        return playerState;
    }

    public static ArrayList<PlayerState> getAllPlayers() {
        assert GameCommon.isOnSinglePlayer() || GameCommon.isDedicatedServer();
        ArrayList<PlayerState> allPlayers = new ArrayList<>(GameServer.getServerState().getPlayerStatesByName().values());
        try {
            HashMap<String, Tag> entries = getEntriesOfType(EntryType.PLAYER_STATE);
            for(Map.Entry<String, Tag> entry : entries.entrySet()) {
                PlayerState playerState = new PlayerState(GameServer.getServerState());
                playerState.initialize();
                playerState.fromTagStructure(entry.getValue());
                playerState.setName(entry.getKey());
                if(!allPlayers.contains(playerState)) allPlayers.add(playerState);
            }
        } catch(Exception exception) {
            LogManager.logException("Encountered an exception while trying to fetch players from database", exception);
        }
        return allPlayers;
    }

    public static HashMap<String, Tag> getEntriesOfType(final EntryType entryType) {
        assert GameCommon.isOnSinglePlayer() || GameCommon.isDedicatedServer();
        File databaseFolder = new FileExt(GameServerState.ENTITY_DATABASE_PATH);
        if(databaseFolder.exists()) {
            try {
                File[] listFiles = databaseFolder.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File arg0, String name) {
                        return name.startsWith(entryType.databasePrefix);
                    }
                });

                if(listFiles != null) {
                    HashMap<String, Tag> entryMap = new HashMap<>();
                    for(File listFile : listFiles) {
                        String name = listFile.getName().substring(entryType.databasePrefix.length(), listFile.getName().lastIndexOf("."));
                        Tag tag = Tag.readFrom(new BufferedInputStream(new FileInputStream(listFile)), true, false);
                        entryMap.put(name, tag);
                    }
                    return entryMap;
                }
            } catch(Exception exception) {
                LogManager.logException("Encountered an exception while trying to fetch server database entries", exception);
            }
        }
        return new HashMap<>();
    }
}