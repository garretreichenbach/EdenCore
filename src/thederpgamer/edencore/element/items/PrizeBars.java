package thederpgamer.edencore.element.items;

import api.config.BlockConfig;
import api.utils.element.Blocks;
import org.schema.game.common.data.element.ElementCategory;
import org.schema.schine.graphicsengine.core.GraphicsContext;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/17/2021]
 */
public class PrizeBars implements ItemGroup {

    @Override
    public Item[] getItems() {
        return new Item[] {new BronzeBar(), new SilverBar(), new GoldBar()};
    }

    @Override
    public ElementCategory getCategory() {
        return Blocks.GOLD_BAR.getInfo().getType();
    }

    @Override
    public String getMultiSlot() {
        return "prizebars";
    }

    public class BronzeBar extends Item {

        public BronzeBar() {
            super("Bronze Bar", getCategory());
        }

        @Override
        public void initialize() {
            if(GraphicsContext.initialized) {
                itemInfo.setBuildIconNum(Blocks.BRONZE_BAR.getInfo().getBuildIconNum());
            }
            itemInfo.setDescription("A rare bronze token which can be redeemed for unique prizes at the server shop.");
            itemInfo.setInRecipe(false);
            itemInfo.setShoppable(false);
            itemInfo.setPlacable(false);
            itemInfo.setPhysical(false);
            BlockConfig.add(itemInfo);
        }
    }

    public class SilverBar extends Item {

        public SilverBar() {
            super("Silver Bar", getCategory());
        }

        @Override
        public void initialize() {
            if(GraphicsContext.initialized) {
                itemInfo.setBuildIconNum(Blocks.SILVER_BAR.getInfo().getBuildIconNum());
            }
            itemInfo.setDescription("An esteemed silver token which can be redeemed for unique prizes at the server shop.");
            itemInfo.setInRecipe(false);
            itemInfo.setShoppable(false);
            itemInfo.setPlacable(false);
            itemInfo.setPhysical(false);
            BlockConfig.add(itemInfo);
        }
    }

    public class GoldBar extends Item {

        public GoldBar() {
            super("Gold Bar", getCategory());
        }

        @Override
        public void initialize() {
            if(GraphicsContext.initialized) {
                itemInfo.setBuildIconNum(Blocks.GOLD_BAR.getInfo().getBuildIconNum());
            }
            itemInfo.setDescription("An exquisite gold token which can be redeemed for unique prizes at the server shop.");
            itemInfo.setInRecipe(false);
            itemInfo.setShoppable(false);
            itemInfo.setPlacable(false);
            itemInfo.setPhysical(false);
            BlockConfig.add(itemInfo);
        }
    }
}