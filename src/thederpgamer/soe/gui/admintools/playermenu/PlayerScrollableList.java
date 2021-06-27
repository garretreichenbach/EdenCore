package thederpgamer.soe.gui.admintools.playermenu;

import org.schema.game.common.data.player.PlayerState;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUIElementList;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIActiveInterface;
import org.schema.schine.graphicsengine.forms.gui.newgui.ScrollableTableList;
import org.schema.schine.input.InputState;
import thederpgamer.soe.utils.PlayerUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

/**
 * Player List for Admin Tools menu.
 *
 * @author TheDerpGamer
 * @since 06/27/2021
 */
public class PlayerScrollableList extends ScrollableTableList<PlayerState> implements GUIActiveInterface {

    public PlayerScrollableList(InputState state, float width, float height, GUIElement contentPane) {
        super(state, width, height, contentPane);
    }

    @Override
    public void initColumns() {
        addColumn("Name", 10.0f, new Comparator<PlayerState>() {
            @Override
            public int compare(PlayerState o1, PlayerState o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        addColumn("Faction", 10.0f, new Comparator<PlayerState>() {
            @Override
            public int compare(PlayerState o1, PlayerState o2) {
                return o1.getFactionName().compareTo(o2.getFactionName());
            }
        });

        addColumn("Sector", 5.0f, new Comparator<PlayerState>() {
            @Override
            public int compare(PlayerState o1, PlayerState o2) {
                return o1.getCurrentSector().compareTo(o2.getCurrentSector());
            }
        });

        addColumn("Type", 7.0f, new Comparator<PlayerState>() {
            @Override
            public int compare(PlayerState o1, PlayerState o2) {
                return PlayerUtils.getPlayerType(o1).compareTo(PlayerUtils.getPlayerType(o2));
            }
        });
    }

    @Override
    protected Collection<PlayerState> getElementList() {
        return null;
    }

    @Override
    public void updateListEntries(GUIElementList var1, Set<PlayerState> var2) {

    }
}
