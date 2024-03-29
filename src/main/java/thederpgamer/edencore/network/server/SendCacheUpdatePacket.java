package thederpgamer.edencore.network.server;

import api.common.GameClient;
import api.mod.config.PersistentObjectUtil;
import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.data.exchange.BlueprintExchangeItem;
import thederpgamer.edencore.data.exchange.ItemExchangeItem;
import thederpgamer.edencore.data.exchange.ResourceExchangeItem;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.manager.ClientCacheManager;
import thederpgamer.edencore.utils.DataUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Sends a cache updateClients to clients.
 * <p>[SERVER] -> [CLIENT]</p>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/18/2021]
 */
public class SendCacheUpdatePacket extends Packet {
	//Exchange
	private final ArrayList<BlueprintExchangeItem> blueprintExchangeItems = new ArrayList<>();
	private final ArrayList<ResourceExchangeItem> resourceExchangeItems = new ArrayList<>();
	private final ArrayList<ItemExchangeItem> itemExchangeItems = new ArrayList<>();
	//Events
	private final ArrayList<EventData> eventData = new ArrayList<>();
	//Build Sector
	private final ArrayList<BuildSectorData> sectorData = new ArrayList<>();
	private final ArrayList<SegmentController> sectorEntities = new ArrayList<>();
	//Data
	private PlayerState playerState;

	public SendCacheUpdatePacket() {
	}

	public SendCacheUpdatePacket(PlayerState playerState) {
		this.playerState = playerState;
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		//Exchange
		int bpSize = packetReadBuffer.readInt();
		if(bpSize > 0) for(int i = 0; i < bpSize; i++) blueprintExchangeItems.add(new BlueprintExchangeItem(packetReadBuffer));
		EdenCore.getInstance().logInfo("Received " + bpSize + " updated bp exchange items.");
		int resSize = packetReadBuffer.readInt();
		if(resSize > 0) for(int i = 0; i < resSize; i++) resourceExchangeItems.add(new ResourceExchangeItem(packetReadBuffer));
		EdenCore.getInstance().logInfo("Received " + resSize + " updated resource exchange items.");
		int itemSize = packetReadBuffer.readInt();
		if(itemSize > 0) for(int i = 0; i < itemSize; i++) itemExchangeItems.add(new ItemExchangeItem(packetReadBuffer));
		EdenCore.getInstance().logInfo("Received " + resSize + " updated item exchange items.");
		//Events
        /* Todo: Finish events
        int eventSize = packetReadBuffer.readInt();
        for(int i = 0; i < eventSize; i ++) eventData.add(EventData.fromPacket(packetReadBuffer));

        int sortieSize = packetReadBuffer.readInt();
        for(int i = 0; i < sortieSize; i ++) sortieData.add(new SortieData(packetReadBuffer));
         */
		//Build Sectors
		int sectorSize = packetReadBuffer.readInt();
		if(sectorSize > 0) {
			for(int i = 0; i < sectorSize; i++) {
				try {
					sectorData.add(new BuildSectorData(packetReadBuffer));
				} catch(Exception exception) {
					EdenCore.getInstance().logException("Encountered an exception while trying to deserialize Build Sector Data", exception);
				}
			}
		}
		EdenCore.getInstance().logInfo("Received " + sectorSize + " updated build sector data.");

        /*
        int entitySize = packetReadBuffer.readInt();
        if(entitySize > 0) {
            for(int i = 0; i < entitySize; i ++) {
                try {
                    int entityId = packetReadBuffer.readInt();
                    if(entityId > 0) {
                        Sendable sendable = GameCommon.getGameObject(entityId);
                        if(sendable instanceof SegmentController && !((SegmentController) sendable).isVirtualBlueprint()) sectorEntities.add((SegmentController) sendable);
                    }
                    //Sendable sendable = packetReadBuffer.readSendable(); This throws an exception sometimes, and even with a catch it still crashes if thrown
                    //if(sendable instanceof SegmentController) sectorEntities.add((SegmentController) sendable);
                } catch(Exception exception) {
                    EdenCore.getInstance().logException("Encountered an exception while trying to deserialize SegmentController data", exception);
                }
            }
        }
        EdenCore.getInstance().logDebug("Received " + entitySize + " updated sector entity data.");

         */
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		getBlueprintItems();
		packetWriteBuffer.writeInt(blueprintExchangeItems.size());
		if(blueprintExchangeItems.size() > 0) for(BlueprintExchangeItem exchangeItem : blueprintExchangeItems) exchangeItem.serialize(packetWriteBuffer);
		getResourceItems();
		packetWriteBuffer.writeInt(resourceExchangeItems.size());
		if(resourceExchangeItems.size() > 0) for(ResourceExchangeItem exchangeItem : resourceExchangeItems) exchangeItem.serialize(packetWriteBuffer);
		getItems();
		packetWriteBuffer.writeInt(itemExchangeItems.size());
		if(itemExchangeItems.size() > 0) for(ItemExchangeItem exchangeItem : itemExchangeItems) exchangeItem.serialize(packetWriteBuffer);
		getBuildSectors();
		packetWriteBuffer.writeInt(sectorData.size());
		if(sectorData.size() > 0) for(BuildSectorData sector : sectorData) sector.serialize(packetWriteBuffer);

        /*
        getSectorEntities();
        packetWriteBuffer.writeInt(sectorEntities.size());
        if(sectorEntities.size() > 0) for(SegmentController segmentController : sectorEntities) packetWriteBuffer.writeInt(segmentController.getId());
         */
	}

