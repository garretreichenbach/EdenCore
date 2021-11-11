package thederpgamer.edencore.data.event.types;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.common.util.linAlg.Vector3i;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.data.event.EventRuleset;
import thederpgamer.edencore.data.event.EventTarget;

import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/05/2021]
 */
public class CaptureEvent extends EventData {

    public CaptureEvent(String name, String description, EventType eventType, EventRuleset ruleset, Vector3i sector) {
        super(name, description, eventType, ruleset, sector);
    }

    @Override
    public void deserialize(PacketReadBuffer readBuffer) throws IOException {
        name = readBuffer.readString();
        description = readBuffer.readString();
        eventType = EventType.deserialize(readBuffer);
        ruleset = new EventRuleset(readBuffer);
        sector = readBuffer.readVector();
        int size = readBuffer.readInt();
        if(size > 0) {
            targets = new EventTarget[size];
            for(int i = 0; i < size; i ++) targets[i] = new EventTarget(readBuffer);
        }
    }

    @Override
    public void serialize(PacketWriteBuffer writeBuffer) throws IOException {

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
