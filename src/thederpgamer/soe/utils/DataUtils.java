package thederpgamer.soe.utils;

import api.common.GameClient;
import api.common.GameCommon;
import thederpgamer.soe.EdenCore;
import thederpgamer.soe.manager.LogManager;
import thederpgamer.soe.manager.MessageType;

/**
 * Contains misc mod data utilities.
 *
 * @author TheDerpGamer
 * @since 06/27/2021
 */
public class DataUtils {

    public static String getResourcesPath() {
        return EdenCore.getInstance().getSkeleton().getResourcesFolder().getPath().replace('\\', '/');
    }

    public static String getWorldDataPath() {
        String universeName = GameCommon.getUniqueContextId();
        if(!universeName.contains(":")) {
            return getResourcesPath() + "/data/" + universeName;
        } else {
            try {
                LogManager.logMessage(MessageType.ERROR, "Client " + GameClient.getClientPlayerState().getName() + " attempted to illegally access server data.");
            } catch(Exception ignored) { }
            return null;
        }
    }
}