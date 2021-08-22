package thederpgamer.edencore.gui.admintools;

import api.utils.gui.GUIMenuPanel;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.gui.admintools.playermenu.PlayerScrollableList;

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
        final GUIContentPane playerMenu = guiWindow.addTab("PLAYERS");
        playerMenu.setTextBoxHeightLast(450);

        final PlayerScrollableList playerList = new PlayerScrollableList(getState(), playerMenu.getWidth(), playerMenu.getHeight(), playerMenu.getContent(0));
        playerList.onInit();
        playerMenu.getContent(0).attach(playerList);

        /*
        if(ConfigManager.getMainConfig().getList("operators").contains(GameClient.getClientPlayerState().getName())) {
            playerMenu.addNewTextBox(0, 22);
            GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 1, 1, playerMenu.getContent(1));
            buttonPane.onInit();

            /*
            buttonPane.addButton(0, 0, "ADD PLAYER", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
                        final String[] inputArray = new String[1];
                        (new PlayerGameTextInput("ADD_PLAYER_NAME_INPUT", GameClient.getClientState(), 30, "Player Name", "") {
                            @Override
                            public String[] getCommandPrefixes() {
                                return null;
                            }

                            @Override
                            public String handleAutoComplete(String s, TextCallback textCallback, String s1) throws PrefixNotFoundException {
                                return null;
                            }

                            @Override
                            public void onFailedTextCheck(String s) {

                            }

                            @Override
                            public void onDeactivate() {

                            }

                            @Override
                            public boolean onInput(String s) {
                                if(s != null && s.length() > 0) {
                                    if(!GameServer.getServerState().getPlayerStatesByName().containsKey(s)) {
                                        inputArray[0] = s;
                                        GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
                                        return true;
                                    } else {
                                        GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - error 1");
                                        return false;
                                    }
                                } else return false;
                            }
                        }).activate();

                        String newPlayerName = inputArray[0];
                        GameServer.getServerState().getPlayerStatesByName().put(newPlayerName, new PlayerState(GameServer.getServerState()));
                        GameServer.getServerState().getController().broadcastMessage(Lng.astr("%s has joined the game", newPlayerName), ServerMessage.MESSAGE_TYPE_SIMPLE);
                        playerList.flagDirty();
                        playerList.handleDirty();
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

            buttonPane.addButton(1, 0, "REMOVE PLAYER", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        if(playerList.getSelectedRow() != null && playerList.getSelectedRow().f != null && PlayerUtils.getPlayerType(playerList.getSelectedRow().f).equals(PlayerUtils.PlayerType.FAKE)) {
                            GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - cancel");
                            GameServer.getServerState().getPlayerStatesByName().remove(playerList.getSelectedRow().f.getName());
                            GameServer.getServerState().getController().broadcastMessage(Lng.astr("%s has left the game", playerList.getSelectedRow().f), ServerMessage.MESSAGE_TYPE_SIMPLE);
                            playerList.flagDirty();
                            playerList.handleDirty();
                        } else GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - error 1");
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

            playerMenu.getContent(1).attach(buttonPane);
        }
         */
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