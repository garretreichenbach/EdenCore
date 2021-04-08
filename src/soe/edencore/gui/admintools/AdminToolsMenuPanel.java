package soe.edencore.gui.admintools;

import api.utils.gui.GUIMenuPanel;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.input.InputState;
import soe.edencore.gui.admintools.logs.AdminLogList;
import soe.edencore.gui.admintools.player.PlayerDataList;

/**
 * AdminToolsMenuPanel.java
 * Menu panel for admin tools GUI.
 *
 * @author TheDerpGamer
 * @since 03/20/2021
 */
public class AdminToolsMenuPanel extends GUIMenuPanel {

    public static final String[] usagePerms = {
            "*",
            "admin.*",
            "admin.menu.*",
            "admin.menu.open"
    };

    private GUIContentPane serverTab;
    private GUIContentPane playerTab;
    private GUIContentPane factionTab;
    private GUIContentPane entityTab;
    private GUIContentPane otherTab;
    private GUIContentPane logTab;

    private PlayerDataList playerDataList;
    private AdminLogList adminLogList;

    public AdminToolsMenuPanel(InputState inputState) {
        super(inputState, "AdminToolsPanel", 800, 500);
    }

    @Override
    public void recreateTabs() {
        orientate(GUIElement.ORIENTATION_HORIZONTAL_MIDDLE | GUIElement.ORIENTATION_VERTICAL_MIDDLE);
        guiWindow.clearTabs();

        //Server Tab
        serverTab = guiWindow.addTab("SERVER");
        serverTab.setTextBoxHeightLast(300);

        //Player Tab
        playerTab = guiWindow.addTab("PLAYER");
        playerTab.setTextBoxHeightLast(300);
        (playerDataList = new PlayerDataList(getState(), playerTab.getContent(0))).onInit();
        playerTab.getContent(0).attach(playerDataList);

        //Faction Tab
        factionTab = guiWindow.addTab("FACTION");
        factionTab.setTextBoxHeightLast(300);

        //Entity Tab
        entityTab = guiWindow.addTab("ENTITY");
        entityTab.setTextBoxHeightLast(300);

        //Other Tab
        otherTab = guiWindow.addTab("OTHER");
        otherTab.setTextBoxHeightLast(300);

        //Log Tab
        logTab = guiWindow.addTab("LOGS");
        logTab.setTextBoxHeightLast(300);
        (adminLogList = new AdminLogList(getState(), logTab.getContent(0))).onInit();
        logTab.getContent(0).attach(adminLogList);
    }
}
