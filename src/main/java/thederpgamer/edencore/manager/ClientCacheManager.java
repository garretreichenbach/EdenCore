package thederpgamer.edencore.manager;

import org.schema.game.common.controller.SegmentController;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.data.exchange.BlueprintExchangeItem;
import thederpgamer.edencore.data.exchange.ItemExchangeItem;
import thederpgamer.edencore.data.exchange.ResourceExchangeItem;
import thederpgamer.edencore.data.other.BuildSectorData;

import java.util.ArrayList;

/**
 * Stores client cache data for periodical updates from server.
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/18/2021]
 */
public class ClientCacheManager {

    public static final int BLUEPRINT_EXCHANGE = 0;
    public static final int RESOURCE_EXCHANGE = 1;
    public static final int ITEM_EXCHANGE = 2;
    public static final int EVENT_DATA = 3;
    public static final int BUILD_SECTOR_DATA = 4;
    public static final int BUILD_SECTOR_ENTITIES = 5;

    //Exchange
    public static final ArrayList<BlueprintExchangeItem> blueprintExchangeItems = new ArrayList<>();
    public static final ArrayList<ResourceExchangeItem> resourceExchangeItems = new ArrayList<>();
    public static final ArrayList<ItemExchangeItem> itemExchangeItems = new ArrayList<>();

    //Events
    public static final ArrayList<EventData> eventData = new ArrayList<>();

    //Build Sector
    public static final ArrayList<BuildSectorData> accessibleSectors = new ArrayList<>();
    public static final ArrayList<SegmentController> sectorEntities = new ArrayList<>();

    public static void updateCache(int type, Object data) {
        switch(type) {
            case BLUEPRINT_EXCHANGE:
                blueprintExchangeItems.remove((BlueprintExchangeItem) data);
                blueprintExchangeItems.add((BlueprintExchangeItem) data);
                break;
            case RESOURCE_EXCHANGE:
                resourceExchangeItems.remove((ResourceExchangeItem) data);
                resourceExchangeItems.add((ResourceExchangeItem) data);
                break;
            case ITEM_EXCHANGE:
                itemExchangeItems.remove((ItemExchangeItem) data);
                itemExchangeItems.add((ItemExchangeItem) data);
                break;
            case EVENT_DATA:
                eventData.remove((EventData) data);
                eventData.add((EventData) data);
                break;
            case BUILD_SECTOR_DATA:
                accessibleSectors.remove((BuildSectorData) data);
                accessibleSectors.add((BuildSectorData) data);
                break;
            case BUILD_SECTOR_ENTITIES:
                sectorEntities.remove((SegmentController) data);
                sectorEntities.add((SegmentController) data);
                break;
            default: throw new IllegalArgumentException("Invalid cache type: " + type);
        }
    }
}
