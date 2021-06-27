package thederpgamer.soe;

import api.common.GameClient;
import api.listener.Listener;
import api.listener.events.gui.GUITopBarCreateEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.gui.ModGUIHandler;
import org.schema.game.client.view.gui.newgui.GUITopBar;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIActivationHighlightCallback;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.input.InputState;
import thederpgamer.soe.gui.admintools.AdminToolsControlManager;
import thederpgamer.soe.manager.ConfigManager;
import thederpgamer.soe.manager.LogManager;

/**
 * Main class for EdenCore mod.
 *
 * @author TheDerpGamer
 * @since 06/27/2021
 */
public class EdenCore extends StarMod {

    //Instance
    private static EdenCore instance;
    public static EdenCore getInstance() {
        return instance;
    }
    public EdenCore() {

    }
    public static void main(String[] args) {

    }

    //Data
    private AdminToolsControlManager adminToolsControlManager;

    @Override
    public void onEnable() {
        instance = this;
        ConfigManager.initialize(this);
        LogManager.initialize();
        registerListeners();
    }

    private void registerListeners() {
        StarLoader.registerListener(GUITopBarCreateEvent.class, new Listener<GUITopBarCreateEvent>() {
            @Override
            public void onEvent(final GUITopBarCreateEvent event) {
                if(GameClient.getClientPlayerState().isAdmin()) {
                    GUITopBar.ExpandedButton dropDownButton = event.getDropdownButtons().get(event.getDropdownButtons().size() - 1);
                    dropDownButton.addExpandedButton("ADMIN TOOLS", new GUICallback() {
                        @Override
                        public void callback(final GUIElement guiElement, MouseEvent mouseEvent) {
                            if(mouseEvent.pressedLeftMouse()) {
                                GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
                                if(adminToolsControlManager == null) {
                                    adminToolsControlManager = new AdminToolsControlManager(GameClient.getClientState());
                                    ModGUIHandler.registerNewControlManager(getSkeleton(), adminToolsControlManager);
                                }
                                adminToolsControlManager.setActive(true);
                            }
                        }

                        @Override
                        public boolean isOccluded() {
                            return false;
                        }
                    }, new GUIActivationHighlightCallback() {
                        @Override
                        public boolean isHighlighted(InputState inputState) {
                            return false;
                        }

                        @Override
                        public boolean isVisible(InputState inputState) {
                            return true;
                        }

                        @Override
                        public boolean isActive(InputState inputState) {
                            return true;
                        }
                    });
                }
            }
        }, this);
    }
}
