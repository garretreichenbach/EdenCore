package soe.edencore.server;

import api.common.GameCommon;
import api.mod.ModSkeleton;
import api.mod.config.PersistentObjectUtil;
import soe.edencore.EdenCore;
import soe.edencore.data.PlayerData;
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

    public static PlayerData getPlayerData(String playerName) {
        ArrayList<Object> dataObjectList = new ArrayList<>();
        for(Object dataObject : dataObjectList) {
            PlayerData playerData = (PlayerData) dataObject;
            if(playerData.getPlayerName().equals(playerName)) return playerData;
        }
        return null;
    }

    public static PlayerData addNewPlayerData(String playerName) {
        PlayerData playerData = new PlayerData(GameCommon.getPlayerFromName(playerName));
        PermissionDatabase.generateDefaults(playerData);
        PersistentObjectUtil.addObject(instance, playerData);
        PersistentObjectUtil.save(instance);
        return playerData;
    }
}
