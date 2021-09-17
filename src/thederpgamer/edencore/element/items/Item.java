package thederpgamer.edencore.element.items;

import api.config.BlockConfig;
import org.schema.game.common.data.element.ElementCategory;
import org.schema.game.common.data.element.ElementInformation;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.element.ElementManager;
import thederpgamer.edencore.manager.ResourceManager;

/**
 * Base abstract item class.
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/17/2021]
 */
public abstract class Item {

    protected ElementInformation itemInfo;

    public Item(String name, ElementCategory category) {
        String internalName = name.toLowerCase().replace(" ", "-").trim();
        short textureId = (short) ResourceManager.getTexture(internalName).getTextureId();
        itemInfo = BlockConfig.newElement(EdenCore.getInstance(), name, textureId);
        itemInfo.setBuildIconNum(textureId);
        itemInfo.setPlacable(false);
        itemInfo.setPhysical(false);
        BlockConfig.setElementCategory(itemInfo, category);
        ElementManager.addItem(this);
    }

    public final ElementInformation getItemInfo() {
        return itemInfo;
    }

    public final short getId() {
        return itemInfo.getId();
    }

    public abstract void initialize();
}
