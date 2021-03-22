package soe.edencore.data.player;

import api.common.GameCommon;
import org.schema.game.common.data.player.faction.Faction;
import soe.edencore.server.ServerDatabase;
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
    private PlayerRank rank;
    private ArrayList<String> permissions;
    private ArrayList<PermissionGroup> groups;
    private long playTime;

    public PlayerData(String playerName) {
        this.playerName = playerName;
        this.rank = ServerDatabase.getDefaultRank();
        this.permissions = new ArrayList<>();
        this.groups = new ArrayList<>();
        this.playTime = 0;
    }

    public String getPlayerName() {
        return playerName;
    }

    public PlayerRank getRank() {
        return rank;
    }

    public void setRank(PlayerRank rank) {
        this.rank = rank;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getFactionName() {
        return (getFaction() == null) ? "No Faction" : getFaction().getName();
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
        if(permission.length > 1) {
            for(String perm : permission) {
                if(getPermissions().contains(perm)) return true;
            }
        }
        return getPermissions().contains(permission[0]);
    }

    public double getHoursPlayed() {
        return (double) playTime / (1000 * 60 * 60);
    }

    public void updatePlayTime(long timeSinceLastUpdate) {
        playTime += timeSinceLastUpdate;
    }
}
