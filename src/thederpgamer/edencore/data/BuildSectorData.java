package thederpgamer.edencore.data;

import org.schema.common.util.linAlg.Vector3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <Description>
 *
 * @version 2.0 - [09/07/2021]
 * @author TheDerpGamer
 */
public class BuildSectorData implements ComparableData {

    public String ownerId;
    public String ownerName;
    public Vector3i sector;
    public HashMap<String, HashMap<String, Boolean>> permissions;
    public boolean allAIDisabled = false;

    public BuildSectorData(String ownerName, String ownerId, Vector3i sector, HashMap<String, HashMap<String, Boolean>> permissions) {
        this.ownerName = ownerName;
        this.ownerId = ownerId;
        this.sector = sector;
        this.permissions = permissions;
    }

    @Override
    public boolean equalTo(ComparableData data) {
        if(data instanceof BuildSectorData) {
            BuildSectorData sectorData = (BuildSectorData) data;
            return sectorData.ownerId.equals(ownerId) && sectorData.ownerName.equals(ownerName) && sector.equals(sectorData.sector);
        } else return false;
    }

    public void addPlayer(String player) {
        permissions.remove(player);
        if(player.toLowerCase().equals(ownerName.toLowerCase())) permissions.put(player, getOwnerPermissions());
        else permissions.put(player, getDefaultPermissions());
    }

    public void removePlayer(String player) {
        addPlayer(player);
        denyPermission(player, "JOIN");
    }

    public boolean hasPermission(String player, String permission) {
        if(!permissions.containsKey(player)) {
            denyPermission(player, "JOIN");
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
            if(entry.getValue().get("JOIN")) allowedPlayers.add(entry.getKey());
        }
        return allowedPlayers;
    }

    public HashMap<String, Boolean> getPermissions(String player) {
        if(!permissions.containsKey(player)) {
            addPlayer(player);
            denyPermission(player, "JOIN");
        }
        return permissions.get(player);
    }

    public static HashMap<String, Boolean> getDefaultPermissions() {
        HashMap<String, Boolean> defaultPermissions = new HashMap<>();
        defaultPermissions.put("JOIN", true);
        defaultPermissions.put("EDIT", false);
        defaultPermissions.put("SPAWN", false);
        defaultPermissions.put("SPAWN_ENEMIES", false);
        defaultPermissions.put("DELETE", false);
        defaultPermissions.put("TOGGLE_AI", false);
        return defaultPermissions;
    }

    public static HashMap<String, Boolean> getOwnerPermissions() {
        HashMap<String, Boolean> ownerPermissions = new HashMap<>();
        ownerPermissions.put("JOIN", true);
        ownerPermissions.put("EDIT", true);
        ownerPermissions.put("SPAWN", true);
        ownerPermissions.put("SPAWN_ENENMIES", true);
        ownerPermissions.put("DELETE", true);
        ownerPermissions.put("TOGGLE_AI", true);
        return ownerPermissions;
    }
}
