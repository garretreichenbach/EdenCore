package thederpgamer.edencore.data.exchange;

import api.common.GameClient;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.server.controller.BluePrintController;
import org.schema.game.server.controller.EntityNotFountException;
import org.schema.game.server.data.blueprintnw.BlueprintEntry;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.graphicsengine.forms.gui.GUIOverlay;
import thederpgamer.edencore.manager.ResourceManager;
import thederpgamer.edencore.utils.ImageUtils;

import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/17/2021]
 */
public class BlueprintExchangeItem extends ExchangeItem {

    //public static final transient Vector3i SECTOR = new Vector3i(100000000, 100000000, 100000000);
    public transient BlueprintEntry blueprint;
    public long blocks;
    public String iconPath;

    public BlueprintExchangeItem(PacketReadBuffer readBuffer) {
        super(readBuffer);
    }

    public BlueprintExchangeItem(BlueprintEntry blueprint, short barType, int price, String description, String iconPath) {
        super(barType, price, blueprint.getName(), description);
        this.blueprint = blueprint;
        this.barType = barType;
        this.price = price;
        this.name = blueprint.getName();
        this.blocks = blueprint.getElementCountMapWithChilds().getTotalAmount();
        this.iconPath = iconPath;
        this.description = blocks + " blocks\n";
    }

    @Override
    public GUIOverlay getIcon() {
        GUIOverlay overlay = null;
        if(iconPath != null && !iconPath.isEmpty() && iconPath.startsWith("https://") && iconPath.endsWith(".png")) {
            ImageUtils.getImage(iconPath);
            Sprite sprite = ImageUtils.getImage(iconPath);
            if(sprite != null) {
                sprite.setPositionCenter(true);
                sprite.setWidth(200);
                sprite.setHeight(200);
                overlay = new GUIOverlay(sprite, GameClient.getClientState());
                overlay.setUserPointer(iconPath);
            }
        }
        if(overlay == null) {
            Sprite sprite = ResourceManager.getSprite("default-sprite");
            sprite.setWidth(32);
            sprite.setHeight(32);
            overlay = new GUIOverlay(sprite, GameClient.getClientState());
            overlay.setUserPointer("default-sprite");
        }
        return overlay;
    }

    @Override
    public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
        writeBuffer.writeShort(barType);
        writeBuffer.writeInt(price);
        writeBuffer.writeString(name);
        writeBuffer.writeString(description);
        writeBuffer.writeLong(blocks);
        writeBuffer.writeString(iconPath);
        /* Todo: Somehow generate a preview of the entity that can be used as it's icon
        try {
            writeBuffer.writeSendable(createEntity());
        } catch(EntityNotFountException | EntityAlreadyExistsException | StateParameterNotFoundException exception) {
            exception.printStackTrace();
        }
         */
    }

    @Override
    public void deserialize(PacketReadBuffer readBuffer) throws IOException {
        barType = readBuffer.readShort();
        price = readBuffer.readInt();
        name = readBuffer.readString();
        description = readBuffer.readString();
        blocks = readBuffer.readLong();
        iconPath = readBuffer.readString();
        try {
            blueprint = BluePrintController.active.getBlueprint(name);
        } catch(EntityNotFountException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public boolean equals(ExchangeItem exchangeItem) {
        return exchangeItem instanceof BlueprintExchangeItem && exchangeItem.name.equals(name) && exchangeItem.barType == barType && exchangeItem.price == price;
    }

    /*
    private SegmentController createEntity() throws IOException, StateParameterNotFoundException, EntityAlreadyExistsException, EntityNotFountException {
        SegmentControllerOutline<?> outline = BluePrintController.active.loadBluePrint(
                GameServerState.instance,
                blueprint.getName(),
                blueprint.getName() + "_TEMP",
                new Transform(),
                -1,
                0,
                SECTOR,
                "SERVER",
                PlayerState.buffer,
                null,
                false,
                new ChildStats(false));
        SegmentController entity = outline.spawn(SECTOR, false, new ChildStats(false), new SegmentControllerSpawnCallbackDirect(GameServer.getServerState(), SECTOR) {
            @Override
            public void onNoDocker() {

            }
        });

        new StarRunnable() {
            @Override
            public void run() {

            }
        }.runLater(EdenCore.getInstance(), 5000); //Should be enough time to send the entity and create an image of it's preview

        return entity;
    }
     */
}