package thederpgamer.edencore.data.entity;

import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;

/**
 * Security record for ensuring that entities can't be smuggled out of build sectors.
 *
 * @author TheDerpGamer
 */
public class SegmentControllerRecord {

	public String name;
	public String uniqueIdentifier;
	public int type;
	public final Vector3i buildSector = new Vector3i();

	public SegmentControllerRecord(SegmentController entity, Vector3i buildSector) {
		name = entity.getName();
		uniqueIdentifier = entity.getUniqueIdentifier();
		type = entity.getType().dbTypeId;
		this.buildSector.set(buildSector);
	}
}
