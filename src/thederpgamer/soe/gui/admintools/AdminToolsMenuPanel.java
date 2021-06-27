package thederpgamer.soe.gui.admintools;

import api.utils.gui.GUIMenuPanel;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.input.InputState;
import thederpgamer.soe.gui.admintools.playermenu.PlayerScrollableList;

/**
 * Panel for Admin Tools menu.
 *
 * @author TheDerpGamer
 * @since 06/27/2021
 */
public class AdminToolsMenuPanel extends GUIMenuPanel {

    public AdminToolsMenuPanel(InputState inputState) {
        super(inputState, "AdminToolsMenuPanel", 800, 500);
    }

    @Override
    public void recreateTabs() {
        guiWindow.clearTabs();
        createServerMenu();
        createPlayerMenu();
        createFactionMenu();
        createEntityMenu();
    }

    private void createServerMenu() {
        GUIContentPane serverMenu = guiWindow.addTab("SERVER");
        serverMenu.setTextBoxHeightLast(500);
        //Todo: Server Menu
    }

    private void createPlayerMenu() {
        GUIContentPane playerMenu = guiWindow.addTab("PLAYERS");
        playerMenu.setTextBoxHeightLast(500);

        PlayerScrollableList playerList = new PlayerScrollableList(getState(), playerMenu.getWidth(), playerMenu.getHeight(), playerMenu.getContent(0));
        playerList.onInit();
        playerMenu.getContent(0).attach(playerList);
    }

    private void createFactionMenu() {
        GUIContentPane factionMenu = guiWindow.addTab("FACTIONS");
        factionMenu.setTextBoxHeightLast(500);
        //Todo: Faction Menu
    }

    private void createEntityMenu() {
        GUIContentPane entityMenu = guiWindow.addTab("ENTITIES");
        entityMenu.setTextBoxHeightLast(500);
        //Todo: Entity Menu
    }
}