package thederpgamer.edencore.data.event;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/13/2021]
 */
public class SquadData {

    public SquadMemberData[] squadMembers;

    public SquadData() {

    }

    public boolean ready() {
        for(SquadMemberData memberData : squadMembers) if(!memberData.ready) return false;
        return true;
    }
}
