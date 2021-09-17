package thederpgamer.edencore.gui.exchangemenu;

import api.common.GameClient;
import api.utils.gui.GUIMenuPanel;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUITilePane;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.exchange.BlueprintExchangeItem;
import thederpgamer.edencore.data.exchange.ResourceExchangeItem;
import thederpgamer.edencore.data.exchange.ServerExchangeItem;

import java.util.ArrayList;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/17/2021]
 */
public class ExchangeMenuPanel extends GUIMenuPanel {

    private GUITilePane<BlueprintExchangeItem> blueprintsTilePane;
    private GUITilePane<ResourceExchangeItem> resourcesTilePane;

    public ExchangeMenuPanel(InputState inputState) {
        super(inputState, "ServerExchange", 750, 500);
    }

    @Override
    public void recreateTabs() {
        guiWindow.clearTabs();

        GUIContentPane blueprintsTab = guiWindow.addTab("BLUEPRINTS");
        blueprintsTab.setTextBoxHeightLast(500);
        createBlueprintsTab(blueprintsTab);

        GUIContentPane resourcesTab = guiWindow.addTab("RESOURCES");
        resourcesTab.setTextBoxHeightLast(500);
        createResourcesTab(resourcesTab);

        GUIContentPane exchangeTab = guiWindow.addTab("EXCHANGE");
        exchangeTab.setTextBoxHeightLast(500);
        createExchangeTab(exchangeTab);
    }

    private void createBlueprintsTab(GUIContentPane contentPane) {
        (blueprintsTilePane = new GUITilePane<>(getState(), guiWindow, 130, 180)).onInit();


        if(isAdmin()) {

        }

        contentPane.getContent(0).attach(blueprintsTilePane);
    }

    private void createResourcesTab(GUIContentPane contentPane) {
        (resourcesTilePane = new GUITilePane<>(getState(), guiWindow, 130, 180)).onInit();

        contentPane.getContent(0).attach(resourcesTilePane);
    }

    private void createExchangeTab(GUIContentPane contentPane) {

    }

    private boolean isAdmin() {
        return GameClient.getClientPlayerState().isAdmin();
    }

    private ArrayList<BlueprintExchangeItem> getBlueprints() {

    }
}