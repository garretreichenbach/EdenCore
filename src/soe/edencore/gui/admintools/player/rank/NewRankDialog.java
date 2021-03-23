package soe.edencore.gui.admintools.player.rank;

import api.utils.gui.SimplePopup;
import org.schema.game.client.controller.PlayerInput;
import org.schema.game.client.data.GameClientState;
import org.schema.schine.graphicsengine.core.GLFW;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.input.KeyEventInterface;
import org.schema.schine.input.KeyboardMappings;
import soe.edencore.data.player.PlayerRank;
import soe.edencore.server.ServerDatabase;

/**
 * NewRankDialog.java
 * <Description>
 *
 * @author TheDerpGamer
 * @since 03/21/2021
 */
public class NewRankDialog extends PlayerInput {

    private NewRankPanel panel;
    public PlayerRank.RankType rankType;

    public NewRankDialog(GameClientState clientState) {
        super(clientState);
        rankType = PlayerRank.RankType.PLAYER;
        (panel = new NewRankPanel(getState(), this, this)).onInit();
    }

    @Override
    public void onDeactivate() {

    }

    @Override
    public void handleKeyEvent(KeyEventInterface event) {
        if(KeyboardMappings.getEventKeyState(event, getState())) {
            if(KeyboardMappings.getEventKeyRaw(event) == GLFW.GLFW_KEY_ESCAPE) {
                deactivate();
            } else {
                if(panel.active == NewRankPanel.NAME_INPUT) {
                    panel.rankNameBar.setDrawCarrier(true);
                    panel.rankPrefixBar.setDrawCarrier(false);
                    panel.rankLevelBar.setDrawCarrier(false);
                    panel.rankNameBar.getTextInput().handleKeyEvent(event);
                } else if(panel.active == NewRankPanel.PREFIX_INPUT) {
                    panel.rankNameBar.setDrawCarrier(false);
                    panel.rankPrefixBar.setDrawCarrier(true);
                    panel.rankLevelBar.setDrawCarrier(false);
                    panel.rankPrefixBar.getTextInput().handleKeyEvent(event);
                } else if(panel.active == NewRankPanel.LEVEL_INPUT) {
                    panel.rankNameBar.setDrawCarrier(false);
                    panel.rankPrefixBar.setDrawCarrier(false);
                    panel.rankLevelBar.setDrawCarrier(true);
                    panel.rankLevelBar.getTextInput().handleKeyEvent(event);
                } else {
                    panel.rankNameBar.setDrawCarrier(false);
                    panel.rankPrefixBar.setDrawCarrier(false);
                    panel.rankLevelBar.setDrawCarrier(false);
                }
            }
        }
    }

    @Override
    public void handleMouseEvent(MouseEvent event) {

    }

    @Override
    public NewRankPanel getInputPanel() {
        return panel;
    }

    @Override
    public void callback(GUIElement guiElement, MouseEvent event) {
        if(event.pressedLeftMouse()) {
            if(guiElement != null && guiElement.getUserPointer() != null) {
                if(guiElement.getUserPointer().equals("CANCEL") || guiElement.getUserPointer().equals("X")) {
                    deactivate();
                } else if(guiElement.getUserPointer().equals("OK")) {
                    String rankName = panel.rankNameBar.getTextInput().getCache();
                    String rankPrefix = panel.rankPrefixBar.getTextInput().getCache();
                    String rankLevelString = panel.rankLevelBar.getTextInput().getCache();
                    int rankLevel = 0;

                    if(rankName == null || rankName.isEmpty()) {
                        getState().getController().queueUIAudio("0022_menu_ui - error 1");
                        (new SimplePopup(getState(), "Cannot Add Rank", "The rank name cannot be blank!")).activate();
                    } else if(ServerDatabase.rankExists(rankName)) {
                        getState().getController().queueUIAudio("0022_menu_ui - error 1");
                        (new SimplePopup(getState(), "Cannot Add Rank", "A rank with the name " + rankName.trim() + " already exists in database!")).activate();
                    } else {
                        char[] charArray = rankName.toCharArray();
                        StringBuilder builder = new StringBuilder();
                        for(int i = 0; i < charArray.length; i ++) {
                            if(charArray[i] == '&') {
                                i ++;
                            } else {
                                builder.append(charArray[i]);
                            }
                        }
                        rankName = builder.toString().trim();
                        rankPrefix = rankPrefix.trim();
                        if(rankLevelString != null && !rankLevelString.isEmpty()) {
                            try {
                                rankLevel = Integer.parseInt(rankLevelString.trim());
                            } catch(Exception ignored) { }
                        }

                        ServerDatabase.addRank(new PlayerRank(rankName, rankPrefix, rankType, rankLevel));
                        ServerDatabase.updateGUIs();
                        deactivate();
                    }
                }
            }
        }
    }
}
