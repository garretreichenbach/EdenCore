package thederpgamer.edencore.manager;

import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.data.event.SortieData;
import thederpgamer.edencore.data.exchange.BlueprintExchangeItem;
import thederpgamer.edencore.data.exchange.ResourceExchangeItem;

import java.util.ArrayList;

/**
 * Stores client cache data for periodical updates from server.
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/18/2021]
 */
public class ClientCacheManager {

    //Exchange
    public static final ArrayList<BlueprintExchangeItem> blueprintExchangeItems = new ArrayList<>();
    public static final ArrayList<ResourceExchangeItem> resourceExchangeItems = new ArrayList<>();

    //Events
    public static final ArrayList<EventData> eventData = new ArrayList<>();
    public static final ArrayList<SortieData> sortieData = new ArrayList<>();
}
