package soe.edencore.server.permissions;

import soe.edencore.data.PlayerData;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * PermissionGroup.java
 * <Description>
 * ==================================================
 * Created 03/10/2021
 * @author TheDerpGamer
 */
public class PermissionGroup implements Serializable {

    private String groupName;
    private ArrayList<String> permissions;
    private ArrayList<PlayerData> members;
    private ArrayList<PermissionGroup> inheritedGroups;

    public PermissionGroup(String groupName) {
        this.groupName = groupName;
        this.permissions = new ArrayList<>();
        this.members = new ArrayList<>();
        this.inheritedGroups = new ArrayList<>();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<String> getPermissions() {
        for(PermissionGroup group : inheritedGroups) permissions.addAll(group.getPermissions());
        return permissions;
    }

    public ArrayList<PlayerData> getMembers() {
        return members;
    }

    public ArrayList<PermissionGroup> getInheritedGroups() {
        return inheritedGroups;
    }
}