	@Override
	public void processPacketOnClient() {
		//Todo: Only add the ones that actually need updating rather than just resending all of them
		try { //Exchange
			ClientCacheManager.blueprintExchangeItems.clear();
			ClientCacheManager.blueprintExchangeItems.addAll(blueprintExchangeItems);
			ClientCacheManager.resourceExchangeItems.clear();
			ClientCacheManager.resourceExchangeItems.addAll(resourceExchangeItems);
			ClientCacheManager.itemExchangeItems.clear();
			ClientCacheManager.itemExchangeItems.addAll(itemExchangeItems);
			//EdenCore.getInstance().exchangeMenuControlManager.getMenuPanel().recreateTabs();
		} catch(Exception exception) {
			EdenCore.getInstance().logException("Encountered an exception while trying to updateClients client cache", exception);
		}
		//try { //Events
		//ClientCacheManager.eventData.clear();
		//ClientCacheManager.eventData.addAll(eventData);
		//ClientCacheManager.sortieData.clear();
		//ClientCacheManager.sortieData.addAll(sortieData);
		//} catch(Exception ignored) { }
		//Build Sectors
		try {
			ClientCacheManager.accessibleSectors.clear();
			for(BuildSectorData data : sectorData) {
				if(data != null && GameClient.getClientState() != null && GameClient.getClientPlayerState() != null && data.hasPermission(GameClient.getClientPlayerState().getName(), "ENTER")) ClientCacheManager.accessibleSectors.add(data);
			}
		} catch(Exception exception) {
			EdenCore.getInstance().logException("Encountered an exception while trying to updateClients client cache", exception);
		}
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
		this.playerState = playerState;
	}

	private void getBlueprintItems() {
		ArrayList<BlueprintExchangeItem> exchangeItems = new ArrayList<>();
		for(Object obj : PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(), BlueprintExchangeItem.class)) {
			exchangeItems.add((BlueprintExchangeItem) obj);
		}
		blueprintExchangeItems.addAll(exchangeItems);
	}

	private void getResourceItems() {
		ArrayList<ResourceExchangeItem> exchangeItems = new ArrayList<>();
		for(Object obj : PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(), ResourceExchangeItem.class)) {
			exchangeItems.add((ResourceExchangeItem) obj);
		}
		resourceExchangeItems.addAll(exchangeItems);
	}

	private void getItems() {
		ArrayList<ItemExchangeItem> exchangeItems = new ArrayList<>();
		for(Object obj : PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(), ItemExchangeItem.class)) {
			exchangeItems.add((ItemExchangeItem) obj);
		}
		itemExchangeItems.addAll(exchangeItems);
	}

	private void getBuildSectors() {
		ArrayList<BuildSectorData> sectorDataList = new ArrayList<>();
		for(Object obj : PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(), BuildSectorData.class)) {
			sectorDataList.add((BuildSectorData) obj);
		}
		sectorData.addAll(sectorDataList);
	}

	private void getSectorEntities() {
		if(DataUtils.isPlayerInAnyBuildSector(playerState)) sectorEntities.addAll(DataUtils.getEntitiesInBuildSector(DataUtils.getPlayerCurrentBuildSector(playerState)));
        /*
        if(DataUtils.isPlayerInAnyBuildSector(playerState)) {
            for(SimpleTransformableSendableObject<?> object : GameClient.getClientState().getCurrentSectorEntities().values()) {
                if(object instanceof SegmentController) sectorEntities.add((SegmentController) object);
            }
        }
         */
	}
}
