package thederpgamer.edencore.data.event.types.defense;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.data.event.EventEnemyData;
import thederpgamer.edencore.data.event.EventTarget;
import thederpgamer.edencore.data.event.SquadData;
import thederpgamer.edencore.data.event.target.DefenseTarget;
import thederpgamer.edencore.data.event.types.EventRuleSet;

import java.io.IOException;
import java.util.ArrayList;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/19/2021]
 */
public class DefenseEvent extends EventData {

    public DefenseEvent(String name, String description, EventRuleSet ruleset, EventTarget... targets) {
        super(name, description, EventType.DEFENSE, ruleset, targets);
    }

    public DefenseEvent(PacketReadBuffer readBuffer) throws IOException {
        super(readBuffer);
    }

    @Override
    public void deserialize(PacketReadBuffer readBuffer) throws IOException {
        name = readBuffer.readString();
        description = readBuffer.readString();
        eventType = EventType.DEFENSE;
        ruleset = EventRuleSet.fromPacket(readBuffer, eventType);
        int size = readBuffer.readInt();
        if(size > 0) {
            targets = new EventTarget[size];
            for(int i = 0; i < size; i ++) targets[i] = new DefenseTarget(readBuffer);
        }
        squadData = new SquadData(readBuffer);
    }

    @Override
    public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
        writeBuffer.writeString(name);
        writeBuffer.writeString(description);
        ruleset.serialize(writeBuffer);
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
                //squadData.add   (defenseTarget.getSquadName(), defenseTarget.getSquadSize());
            }
        }
    }

    @Override
    public String getAnnouncement() {
        return "A new Defense Event is starting [" + name + "]";
    }

    @Override
    public ArrayList<EventEnemyData> getEnemies() {
        return null;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void start() {
        if(squadData == null) {
            squadData = new SquadData();
            status = WAITING;
        }

        switch(status) {
            case NONE:
                status = WAITING;
                break;
            case WAITING:
                if(squadData.ready()) status = ACTIVE;
                break;
            case ACTIVE:
                break;
        }
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void end() {

    }
}
