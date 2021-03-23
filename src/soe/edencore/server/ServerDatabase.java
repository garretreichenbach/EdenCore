package soe.edencore.server;

import api.mod.ModSkeleton;
import api.mod.config.PersistentObjectUtil;
import org.schema.schine.graphicsengine.forms.gui.newgui.ScrollableTableList;
import soe.edencore.EdenCore;
import soe.edencore.data.logs.AdminLogEntry;
import soe.edencore.data.player.PlayerData;
import soe.edencore.data.player.PlayerRank;
import soe.edencore.server.permissions.PermissionGroup;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * ServerDatabase.java
 * <Description>
 *
 * @since 03/10/2021
 * @author TheDerpGamer
 */
public class ServerDatabase {

    private static final ModSkeleton instance = EdenCore.instance.getSkeleton();
    public static ArrayList<ScrollableTableList<?>> guiLists = new ArrayList<>();

    private static PlayerRank defaultRank;

    public static void updateGUIs() {
        for(ScrollableTableList<?> guiList : guiLists) {
            try {
                guiList.flagDirty();
                guiList.handleDirty();
            } catch(Exception ignored) { }
        }
    }

    public static PlayerRank getDefaultRank() {
        if(defaultRank == null) {
            defaultRank = new PlayerRank("Player", "&2[Player]", PlayerRank.RankType.PLAYER);
            addRank(defaultRank);
        }
        return defaultRank;
    }

    public static boolean rankExists(String rankName) {
        ArrayList<PlayerRank> rankList = getAllRanks();
        for(PlayerRank playerRank : rankList) {
            if(playerRank.rankName.equals(rankName.toLowerCase())) return true;
        }
        return false;
    }

    public static void addRank(PlayerRank playerRank) {
        ArrayList<PlayerRank> toRemove = new ArrayList<>();
        ArrayList<Object> rankObjectList = PersistentObjectUtil.getObjects(instance, PlayerRank.class);
        for(Object rankObject : rankObjectList) {
            PlayerRank pRank = (PlayerRank) rankObject;
            if(pRank.rankName.equals(playerRank.rankName)) toRemove.add(pRank);
        }
        for(PlayerRank remove : toRemove) PersistentObjectUtil.removeObject(instance, remove);
        PersistentObjectUtil.addObject(instance, playerRank);
    }

    public static void removeRank(PlayerRank playerRank) {
        ArrayList<PlayerData> playerDataList = getAllPlayerData();
        for(PlayerData playerData : playerDataList) {
            if(playerData.getRank().rankName.equals(playerRank.rankName)) {
                playerData.setRank(getDefaultRank());
                updatePlayerData(playerData);
            }
        }

        ArrayList<PlayerRank> toRemove = new ArrayList<>();
        ArrayList<Object> rankObjectList = PersistentObjectUtil.getObjects(instance, PlayerRank.class);
        for(Object rankObject : rankObjectList) {
            PlayerRank pRank = (PlayerRank) rankObject;
            if(pRank.rankName.equals(playerRank.rankName)) toRemove.add(pRank);
        }
        for(PlayerRank remove : toRemove) PersistentObjectUtil.removeObject(instance, remove);
    }

    public static ArrayList<PlayerRank> getAllRanks() {
        ArrayList<PlayerRank> rankList = new ArrayList<>();
        ArrayList<Object> rankObjectList = PersistentObjectUtil.getObjects(instance, PlayerRank.class);
        for(Object rankObject : rankObjectList) rankList.add((PlayerRank) rankObject);
        return rankList;
    }

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
        playerData.getPermissions().addAll(Arrays.asList(EdenCore.instance.defaultPermissions));
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

    public static ArrayList<PermissionGroup> getAllGroups() {
        ArrayList<PermissionGroup> groupList = new ArrayList<>();
        ArrayList<Object> groupObjectList = PersistentObjectUtil.getObjects(instance, PermissionGroup.class);
        for(Object groupObject : groupObjectList) groupList.add((PermissionGroup) groupObject);
        return groupList;
    }

    public static void updateGroup(PermissionGroup group) {
        ArrayList<PermissionGroup> toRemove = new ArrayList<>();
        ArrayList<Object> groupObjectList = PersistentObjectUtil.getObjects(instance, PermissionGroup.class);
        for(Object groupObject : groupObjectList) {
            PermissionGroup permGroup = (PermissionGroup) groupObject;
            if(permGroup.getGroupName().equals(group.getGroupName())) toRemove.add(permGroup);
        }
        for(PermissionGroup remove : toRemove) PersistentObjectUtil.removeObject(instance, remove);
        PersistentObjectUtil.addObject(instance, group);
    }

    public static void removeGroup(PermissionGroup group) {
        for(PlayerData playerData : group.getMembers()) {
            playerData.removeGroup(group);
            updatePlayerData(playerData);
        }
        group.getMembers().clear();

        ArrayList<PermissionGroup> toRemove = new ArrayList<>();
        ArrayList<Object> groupObjectList = PersistentObjectUtil.getObjects(instance, PermissionGroup.class);
        for(Object groupObject : groupObjectList) {
            PermissionGroup permGroup = (PermissionGroup) groupObject;
            if(permGroup.getGroupName().equals(group.getGroupName())) toRemove.add(permGroup);
        }
        for(PermissionGroup remove : toRemove) PersistentObjectUtil.removeObject(instance, remove);
        PersistentObjectUtil.removeObject(instance, group);
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
