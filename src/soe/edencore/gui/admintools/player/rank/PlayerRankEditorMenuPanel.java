package soe.edencore.gui.admintools.player.rank;

import api.common.GameClient;
import api.utils.gui.GUIMenuPanel;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIActivationCallback;
import org.schema.schine.graphicsengine.forms.gui.GUIAncor;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalArea;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalButtonTablePane;
import org.schema.schine.input.InputState;
import soe.edencore.data.player.PlayerData;
import soe.edencore.data.player.PlayerRank;
import soe.edencore.server.ServerDatabase;

/**
 * PlayerRankEditorMenuPanel.java
 * <Description>
 *
 * @author TheDerpGamer
 * @since 03/20/2021
 */
public class PlayerRankEditorMenuPanel extends GUIMenuPanel {

    private PlayerData playerData;
    private PlayerRankList playerRankList;
    private GUIHorizontalButtonTablePane buttonPane;

    public PlayerRankEditorMenuPanel(InputState inputState, PlayerData playerData) {
        super(inputState, "PlayerRankEditorMenuPanel", 800, 500);
        this.playerData = playerData;
    }

    @Override
    public void recreateTabs() {
        orientate(GUIElement.ORIENTATION_HORIZONTAL_MIDDLE | GUIElement.ORIENTATION_VERTICAL_MIDDLE);
        guiWindow.clearTabs();
        GUIContentPane rankTab = guiWindow.addTab("EDIT RANK");
        rankTab.setTextBoxHeightLast((int) (getHeight() - 110));

        playerRankList = new PlayerRankList(getState(), rankTab.getContent(0), playerData);

        rankTab.addNewTextBox(30);
        (buttonPane = new GUIHorizontalButtonTablePane(getState(), 1, 1, rankTab.getContent(1))).onInit();

        buttonPane.addButton(0, 0, "NEW RANK", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent event) {
                if(event.pressedLeftMouse()) {
                    getState().getController().queueUIAudio("0022_menu_ui - enter");
                    (new NewRankDialog(GameClient.getClientState())).activate();
                }
            }

            @Override
            public boolean isOccluded() {
                return false;
            }
        }, new GUIActivationCallback() {
            @Override
            public boolean isVisible(InputState inputState) {
                return true;
            }

            @Override
            public boolean isActive(InputState inputState) {
                return true;
            }
        });

        buttonPane.addButton(1, 0, "DELETE RANK", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent event) {
                if(event.pressedLeftMouse()) {
                    if(playerRankList.getSelectedRow() != null && playerRankList.getSelectedRow().f != null) {
                        PlayerRank selectedRank = playerRankList.getSelectedRow().f;
                        if(selectedRank != ServerDatabase.getDefaultRank()) {
                            getState().getController().queueUIAudio("0022_menu_ui - cancel");
                            ServerDatabase.removeRank(selectedRank);
                            playerRankList.redraw();
                        } else {
                            getState().getController().queueUIAudio("0022_menu_ui - error 1");
                        }
                    }
                }

            }

            @Override
            public boolean isOccluded() {
                return false;
            }
        }, new GUIActivationCallback() {
            @Override
            public boolean isVisible(InputState inputState) {
                return true;
            }

            @Override
            public boolean isActive(InputState inputState) {
                return true;
            }
        });

        rankTab.getContent(1).attach(buttonPane);
    }
}
