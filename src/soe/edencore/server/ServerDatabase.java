package soe.edencore.server;

import api.mod.ModSkeleton;
import api.mod.config.PersistentObjectUtil;
import soe.edencore.EdenCore;
import soe.edencore.data.logs.AdminLogEntry;
import soe.edencore.data.player.PlayerData;
import soe.edencore.data.player.PlayerRank;
import soe.edencore.server.permissions.PermissionDatabase;
import java.util.ArrayList;

/**
 * ServerDatabase.java
 * <Description>
 *
 * @since 03/10/2021
 * @author TheDerpGamer
 */
public class ServerDatabase {

    private static final ModSkeleton instance = EdenCore.instance.getSkeleton();

    public static final PlayerRank defaultRank = new PlayerRank("Player", "&2[Player]");

    public static PlayerData getPlayerData(String playerName) {
        ArrayList<Object> dataObjectList = PersistentObjectUtil.getObjects(instance, PlayerData.class);
        for(Object dataObject : dataObjectList) {
            PlayerData playerData = (PlayerData) dataObject;
            if(playerData.getPlayerName().equals(playerName)) return playerData;
        }
        return null;
    }

    public static ArrayList<PlayerData> getAllPlayerData() {
        ArrayList<PlayerData> playerDataList = new ArrayList<>();
        ArrayList<Object> dataObjectList = PersistentObjectUtil.getObjects(instance, PlayerData.class);
        for(Object dataObject : dataObjectList) playerDataList.add((PlayerData) dataObject);
        return playerDataList;
    }

    public static PlayerData addNewPlayerData(String playerName) {
        PlayerData playerData = new PlayerData(playerName);
        PermissionDatabase.generateDefaults(playerData);
        PersistentObjectUtil.addObject(instance, playerData);
        return playerData;
    }

    public static void updatePlayerData(PlayerData playerData) {
        ArrayList<PlayerData> toRemove = new ArrayList<>();
        ArrayList<Object> dataObjectList = PersistentObjectUtil.getObjects(instance, PlayerData.class);
        for(Object dataObject : dataObjectList) {
            PlayerData pData = (PlayerData) dataObject;
            if(pData.getPlayerName().equals(playerData.getPlayerName())) toRemove.add(pData);
        }
        for(PlayerData remove : toRemove) PersistentObjectUtil.removeObject(instance, remove);
        PersistentObjectUtil.addObject(instance, playerData);
    }

    public static ArrayList<AdminLogEntry> getAdminLog() {
        ArrayList<AdminLogEntry> adminLog = new ArrayList<>();
        ArrayList<AdminLogEntry> toRemove = new ArrayList<>();
        ArrayList<Object> adminLogObjectList = PersistentObjectUtil.getObjects(instance, AdminLogEntry.class);
        for(Object adminLogObject : adminLogObjectList) {
            AdminLogEntry adminLogEntry = (AdminLogEntry) adminLogObject;
            if(adminLogEntry.getAge() > EdenCore.instance.maxLogAge) {
                toRemove.add(adminLogEntry);
            } else {
                adminLog.add(adminLogEntry);
            }
        }
        for(AdminLogEntry removeEntry : toRemove) PersistentObjectUtil.removeObject(instance, removeEntry);
        return adminLog;
    }
}
