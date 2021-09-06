package thederpgamer.edencore.data;

import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;

import javax.vecmath.Vector3f;

/**
 * <Description>
 *
 * @author TheDerpGamer
 */
public class EntityHeaderData {

    public String entityName;
    public SimpleTransformableSendableObject.EntityType entityType;
    public float mass;
    public Vector3f size;
    public String owner;
    public long saveDate;

    public EntityHeaderData(SegmentController entity, PlayerState owner) {
        this.entityName = entity.getRealName();
        this.entityType = entity.getType();
        this.mass = entity.getTotalPhysicalMass();
        this.size = new Vector3f(entity.getBoundingBox().sizeX(), entity.getBoundingBox().sizeY(), entity.getBoundingBox().sizeZ());
        this.owner = owner.getName();
        this.saveDate = System.currentTimeMillis();
    }

    public EntityHeaderData(byte[] data) {
        deserialize(data);
    }

    public byte[] serialize() {
        return (
                "NAME: " + entityName + "\n" +
                "TYPE: " + entityType.toString() + "\n" +
                "MASS: " + mass + "\n" +
                "SIZE: " + toStringPure(size) + "\n" +
                "OWNER: " + owner + "\n" +
                "SAVED: " + saveDate
        ).getBytes();
    }

    public void deserialize(byte[] data) {
        String[] rawData = new String(data).split("\n");
        entityName = rawData[0];
        entityType = SimpleTransformableSendableObject.EntityType.valueOf(rawData[1]);
        mass = Float.parseFloat(rawData[2]);
        size = fromStringPure(rawData[3]);
        owner = rawData[4];
        saveDate = Long.parseLong(rawData[5]);
    }

    private String toStringPure(Vector3f vector) {
        return vector.x + ", " + vector.y + ", " + vector.z;
    }

    private Vector3f fromStringPure(String string) {
        String[] split = string.split(", ");
        return new Vector3f(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]));
    }
}
