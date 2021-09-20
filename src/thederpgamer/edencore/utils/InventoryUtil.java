package thederpgamer.edencore.utils;

import api.DebugFile;
import org.schema.game.common.data.player.inventory.Inventory;
import org.schema.game.common.data.player.inventory.InventorySlot;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/19/2021]
 */
public class InventoryUtil {

    public static void consumeItems(Inventory inventory, short id, int toConsume) {

        if(!inventory.containsAny(id)) DebugFile.err("Tried to consume x" + toConsume + " " + id + "in an inventory, however the inventory did not contain any!");
        else {
            while(toConsume > 0) {
                for(int slotNum : inventory.getSlots()) {
                    InventorySlot slot = inventory.getSlot(slotNum);
                    if(!slot.isMetaItem()) {
                        if(slot.isMultiSlot()) {
                            for(InventorySlot subSlot : slot.getSubSlots()) {
                                if(subSlot.getType() == id) {
                                    int toSubtract = subSlot.count() - toConsume;
                                    if(toSubtract > 0) {
                                        subSlot.setCount(subSlot.count() - toSubtract);
                                        toConsume = toConsume - toSubtract;
                                    } else {
                                        subSlot.clear();
                                        int overFlow = Math.abs(toSubtract);
                                        toConsume = toConsume - overFlow;
                                    }
                                }
                            }
                        } else {
                            if(slot.getType() == id) {
                                int toSubtract = slot.count() - toConsume;
                                if(toSubtract > 0) {
                                    slot.setCount(slot.count() - toSubtract);
                                    toConsume = toConsume - toSubtract;
                                } else {
                                    slot.clear();
                                    int overFlow = Math.abs(toSubtract);
                                    toConsume = toConsume - overFlow;
                                }
                            }
                        }
                    }
                }
            }
            inventory.sendAll();
        }
    }
}
