package thederpgamer.edencore.network.server;

import api.mod.config.PersistentObjectUtil;
import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.exchange.BlueprintExchangeItem;
import thederpgamer.edencore.data.exchange.ResourceExchangeItem;
import thederpgamer.edencore.manager.ClientCacheManager;

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

    private final ArrayList<BlueprintExchangeItem> blueprintExchangeItems = new ArrayList<>();
    private final ArrayList<ResourceExchangeItem> resourceExchangeItems = new ArrayList<>();

    public SendCacheUpdatePacket() {

    }

    @Override
    public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
        int bpSize = packetReadBuffer.readInt();
        for(int i = 0; i < bpSize; i ++) blueprintExchangeItems.add(new BlueprintExchangeItem(packetReadBuffer));

        int resSize = packetReadBuffer.readInt();
        for(int i = 0; i < resSize; i ++) resourceExchangeItems.add(new ResourceExchangeItem(packetReadBuffer));
    }

    @Override
    public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
        getBlueprintItems();
        packetWriteBuffer.writeInt(blueprintExchangeItems.size());
        for(BlueprintExchangeItem exchangeItem : blueprintExchangeItems) exchangeItem.serialize(packetWriteBuffer);

        getResourceItems();
        packetWriteBuffer.writeInt(resourceExchangeItems.size());
        for(ResourceExchangeItem exchangeItem : resourceExchangeItems) exchangeItem.serialize(packetWriteBuffer);
    }

    @Override
    public void processPacketOnClient() {
        try {
            ClientCacheManager.blueprintExchangeItems.clear();
            ClientCacheManager.blueprintExchangeItems.addAll(blueprintExchangeItems);

            ClientCacheManager.resourceExchangeItems.clear();
            ClientCacheManager.resourceExchangeItems.addAll(resourceExchangeItems);

            EdenCore.getInstance().exchangeMenuControlManager.getMenuPanel().recreateTabs();
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
}
