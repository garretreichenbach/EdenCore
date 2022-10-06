package thederpgamer.edencore.data.event.types;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.common.util.linAlg.Vector3i;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.data.event.EventEnemyData;
import thederpgamer.edencore.data.event.EventRuleset;
import thederpgamer.edencore.data.event.EventTarget;
import thederpgamer.edencore.data.event.target.CaptureTarget;

import java.io.IOException;
import java.util.ArrayList;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/05/2021]
 */
public class CaptureEvent extends EventData {

    public CaptureEvent(String name, String description, EventRuleset ruleset, Vector3i sector) {
        super(name, description, EventType.CAPTURE, ruleset, sector);
    }

    public CaptureEvent(PacketReadBuffer readBuffer) throws IOException {
        super(readBuffer);
    }

    @Override
    public void deserialize(PacketReadBuffer readBuffer) throws IOException {
        name = readBuffer.readString();
        description = readBuffer.readString();
        eventType = EventType.CAPTURE;
        ruleset = new EventRuleset(readBuffer);
        sector = readBuffer.readVector();
        int size = readBuffer.readInt();
        if(size > 0) {
            targets = new EventTarget[size];
            for(int i = 0; i < size; i ++) targets[i] = new CaptureTarget(readBuffer);
        }
    }

    @Override
    public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
        writeBuffer.writeString(name);
        writeBuffer.writeString(description);
        ruleset.serialize(writeBuffer);
        writeBuffer.writeVector(sector);
        writeBuffer.writeInt(targets.length);
        if(targets.length > 0) for(EventTarget target : targets) target.serialize(writeBuffer);
    }

    @Override
    public String getAnnouncement() {
        return null;
    }

    @Override
    public ArrayList<EventEnemyData> getEnemies() {
        return null;
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public void start() {

    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void end() {

    }
}
