package thederpgamer.edencore.data.event.types.capture;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.data.event.EventEnemyData;
import thederpgamer.edencore.data.event.EventTarget;
import thederpgamer.edencore.data.event.target.CaptureTarget;
import thederpgamer.edencore.data.event.types.EventRuleSet;

import java.io.IOException;
import java.util.ArrayList;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/05/2021]
 */
public class CaptureEvent extends EventData {

    public CaptureEvent(String name, String description, EventRuleSet ruleset, Vector3i sector) {
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
        ruleset = EventRuleSet.fromPacket(readBuffer, eventType);
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
        //Pick a random amount of capture targets based off difficulty
        SegmentController segmentController = generateCaptureTarget();
        targets[0] = new CaptureTarget(segmentController.getUniqueIdentifier());
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void end() {

    }

    /**
     * Calculates the event difficulty based off the event ruleset and the player count and mass.
     * @return
     */
    public float getDifficulty() throws IOException {
       if(ruleset == null) ruleset = new CaptureRuleSet(this);
    }

    private SegmentController generateCaptureTarget() {

    }
}
