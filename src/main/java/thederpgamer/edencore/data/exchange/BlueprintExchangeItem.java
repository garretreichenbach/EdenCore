package thederpgamer.edencore.data.exchange;

import api.common.GameClient;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.common.util.StringTools;
import org.schema.game.common.data.player.catalog.CatalogPermission;
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
	// public static final transient Vector3i SECTOR = new Vector3i(100000000, 100000000, 100000000);
	// public long blocks;
	public String iconPath;
	public boolean community;
	public String seller = "server";
	public String entityType = "SHIP";

	public BlueprintExchangeItem(PacketReadBuffer readBuffer) {
		super(readBuffer);
	}

	public BlueprintExchangeItem(CatalogPermission blueprint, short barType, int price, String description, String iconPath) {
		super(barType, price, blueprint.getUid(), description);
		this.barType = barType;
		this.price = price;
		this.name = blueprint.getUid();
		// this.blocks = blueprint.getElementCountMapWithChilds().getTotalAmount();
		this.iconPath = iconPath;
		this.description = StringTools.massFormat(blueprint.mass) + " mass\n" + description;
		this.seller = "server";
		this.entityType = blueprint.type.name();
	}

	@Override
	public void deserialize(PacketReadBuffer readBuffer) throws IOException {
		barType = readBuffer.readShort();
		price = readBuffer.readInt();
		name = readBuffer.readString();
		description = readBuffer.readString();
		iconPath = readBuffer.readString();
		community = readBuffer.readBoolean();
		try {
			seller = readBuffer.readString();
			entityType = readBuffer.readString();
		} catch(NullPointerException ignored) {
			seller = "server";
			entityType = "SHIP";
		}
	}

	@Override
	public GUIOverlay getIcon() {
		GUIOverlay overlay = null;
		if(iconPath != null && !iconPath.isEmpty() && iconPath.startsWith("https://") && iconPath.endsWith(".png")) {
			int attempts = 0;
			while(attempts < 5 && overlay == null) {
				overlay = fetchImageIcon(iconPath);
				attempts++;
			}
		}
		if(overlay == null) {
			Sprite sprite = ResourceManager.getSprite("default-sprite");
			sprite.setWidth(100);
			sprite.setHeight(100);
			overlay = new GUIOverlay(sprite, GameClient.getClientState());
			overlay.setUserPointer("default-sprite");
		}
		return overlay;
	}

	private GUIOverlay fetchImageIcon(String url) {
		GUIOverlay overlay = null;
		Sprite sprite = ImageUtils.getImage(url);
		if(sprite != null) {
			sprite.setPositionCenter(true);
			sprite.setWidth(128);
			sprite.setHeight(128);
			overlay = new GUIOverlay(sprite, GameClient.getClientState());
			overlay.setUserPointer(url);
		}
		return overlay;
	}

	@Override
	public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
		writeBuffer.writeShort(barType);
		writeBuffer.writeInt(price);
		writeBuffer.writeString(name);
		writeBuffer.writeString(description);
		// writeBuffer.writeLong(blocks);
		writeBuffer.writeString(iconPath);
		writeBuffer.writeBoolean(community);
		if(seller == null) seller = "server";
		writeBuffer.writeString(seller);
		if(entityType == null) entityType = "SHIP";
		writeBuffer.writeString(entityType);
    /* Todo: Somehow generate a preview of the entity that can be used as it's icon
    try {
        writeBuffer.writeSendable(createEntity());
    } catch(EntityNotFountException | EntityAlreadyExistsException | StateParameterNotFoundException exception) {
        exception.printStackTrace();
    }
     */
	}

	@Override
	public boolean equals(ExchangeItem exchangeItem) {
		return exchangeItem instanceof BlueprintExchangeItem && exchangeItem.name.equals(name) && exchangeItem.barType == barType && exchangeItem.price == price && ((BlueprintExchangeItem) exchangeItem).entityType.equals(entityType);
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
