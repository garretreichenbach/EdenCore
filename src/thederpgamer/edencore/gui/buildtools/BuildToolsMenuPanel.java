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
import thederpgamer.edencore.EdenCore;
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
    private GUIHorizontalButtonTablePane playerButtonPane;
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
        contentPane.setTextBoxHeightLast(450 - 28);
        contentPane.addDivider((int) (contentPane.getWidth() / 2) + 50);
        contentPane.setTextBoxHeight(1, 0, 450 - 28);
        contentPane.addNewTextBox(0, 450 - 28);
        contentPane.addNewTextBox(1, 450 - 28);

        GUIAncor statsPane = contentPane.getContent(0, 0);
        GUIAncor statsButtonPane = contentPane.getContent(0, 1);
        GUIAncor variablesPane = contentPane.getContent(1, 0);
        GUIAncor optionsButtonPane = contentPane.getContent(1, 1);

        statsGraph = new GUIGraph(getState());
        statsPane.attach(statsGraph);
    }

    private void createBuildSectorPanel() {
        GUIContentPane contentPane = guiWindow.addTab("BUILD SECTOR");
        contentPane.setTextBoxHeightLast(28);
        contentPane.addNewTextBox(0, 450 - 28);
        contentPane.addDivider((int) (contentPane.getWidth() / 2) - 30);
        contentPane.setTextBoxHeight(1, 0, 28);
        contentPane.addNewTextBox(1, 450 - 28);

        GUIAncor playerActionsPane = contentPane.getContent(0, 0);
        GUIAncor playerListPane = contentPane.getContent(0, 1);
        GUIAncor entityActionsPane = contentPane.getContent(1, 0);
        GUIAncor entityListPane = contentPane.getContent(1, 1);

        playerButtonPane = new GUIHorizontalButtonTablePane(getState(), 1, 1, playerActionsPane);
        createPlayerButtonPane();
        playerActionsPane.attach(playerButtonPane);

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

    private void createPlayerButtonPane() {
        playerButtonPane.onInit();
        int pos = 0;
        if(!DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) { //Not in build sector
            playerButtonPane.addButton(0, pos, "ENTER BUILD SECTOR", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        getState().getController().queueUIAudio("0022_menu_ui - enter");
                        try {
                            if(DataUtils.canTeleportPlayer(GameClient.getClientPlayerState())) DataUtils.movePlayerToBuildSector(GameClient.getClientPlayerState(), DataUtils.getBuildSector(GameClient.getClientPlayerState()));
                            else {
                                PlayerUtils.sendMessage(GameClient.getClientPlayerState(), "You can't teleport to your build sector right now as there are enemies nearby.");
                                EdenCore.getInstance().buildToolsControlManager.setActive(false);
                            }
                        } catch(IOException exception) {
                            LogManager.logException("Something went wrong while trying to transport player \"" + GameClient.getClientPlayerState().getName() + "\" to their build sector in " + DataUtils.getBuildSector(GameClient.getClientPlayerState()).sector.toString(), exception);
                            try {
                                DataUtils.movePlayerFromBuildSector(GameClient.getClientPlayerState());
                            } catch(IOException ignored) {
                                PlayerUtils.sendMessage(GameClient.getClientPlayerState(), "Something went wrong while trying to transport you to your build sector! Please report this issue to an admin!");
                            } finally {
                                redraw();
                            }
                        } finally {
                            redraw();
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
            pos ++;
        } else {
            playerButtonPane.addButton(0, pos, "EXIT BUILD SECTOR", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        getState().getController().queueUIAudio("0022_menu_ui - enter");
                        try {
                            DataUtils.movePlayerFromBuildSector(GameClient.getClientPlayerState());
                        } catch(IOException ignored) {

                        } finally {
                            redraw();
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
            pos ++;
        }
    }

    public void redraw() {
        EdenCore.getInstance().buildToolsControlManager.setActive(false);
        EdenCore.getInstance().buildToolsControlManager.setActive(true);
    }
}
