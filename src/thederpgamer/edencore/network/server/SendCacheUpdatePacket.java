package thederpgamer.edencore.network.server;

import api.common.GameClient;
import api.mod.config.PersistentObjectUtil;
import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.data.event.SortieData;
import thederpgamer.edencore.data.exchange.BlueprintExchangeItem;
import thederpgamer.edencore.data.exchange.ResourceExchangeItem;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.manager.ClientCacheManager;
import thederpgamer.edencore.manager.LogManager;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Sends a cache update to clients.
 * <p>[SERVER] -> [CLIENT]</p>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/18/2021]
 */
public class SendCacheUpdatePacket extends Packet {

    //Exchange
    private final ArrayList<BlueprintExchangeItem> blueprintExchangeItems = new ArrayList<>();
    private final ArrayList<ResourceExchangeItem> resourceExchangeItems = new ArrayList<>();

    //Events
    private final ArrayList<EventData> eventData = new ArrayList<>();
    private final ArrayList<SortieData> sortieData = new ArrayList<>();

    //Build Sector
    private final ArrayList<BuildSectorData> sectorData = new ArrayList<>();

    public SendCacheUpdatePacket() {

    }

    @Override
    public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
        //Exchange
        int bpSize = packetReadBuffer.readInt();
        if(bpSize > 0) for(int i = 0; i < bpSize; i ++) blueprintExchangeItems.add(new BlueprintExchangeItem(packetReadBuffer));
        LogManager.logDebug("Received " + bpSize + " updated bp exchange items.");

        int resSize = packetReadBuffer.readInt();
        if(resSize > 0) for(int i = 0; i < resSize; i ++) resourceExchangeItems.add(new ResourceExchangeItem(packetReadBuffer));
        LogManager.logDebug("Received " + resSize + " updated resource exchange items.");

        //Events
        //int eventSize = packetReadBuffer.readInt();
        //for(int i = 0; i < eventSize; i ++) eventData.add(new EventData(packetReadBuffer));

        //int sortieSize = packetReadBuffer.readInt();
        //for(int i = 0; i < sortieSize; i ++) sortieData.add(new SortieData(packetReadBuffer));

        //Build Sectors
        int sectorSize = packetReadBuffer.readInt();
        if(sectorSize > 0) {
            for(int i = 0; i < sectorSize; i ++) {
                try {
                    sectorData.add(new BuildSectorData(packetReadBuffer));
                } catch(Exception exception) {
                    LogManager.logException("Encountered an exception while trying to deserialize Build Sector Data", exception);
                }
            }
        }
        LogManager.logDebug("Received " + sectorSize + " updated build sector data.");
    }

    @Override
    public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
        getBlueprintItems();
        packetWriteBuffer.writeInt(blueprintExchangeItems.size());
        if(blueprintExchangeItems.size() > 0) for(BlueprintExchangeItem exchangeItem : blueprintExchangeItems) exchangeItem.serialize(packetWriteBuffer);

        getResourceItems();
        packetWriteBuffer.writeInt(resourceExchangeItems.size());
        if(resourceExchangeItems.size() > 0) for(ResourceExchangeItem exchangeItem : resourceExchangeItems) exchangeItem.serialize(packetWriteBuffer);

        getBuildSectors();
        packetWriteBuffer.writeInt(sectorData.size());
        if(sectorData.size() > 0) for(BuildSectorData sector : sectorData) sector.serialize(packetWriteBuffer);
    }

    @Override
    public void processPacketOnClient() {
        //Todo: Only add the ones that actually need updating rather than just resending all of them
        try { //Exchange
            ClientCacheManager.blueprintExchangeItems.clear();
            ClientCacheManager.blueprintExchangeItems.addAll(blueprintExchangeItems);

            ClientCacheManager.resourceExchangeItems.clear();
            ClientCacheManager.resourceExchangeItems.addAll(resourceExchangeItems);

            EdenCore.getInstance().exchangeMenuControlManager.getMenuPanel().recreateTabs();
        } catch(Exception ignored) { }

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
                if(data.hasPermission(GameClient.getClientPlayerState().getName(), "ENTER")) ClientCacheManager.accessibleSectors.add(data);
            }
        } catch(Exception ignored) { }
    }

    @Override
    public void processPacketOnServer(PlayerState playerState) {

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

    private void getBuildSectors() {
        ArrayList<BuildSectorData> sectorDataList = new ArrayList<>();
        for(Object obj : PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(), BuildSectorData.class)) {
            sectorDataList.add((BuildSectorData) obj);
        }
        sectorData.addAll(sectorDataList);
    }
}
