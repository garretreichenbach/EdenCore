package soe.edencore.server.permissions;

import api.mod.ModSkeleton;
import api.mod.config.PersistentObjectUtil;
import soe.edencore.EdenCore;
import soe.edencore.data.PlayerData;
import java.util.Arrays;

/**
 * PermissionDatabase.java
 * <Description>
 * ==================================================
 * Created 03/10/2021
 * @author TheDerpGamer
 */
public class PermissionDatabase {

    private static final ModSkeleton instance = EdenCore.instance.getSkeleton();

    public static void generateDefaults(PlayerData playerData) {
        playerData.getPermissions().addAll(Arrays.asList(EdenCore.instance.defaultPermissions));
        PersistentObjectUtil.addObject(instance, playerData);
        PersistentObjectUtil.save(instance);
    }
}
