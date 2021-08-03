package thederpgamer.edencore.gui.buildtools;

import api.common.GameClient;
import api.utils.game.PlayerUtils;
import api.utils.gui.GUIMenuPanel;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIActivationCallback;
import org.schema.schine.graphicsengine.forms.gui.GUIAncor;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.graph.GUIGraph;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalArea;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalButtonTablePane;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.BuildSectorData;
import thederpgamer.edencore.gui.buildtools.buildsector.BuildSectorEntityList;
import thederpgamer.edencore.gui.buildtools.buildsector.BuildSectorPlayerList;
import thederpgamer.edencore.manager.LogManager;
import thederpgamer.edencore.utils.DataUtils;
import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/30/2021
 */
public class BuildToolsMenuPanel extends GUIMenuPanel {

    //Elements
    private GUIGraph statsGraph;
    private GUIHorizontalButtonTablePane buttonPane;
    private BuildSectorPlayerList playerList;
    private BuildSectorEntityList entityList;

    public BuildToolsMenuPanel(InputState inputState) {
        super(inputState, "BuildToolsMenuPanel", 750, 450);
    }

    @Override
    public void recreateTabs() {
        guiWindow.clearTabs();
        createStatsMenu();
        createBuildSectorPanel();
        createSpawningMenu();
    }

    private void createStatsMenu() {
        GUIContentPane contentPane = guiWindow.addTab("STATISTICS");
        contentPane.setTextBoxHeightLast(450);

        statsGraph = new GUIGraph(getState());
    }

    private void createBuildSectorPanel() {
        GUIContentPane contentPane = guiWindow.addTab("BUILD SECTOR");
        contentPane.setTextBoxHeightLast(450);
        contentPane.addNewTextBox(0, 400);
        contentPane.addDivider(375);

        GUIAncor actionsPane = contentPane.getContent(0, 0);
        GUIAncor entityListPane = contentPane.getContent(0, 1);
        GUIAncor playerListPane = contentPane.getContent(1, 1);

        buttonPane = new GUIHorizontalButtonTablePane(getState(), 1, 1, actionsPane);
        createButtonPane(buttonPane);
        actionsPane.attach(buttonPane);

        entityList = new BuildSectorEntityList(getState(), 375, 400, entityListPane);
        entityList.onInit();
        entityListPane.attach(entityList);

        playerList = new BuildSectorPlayerList(getState(), 375, 400, playerListPane);
        playerList.onInit();
        playerListPane.attach(playerList);
    }

    private void createSpawningMenu() {
        GUIContentPane contentPane = guiWindow.addTab("SPAWNING");
        contentPane.setTextBoxHeightLast(450);
    }

    private void createButtonPane(GUIHorizontalButtonTablePane buttonPane) {
        buttonPane.onInit();
        BuildSectorData sectorData = DataUtils.getPlayerCurrentBuildSector(GameClient.getClientPlayerState());
        int pos = 0;
        if(sectorData == null) { //Not in build sector
            buttonPane.addButton(0, pos, "ENTER BUILD SECTOR", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        getState().getController().queueUIAudio("0022_menu_ui - enter");
                        try {
                            DataUtils.movePlayerToBuildSector(GameClient.getClientPlayerState(), DataUtils.getBuildSector(GameClient.getClientPlayerState()));
                        } catch(IOException exception) {
                            LogManager.logException("Something went wrong while trying to transport player " + GameClient.getClientPlayerState().getName() + " to their build sector in " + DataUtils.getBuildSector(GameClient.getClientPlayerState()), exception);
                            DataUtils.movePlayerFromBuildSector(GameClient.getClientPlayerState());
                            PlayerUtils.sendMessage(GameClient.getClientPlayerState(), "Something went wrong while trying to transport you to your build sector! Please report this issue to an admin!");
                        }
                        refresh();
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
            pos ++;
        } else {
            buttonPane.addButton(0, pos, "EXIT BUILD SECTOR", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        getState().getController().queueUIAudio("0022_menu_ui - enter");
                        DataUtils.movePlayerFromBuildSector(GameClient.getClientPlayerState());
                        refresh();
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
            pos ++;
        }
    }

    public void refresh() {
        entityList.flagDirty();
        entityList.handleDirty();
        playerList.flagDirty();
        playerList.handleDirty();
        createButtonPane(buttonPane);
    }
}
