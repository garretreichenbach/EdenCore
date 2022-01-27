package thederpgamer.edencore.data.other;

import javax.vecmath.Vector3f;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;

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
    this.size =
        new Vector3f(
            entity.getBoundingBox().sizeX(),
            entity.getBoundingBox().sizeY(),
            entity.getBoundingBox().sizeZ());
    this.owner = owner.getName();
    this.saveDate = System.currentTimeMillis();
  }

  public EntityHeaderData(byte[] data) {
    deserialize(data);
  }

  public byte[] serialize() {
    String type =
        (entityType.equals(SimpleTransformableSendableObject.EntityType.SPACE_STATION))
            ? "SPACE_STATION"
            : "SHIP";
    return ("NAME: "
            + entityName
            + "\n"
            + "TYPE: "
            + type
            + "\n"
            + "MASS: "
            + mass
            + "\n"
            + "SIZE: "
            + toStringPure(size)
            + "\n"
            + "OWNER: "
            + owner
            + "\n"
            + "SAVED: "
            + saveDate)
        .getBytes();
  }

  public void deserialize(byte[] data) {
    String[] rawData = new String(data).split("\n");
    for (int i = 0; i < rawData.length; i++) rawData[i] = rawData[i].split(": ")[1];
    entityName = rawData[0];
    entityType =
        (!rawData[0].equals("SPACE_STATION"))
            ? SimpleTransformableSendableObject.EntityType.SHIP
            : SimpleTransformableSendableObject.EntityType.SPACE_STATION;
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
    return new Vector3f(
        Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]));
  }
}
