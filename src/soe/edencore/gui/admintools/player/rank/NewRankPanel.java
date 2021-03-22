package soe.edencore.gui.admintools.player.rank;

import org.schema.game.client.view.gui.GUIInputPanel;
import org.schema.schine.common.TextAreaInput;
import org.schema.schine.common.TextCallback;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalArea;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalButtonTablePane;
import org.schema.schine.input.InputState;
import soe.edencore.data.player.PlayerRank;

/**
 * NewRankPanel.java
 * <Description>
 *
 * @author TheDerpGamer
 * @since 03/21/2021
 */
public class NewRankPanel extends GUIInputPanel {

    public static final int NONE = 0;
    public static final int NAME_INPUT = 1;
    public static final int PREFIX_INPUT = 2;
    public static final int LEVEL_INPUT = 3;
    public int active = NONE;

    private GUICallback textCallback;
    private GUICallback callback;
    private GUIAncor content;

    public GUITextInput rankNameBar;
    public GUITextInput rankPrefixBar;
    public GUITextInput rankLevelBar;
    public GUITextOverlay rankTypeOverlay;
    public GUIHorizontalButtonTablePane rankTypeButtonPane;

    public NewRankPanel(InputState inputState, GUICallback callback) {
        super("NewRankPanel", inputState, callback, "New Rank", "");
        this.callback = callback;
        this.content = new GUIAncor(getState(), 300, 450);
    }

    @Override
    public void onInit() {
        super.onInit();
        setPos(0, 0, 0);
        getPos().y -= 10;

        textCallback = new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent event) {
                if(event.pressedLeftMouse() && guiElement != null && guiElement.getUserPointer() != null) {
                    if(guiElement.getUserPointer().equals("NAME_INPUT")) {
                        active = NAME_INPUT;
                        rankNameBar.setDrawCarrier(true);
                        rankPrefixBar.setDrawCarrier(false);
                        rankLevelBar.setDrawCarrier(false);
                    } else if(guiElement.getUserPointer().equals("PREFIX_INPUT")) {
                        active = PREFIX_INPUT;
                        rankNameBar.setDrawCarrier(false);
                        rankPrefixBar.setDrawCarrier(true);
                        rankLevelBar.setDrawCarrier(false);
                    } else if(guiElement.getUserPointer().equals("LEVEL_INPUT")) {
                        active = LEVEL_INPUT;
                        rankNameBar.setDrawCarrier(false);
                        rankPrefixBar.setDrawCarrier(false);
                        rankLevelBar.setDrawCarrier(true);
                    } else {
                        active = NONE;
                        rankNameBar.setDrawCarrier(false);
                        rankPrefixBar.setDrawCarrier(false);
                        rankLevelBar.setDrawCarrier(false);
                    }
                }
            }

            @Override
            public boolean isOccluded() {
                return false;
            }
        };

        (rankNameBar = new GUITextInput(280, 30, getState())).setTextBox(true);
        rankNameBar.setTextInput(new TextAreaInput(10, 1, new TextCallback() {
            @Override
            public String[] getCommandPrefixes() {
                return new String[0];
            }

            @Override
            public String handleAutoComplete(String s, TextCallback textCallback, String s1) {
                return null;
            }

            @Override
            public void onFailedTextCheck(String s) {

            }

            @Override
            public void onTextEnter(String s, boolean b, boolean b1) {

            }

            @Override
            public void newLine() {

            }
        }));
        rankNameBar.setUserPointer("NAME_INPUT");
        rankNameBar.setCallback(textCallback);
        rankNameBar.setMouseUpdateEnabled(true);
        rankNameBar.setPreText("Player");
        content.attach(rankNameBar);

        (rankPrefixBar = new GUITextInput(280, 30, getState())).setTextBox(true);
        rankPrefixBar.setTextInput(new TextAreaInput(15, 1, new TextCallback() {
            @Override
            public String[] getCommandPrefixes() {
                return new String[0];
            }

            @Override
            public String handleAutoComplete(String s, TextCallback textCallback, String s1) {
                return null;
            }

            @Override
            public void onFailedTextCheck(String s) {

            }

            @Override
            public void onTextEnter(String s, boolean b, boolean b1) {

            }

            @Override
            public void newLine() {

            }
        }));
        rankNameBar.setUserPointer("PREFIX_INPUT");
        rankPrefixBar.setCallback(textCallback);
        rankPrefixBar.setMouseUpdateEnabled(true);
        rankPrefixBar.setPreText("&2[Player]");
        rankPrefixBar.getPos().y += rankNameBar.getHeight() + 4;
        content.attach(rankPrefixBar);

        (rankLevelBar = new GUITextInput(280, 30, getState())).setTextBox(true);
        rankLevelBar.setTextInput(new TextAreaInput(2, 1, new TextCallback() {
            @Override
            public String[] getCommandPrefixes() {
                return new String[0];
            }

            @Override
            public String handleAutoComplete(String s, TextCallback textCallback, String s1) {
                return null;
            }

            @Override
            public void onFailedTextCheck(String s) {

            }

            @Override
            public void onTextEnter(String s, boolean b, boolean b1) {

            }

            @Override
            public void newLine() {

            }
        }));
        rankNameBar.setUserPointer("LEVEL_INPUT");
        rankLevelBar.setCallback(textCallback);
        rankLevelBar.setMouseUpdateEnabled(true);
        rankLevelBar.setPreText("0");
        rankLevelBar.getPos().y += (rankNameBar.getHeight() + 4) * 2;
        content.attach(rankLevelBar);

        (rankTypeOverlay = new GUITextOverlay(280, 30, getState())).onInit();
        rankTypeOverlay.getPos().y += (rankNameBar.getHeight() + 4) * 3;
        rankTypeOverlay.setTextSimple(PlayerRank.RankType.PLAYER.toString());
        content.attach(rankTypeOverlay);

        (rankTypeButtonPane = new GUIHorizontalButtonTablePane(getState(), 1, 1, getContent())).onInit();
        rankTypeButtonPane.getPos().y += (rankNameBar.getHeight() + 4) * 4;
        rankTypeButtonPane.addButton(0, 0, "PLAYER", GUIHorizontalArea.HButtonColor.BLUE, callback, new GUIActivationCallback() {
            @Override
            public boolean isVisible(InputState inputState) {
                return true;
            }

            @Override
            public boolean isActive(InputState inputState) {
                return true;
            }
        });
        rankTypeButtonPane.addButton(1, 0, "DONATOR", GUIHorizontalArea.HButtonColor.GREEN, callback, new GUIActivationCallback() {
            @Override
            public boolean isVisible(InputState inputState) {
                return true;
            }

            @Override
            public boolean isActive(InputState inputState) {
                return true;
            }
        });
        rankTypeButtonPane.addButton(2, 0, "STAFF", GUIHorizontalArea.HButtonColor.PINK, callback, new GUIActivationCallback() {
            @Override
            public boolean isVisible(InputState inputState) {
                return true;
            }

            @Override
            public boolean isActive(InputState inputState) {
                return true;
            }
        });
        content.attach(rankTypeButtonPane);

        getContent().attach(content);
    }

    @Override
    public float getHeight() {
        return content.getHeight();
    }

    @Override
    public float getWidth() {
        return content.getWidth();
    }
}
