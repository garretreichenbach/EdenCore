package thederpgamer.edencore.data.other;

import api.mod.config.PersistentObjectUtil;
import org.schema.game.common.data.player.SavedCoordinate;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.navigation.MapMarker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 18.09.2021
 * TIME: 14:12
 */
public class NavigationListContainer implements Serializable {
    public HashMap<Long, MapMarker> publicMarkers = new HashMap<>();

    /**
     * auto adds itself to persisntence.
     */
    public NavigationListContainer() {

    }

    public void save() {
        PersistentObjectUtil.save(EdenCore.getInstance().getSkeleton());
    }

    /**
     * get the container object. only one allowed. will auto deleted anything over index 0
     * @param autoAdd automatically add to skeleton
     * @return existing or new object
     */
    public static NavigationListContainer getContainer(boolean autoAdd) {
        ArrayList<Object> objs = PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(),NavigationListContainer.class);
        if (objs.size()==0) {
            NavigationListContainer c = new NavigationListContainer();
            if (autoAdd) {
                //add to persisntence
                PersistentObjectUtil.addObject(EdenCore.getInstance().getSkeleton(), c);
            }
            return c;
        }
        for (int i = objs.size()-1; i>0; i--) {
            PersistentObjectUtil.removeObject(EdenCore.getInstance().getSkeleton(), i);
        }

        assert PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(),NavigationListContainer.class).size()==1;
        assert objs.get(0) instanceof NavigationListContainer;

        return (NavigationListContainer) objs.get(0);
    }
}
