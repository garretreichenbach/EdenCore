package thederpgamer.edencore.data.event.types.defense;

import api.mod.config.PersistentObjectUtil;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.data.event.EventTarget;
import thederpgamer.edencore.data.event.SquadData;
import thederpgamer.edencore.data.event.target.DefenseTarget;

import java.io.IOException;
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
        name = readBuffer.readString();
        description = readBuffer.readString();
        eventType = EventType.DEFENSE;
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

    public static EventData getRandom() {
        ArrayList<DefenseEvent> defenseEvents = new ArrayList<>();
        for(Object obj : PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(), DefenseEvent.class)) {
            if(obj instanceof DefenseEvent) defenseEvents.add((DefenseEvent) obj);
        }
        if(defenseEvents.size() > 0) return defenseEvents.get((int) (Math.random() * defenseEvents.size()));
        else return null;
    }
}
