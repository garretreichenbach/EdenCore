package thederpgamer.edencore.drawer;

import api.common.GameClient;
import api.utils.draw.ModWorldDrawer;
import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.controller.manager.ingame.map.MapControllerManager;
import org.schema.game.client.view.gui.shiphud.newhud.Hud;
import org.schema.schine.graphicsengine.core.Timer;
import thederpgamer.edencore.manager.LogManager;
import thederpgamer.edencore.utils.DataUtils;

import java.lang.reflect.Field;

/**
 * Modifies the Hud Drawer while the client is in a build sector in order to hide the actual coordinates.
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/08/2021]
 */
public class BuildSectorHudDrawer extends ModWorldDrawer {

    private boolean initialized = false;

    private String[] buildSectorNameReplace;
    private Vector3i[] buildSectorPosReplace;
    private Transform[] buildSectorTransformReplace;

    @Override
    public void onInit() {
        buildSectorNameReplace = new String[6];
        buildSectorPosReplace = new Vector3i[6];
        buildSectorTransformReplace = new Transform[6];

        Vector3i posReplace = new Vector3i();
        Transform transformReplace = new Transform();

        for(int i = 0; i < 6; i ++) {
            buildSectorNameReplace[i] = "";
            buildSectorPosReplace[i] = posReplace;
            buildSectorTransformReplace[i] = transformReplace;
            //Don't initialize a new object for each in order to save memory
            //buildSectorPosReplace[i] = new Vector3i();
            //buildSectorTransformReplace[i] = new Transform();
        }
        initialized = true;
    }

    @Override
    public void draw() {
        if(!initialized) onInit();
        if(DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
            MapControllerManager mapControlManager = GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getMapControlManager();
            if(mapControlManager.isActive()) { //Don't allow map usage while in a build sector
                GameClient.getClientState().getWorldDrawer().getGameMapDrawer().cleanUp();
                GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().galaxyMapAction();
                GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getMapControlManager().setActive(false);
            }

            try {
                Hud hud = GameClient.getClientState().getWorldDrawer().getGuiDrawer().getHud();
                hud.getRadar().getLocation().setTextSimple("<Build Sector>");

                //Empty the sector name displays
                Field sectorsNamesField = hud.getIndicator().getClass().getDeclaredField("neighborSectorsNames");
                sectorsNamesField.setAccessible(true);
                sectorsNamesField.set(hud.getIndicator(), buildSectorNameReplace.clone());

                //Empty the sector position displays
                Field sectorsPosField = hud.getIndicator().getClass().getDeclaredField("neighborSectorsPos");
                sectorsPosField.setAccessible(true);
                sectorsPosField.set(hud.getIndicator(), buildSectorPosReplace.clone());

                //Empty the sector transform displays
                Field sectorsTransformField = hud.getIndicator().getClass().getDeclaredField("neighborSectors");
                sectorsTransformField.setAccessible(true);
                sectorsTransformField.set(hud.getIndicator(), buildSectorTransformReplace.clone());
                //This is probably fine as these are only used for display purposes
            } catch(Exception exception) {
                LogManager.logException("Something went wrong while trying to update the hud indicator displays" , exception);
            }
        } else cleanUp();
    }

    @Override
    public void update(Timer timer) {

    }

    @Override
    public void cleanUp() {
        GameClient.getClientState().getWorldDrawer().getGuiDrawer().getHud().getRadar().getLocation().setTextSimple(GameClient.getClientPlayerState().getCurrentSector().toStringPure());
    }

    @Override
    public boolean isInvisible() {
        return false;
    }
}
