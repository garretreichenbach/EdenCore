package thederpgamer.edencore.data.event.types.defense;

import api.common.GameServer;
import api.mod.config.PersistentObjectUtil;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.player.faction.Faction;
import org.schema.game.common.data.world.Sector;
import org.schema.game.common.data.world.Universe;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.data.event.EventTarget;
import thederpgamer.edencore.data.event.SquadData;
import thederpgamer.edencore.data.event.target.DefenseTarget;
import thederpgamer.edencore.utils.EventUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/19/2021]
 */
public class DefenseEvent extends EventData {

    public DefenseEvent(String name, String description, EventTarget... targets) {
        super(name, description, EventType.DEFENSE, targets);
    }

    public DefenseEvent(PacketReadBuffer readBuffer) throws IOException {
        super(readBuffer);
    }

	@Override
    public void deserialize(PacketReadBuffer readBuffer) throws IOException {
        //eventType = EventType.values()[readBuffer.readInt()];
        id = readBuffer.readLong();
        name = readBuffer.readString();
        description = readBuffer.readString();
        int size = readBuffer.readInt();
        if(size > 0) {
            targets = new EventTarget[size];
            for(int i = 0; i < size; i ++) targets[i] = new DefenseTarget(readBuffer);
        }
        squadData = new SquadData(readBuffer);
    }

    @Override
    public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
        writeBuffer.writeInt(eventType.ordinal());
        writeBuffer.writeLong(id);
        writeBuffer.writeString(name);
        writeBuffer.writeString(description);
        writeBuffer.writeInt(targets.length);
        if(targets.length > 0) for(EventTarget target : targets) target.serialize(writeBuffer);
        squadData.serialize(writeBuffer);
    }

    @Override
    public void initializeEvent() {
        squadData = new SquadData();
        for(EventTarget target : targets) {
            if(target instanceof DefenseTarget) {
                DefenseTarget defenseTarget = (DefenseTarget) target;
            }
        }
    }

    @Override
    public String getAnnouncement() {
        return "A new Defense Event is starting [" + name + "]";
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void transition() {
        if(squadData == null) {
            squadData = new SquadData();
            status = WAITING;
        }

        switch(status) {
            case NONE:
                status = WAITING;
                initializeEvent();
                break;
            case WAITING:
                if(squadData.ready()) {
                    status = ACTIVE;
                    start();
                }
                break;
            case ACTIVE:
                break;
        }
    }

    @Override
    public void start() {
        //Pick a random far away sector to set up the event in
        Vector3i coords = EventUtils.getRandomSector();
        try {
            //Load the sector
            Universe universe = GameServer.getServerState().getUniverse();
            universe.loadOrGenerateSector(coords);
            Sector sector = universe.getSector(coords);
            if(sector.isTutorialSector() || sector.isPersonalOrTestSector()) {
                EventUtils.logEventMessage(this, "[CATASTROPHIC FAILURE]: Somehow, despite all odds, the event sector " + coords + " is a tutorial or personal sector. This should be statistically impossible.");
                throw new IllegalStateException("Event sector " + coords + " is a tutorial or personal sector. This should be statistically impossible.");
            }

            //Set up the factions
            Faction blueTeam = EventUtils.getBlueTeam();
            //Faction redTeam = EventUtils.getRedTeam();
            //We don't need a red team for now, as the events are currently only PVE. When PVP events are added, the above will be needed.
            EventUtils.setTeam(squadData, blueTeam); //Temporarily change the faction of all the squad members to the blue team for the event.

            //Set up the targets
            for(EventTarget target : targets) {
                if(target instanceof DefenseTarget) {
                    DefenseTarget defenseTarget = (DefenseTarget) target;

                }
            }
        } catch(IOException | SQLException exception) {
            exception.printStackTrace();
            EventUtils.cancelEvent(this);
        }
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void end() {

    }

    public static EventData getRandom() {
        ArrayList<DefenseEvent> defenseEvents = new ArrayList<>();
        for(Object obj : PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(), DefenseEvent.class)) {
            if(obj instanceof DefenseEvent) defenseEvents.add((DefenseEvent) obj);
        }
        if(defenseEvents.size() > 0) return defenseEvents.get((int) (Math.random() * defenseEvents.size()));
        else return null;
    }

    public static DefenseEvent create(String combatType, String name) {
        DefenseEvent defenseEvent = new DefenseEvent(name, "No description");
        defenseEvent.code = (combatType.equalsIgnoreCase("pvp") ? 1 : 0) | EventData.DAILY; //Todo: Add weekly and monthly events
        PersistentObjectUtil.addObject(EdenCore.getInstance().getSkeleton(), defenseEvent);
        PersistentObjectUtil.save(EdenCore.getInstance().getSkeleton());
        return defenseEvent;
    }
}
