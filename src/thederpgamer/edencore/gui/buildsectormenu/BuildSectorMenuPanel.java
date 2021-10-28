package thederpgamer.edencore.gui.buildsectormenu;

import api.network.packets.PacketUtil;
import api.utils.gui.GUIMenuPanel;
import org.schema.schine.graphicsengine.core.GLFrame;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.network.client.RequestClientCacheUpdatePacket;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/22/2021]
 */
public class BuildSectorMenuPanel extends GUIMenuPanel {

    public BuildSectorMenuPanel(InputState inputState) {
        super(inputState, "BuildSectorMenu", GLFrame.getWidth() / 2, (int) (GLFrame.getHeight() / 2.5f));
    }

    @Override
    public void recreateTabs() {
        PacketUtil.sendPacketToServer(new RequestClientCacheUpdatePacket());
        int lastTab = guiWindow.getSelectedTab();
        guiWindow.clearTabs();

        GUIContentPane managementTab = guiWindow.addTab("MANAGEMENT");
        managementTab.setTextBoxHeightLast(GLFrame.getHeight() / 2);
        createManagementTab(managementTab);

        GUIContentPane entitiesTab = guiWindow.addTab("ENTITIES");
        entitiesTab.setTextBoxHeightLast(GLFrame.getHeight() / 2);
        createEntitiesTab(entitiesTab);

        GUIContentPane catalogTab = guiWindow.addTab("CATALOG");
        catalogTab.setTextBoxHeightLast(GLFrame.getHeight() / 2);
        createCatalogTab(catalogTab);

        guiWindow.setSelectedTab(lastTab);
    }

    private void createManagementTab(GUIContentPane contentPane) {
        contentPane.addDivider((int) (GLFrame.getWidth() / 2.5f));
        (new BuildSectorScrollableList(getState(), contentPane.getContent(0, 0), this)).onInit();
        (new BuildSectorUserScrollableList(getState(), contentPane.getContent(1, 0), this)).onInit();
    }

    private void createEntitiesTab(GUIContentPane contentPane) {

    }

    private void createCatalogTab(GUIContentPane contentPane) {
        contentPane.addDivider((int) (GLFrame.getWidth() / 2.5f));
    }
}
