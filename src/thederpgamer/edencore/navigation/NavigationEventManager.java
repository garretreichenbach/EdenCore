package thederpgamer.edencore.navigation;

import api.ModPlayground;
import api.listener.Listener;
import api.listener.events.input.KeyPressEvent;
import api.listener.events.player.PlayerSpawnEvent;
import api.mod.StarLoader;
import org.schema.game.client.data.GameClientState;
import org.schema.game.client.view.gamemap.GameMapDrawer;
import org.schema.game.server.data.GameServerState;
import thederpgamer.edencore.EdenCore;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 27.09.2021
 * TIME: 16:51
 * core of the eventbased update system
 */
public class NavigationEventManager {
    public static void clientInit() {
        StarLoader.registerListener(PlayerSpawnEvent.class, new Listener<PlayerSpawnEvent>() {
            @Override
            public void onEvent(PlayerSpawnEvent playerSpawnEvent) {
                EdenMapDrawer drawer = EdenMapDrawer.instance;
                if (drawer == null)
                    return;
                drawer.updatePrivateMarkers();
            }
        }, EdenCore.getInstance());
        StarLoader.registerListener(KeyPressEvent.class, new Listener<KeyPressEvent>() {
            @Override
            public void onEvent(KeyPressEvent keyPressEvent) {
                GameMapDrawer drawer = GameClientState.instance.getWorldDrawer().getGameMapDrawer();
                if (keyPressEvent.isKeyDown() && keyPressEvent.getChar()=='m' && !drawer.isMapActive()) { //TODO this is a very unspecific event and fires way to often
                    if (EdenMapDrawer.instance == null)
                        return;
                //    ModPlayground.broadcastMessage("M clicked");
                    EdenMapDrawer.instance.updatePrivateMarkers();
                    EdenMapDrawer.instance.updateInternalList();
                }
            }
        },EdenCore.getInstance());
    }

    public static void serverInit() {
        StarLoader.registerListener(PlayerSpawnEvent.class, new Listener<PlayerSpawnEvent>() {
            @Override
            public void onEvent(PlayerSpawnEvent event) {
                NavigationUtilManager.instance.synchPlayer(event.getPlayer().getOwnerState());
            }
        },EdenCore.getInstance());
    }
}
