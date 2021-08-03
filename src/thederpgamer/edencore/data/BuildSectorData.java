package thederpgamer.edencore.data;

import org.schema.common.util.linAlg.Vector3i;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/02/2021
 */
public class BuildSectorData implements ComparableData {

    public String ownerId;
    public String ownerName;
    public Vector3i sector;
    public String[] permissions;

    public BuildSectorData(String ownerName, String ownerId, Vector3i sector, String[] permissions) {
        this.ownerName = ownerName;
        this.ownerId = ownerId;
        this.sector = sector;
        this.permissions = permissions;
    }

    @Override
    public boolean equalTo(ComparableData data) {
        if(data instanceof BuildSectorData) {
            BuildSectorData sectorData = (BuildSectorData) data;
            return sectorData.ownerId.equals(ownerId) && sectorData.ownerName.equals(ownerName) && sector.equals(sectorData.sector);
        } else return false;
    }
}
