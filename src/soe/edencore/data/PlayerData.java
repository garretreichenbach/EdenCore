package soe.edencore.data;

import api.common.GameCommon;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.faction.Faction;
import soe.edencore.server.permissions.PermissionGroup;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * PlayerData.java
 * <Description>
 *
 * @since 03/10/2021
 * @author TheDerpGamer
 */
public class PlayerData implements Serializable {

    private String playerName;
    private ArrayList<String> permissions;
    private ArrayList<PermissionGroup> groups;

    public PlayerData(PlayerState playerState) {
        this.playerName = playerState.getName();
        this.permissions = new ArrayList<>();
        this.groups = new ArrayList<>();
    }

    public String getPlayerName() {
        return playerName;
    }

    public Faction getFaction() {
        if(inFaction()) {
            return GameCommon.getGameState().getFactionManager().getFaction(GameCommon.getPlayerFromName(playerName).getFactionId());
        } else {
            return null;
        }
    }

    public boolean inFaction() {
        return GameCommon.getPlayerFromName(playerName).getFactionId() != 0;
    }

    public ArrayList<PermissionGroup> getGroups() {
        return groups;
    }

    public void addGroup(PermissionGroup group) {
        group.getMembers().add(this);
        groups.add(group);
    }

    public void removeGroup(PermissionGroup group) {
        group.getMembers().remove(this);
        groups.remove(group);
    }

    public ArrayList<String> getPermissions() {
        for(PermissionGroup group : getGroups()) permissions.addAll(group.getPermissions());
        return permissions;
    }

    public boolean hasPermission(String... permission) {
        return getPermissions().contains(permission);
    }
}
