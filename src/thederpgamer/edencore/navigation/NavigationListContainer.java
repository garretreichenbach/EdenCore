package thederpgamer.edencore.navigation;

import api.mod.config.PersistentObjectUtil;
import thederpgamer.edencore.EdenCore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 18.09.2021
 * TIME: 14:12
 */
public class NavigationListContainer implements Serializable {
    public Collection<MapMarker> mapMarkers = new ArrayList<>();
    public Collection<GateMarker> gateMarkers = new ArrayList<>();

    public void setPublicMarkers(Collection<MapMarker> markers) {
        mapMarkers.clear();
        gateMarkers.clear();
        for (MapMarker m: markers) {
            //get the markers actual class, store it in a list mapped to this class
            Class clazz = m.getClass();
            if (m instanceof GateMarker) {
                gateMarkers.add((GateMarker) m);
            } else {
                mapMarkers.add(m);
            }
        }
    }

    /**
     * auto adds itself to persisntence.
     */
    public NavigationListContainer() {

    }



    public void getPublicMarkers(Collection<MapMarker> in) {
        in.addAll(mapMarkers);
        in.addAll(gateMarkers);
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
