package thederpgamer.edencore.data.other;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.common.util.linAlg.Vector3i;
import thederpgamer.edencore.manager.LogManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores build sector information as persistent data.
 *
 * @version 2.0 - [09/07/2021]
 * @author TheDerpGamer
 */
public class BuildSectorData {

    public String ownerName;
    public Vector3i sector;
    public HashMap<String, HashMap<String, Boolean>> permissions;
    public boolean allAIDisabled;

    public BuildSectorData(String ownerName, Vector3i sector, HashMap<String, HashMap<String, Boolean>> permissions) {
        this.ownerName = ownerName;
        this.sector = sector;
        this.permissions = permissions;
        this.allAIDisabled = true;
        this.addPlayer(ownerName);
    }

    public BuildSectorData(PacketReadBuffer packetReadBuffer) throws IOException {
        deserialize(packetReadBuffer);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof BuildSectorData && ((BuildSectorData) object).ownerName.equals(ownerName) && ((BuildSectorData) object).sector.equals(sector);
    }

    public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
        writeBuffer.writeString(ownerName);
        writeBuffer.writeVector(sector);
        writeBuffer.writeInt(permissions.keySet().size());
        for(Map.Entry<String, HashMap<String, Boolean>> userMapEntry : permissions.entrySet()) {
            writeBuffer.writeString(userMapEntry.getKey()); //Write player name
            writeBuffer.writeInt(userMapEntry.getValue().size());
            for(Map.Entry<String, Boolean> permissionEntry : userMapEntry.getValue().entrySet()) {
                writeBuffer.writeString(permissionEntry.getKey()); //Write permission
                writeBuffer.writeBoolean(permissionEntry.getValue()); //Write value
            }
        }
        writeBuffer.writeBoolean(allAIDisabled);
    }

    public void deserialize(PacketReadBuffer readBuffer) throws IOException {
        if(permissions == null) permissions = new HashMap<>();
        ownerName = readBuffer.readString();
        sector = readBuffer.readVector();
        permissions = new HashMap<>();
        int userLength = readBuffer.readInt();
        if(userLength > 0) {
            for(int u = 0; u < userLength; u ++) {
                try {
                    String userName = readBuffer.readString();
                    int permMapSize = readBuffer.readInt();
                    if(permMapSize > 0) {
                        HashMap<String, Boolean> permMap = new HashMap<>();
                        for(int p = 0; p < permMapSize; p ++) {
                            try {
                                String permission = readBuffer.readString();
                                boolean value = readBuffer.readBoolean();
                                permMap.put(permission, value);
                            } catch(Exception exception) {
                                LogManager.logException("Failed to deserialize build sector permissions", exception);
                            }
                        }
                        permissions.put(userName, permMap);
                    }
                } catch(Exception exception) {
                    LogManager.logException("Failed to deserialize build sector permissions", exception);
                }
            }
        }
        allAIDisabled = readBuffer.readBoolean();
    }

    public void addPlayer(String player) {
        permissions.remove(player);
        if(player.equals(ownerName)) permissions.put(player, getOwnerPermissions());
        else permissions.put(player, getDefaultPermissions());
    }

    public void removePlayer(String player) {
        addPlayer(player);
        denyPermission(player, "ENTER");
    }

    public boolean hasPermission(String player, String permission) {
        if(permissions == null) permissions = new HashMap<>();
        if(!permissions.containsKey(player)) {
            if(!player.equals(ownerName)) denyPermission(player, "ENTER");
            else addPlayer(player);
            return false;
        } else return permissions.get(player).get(permission.toUpperCase());
    }

    public void allowPermission(String player, String permission) {
        if(!permissions.containsKey(player)) addPlayer(player);
        if(permissions.get(player).containsKey(permission.toUpperCase())) {
            permissions.get(player).remove(permission.toUpperCase());
            permissions.get(player).put(permission.toUpperCase(), true);
        }
    }

    public void denyPermission(String player, String permission) {
        if(!permissions.containsKey(player)) addPlayer(player);
        if(permissions.get(player).containsKey(permission.toUpperCase())) {
            permissions.get(player).remove(permission.toUpperCase());
            permissions.get(player).put(permission.toUpperCase(), false);
        }
    }

    public ArrayList<String> getAllowedPlayersByName() {
        ArrayList<String> allowedPlayers = new ArrayList<>();
        for(Map.Entry<String, HashMap<String, Boolean>> entry : permissions.entrySet()) {
            if(entry.getValue().get("ENTER")) allowedPlayers.add(entry.getKey());
        }
        return allowedPlayers;
    }

    public HashMap<String, Boolean> getPermissions(String player) {
        if(!permissions.containsKey(player)) {
            addPlayer(player);
            denyPermission(player, "ENTER");
        }
        return permissions.get(player);
    }

    public static HashMap<String, Boolean> getDefaultPermissions() {
        HashMap<String, Boolean> defaultPermissions = new HashMap<>();
        defaultPermissions.put("ENTER", true);
        defaultPermissions.put("EDIT", false);
        defaultPermissions.put("PICKUP", false);
        defaultPermissions.put("SPAWN", false);
        defaultPermissions.put("SPAWN_ENEMIES", false);
        defaultPermissions.put("DELETE", false);
        defaultPermissions.put("TOGGLE_AI", false);
        return defaultPermissions;
    }

    public static HashMap<String, Boolean> getOwnerPermissions() {
        HashMap<String, Boolean> ownerPermissions = new HashMap<>();
        ownerPermissions.put("ENTER", true);
        ownerPermissions.put("EDIT", true);
        ownerPermissions.put("PICKUP", true);
        ownerPermissions.put("SPAWN", true);
        ownerPermissions.put("SPAWN_ENEMIES", true);
        ownerPermissions.put("DELETE", true);
        ownerPermissions.put("TOGGLE_AI", true);
        return ownerPermissions;
    }
}
