package thederpgamer.edencore.gui.exchangemenu;

import api.common.GameClient;
import api.utils.gui.GUIInputDialogPanel;
import java.util.ArrayList;
import org.schema.game.client.data.GameClientState;
import org.schema.game.client.view.gui.GUIBlockSprite;
import org.schema.game.client.view.gui.advanced.tools.DropdownResult;
import org.schema.game.client.view.gui.advanced.tools.GUIAdvDropdown;
import org.schema.game.client.view.gui.advanced.tools.GUIAdvTextBar;
import org.schema.game.client.view.gui.advanced.tools.TextBarResult;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.meta.MetaObjectManager;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.GUIAncor;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDialogWindow;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.element.ElementManager;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/24/2021]
 */
public class AddItemExchangePanel extends GUIInputDialogPanel {

  private GUIContentPane contentPane;

  public short barId;
  public String currentBarText = "";

  public short itemId;

  public AddItemExchangePanel(InputState inputState, GUICallback guiCallback) {
    super(inputState, "item_exchange_add_panel", "Add Exchange Entry", "", 650, 350, guiCallback);
  }

  @Override
  public void onInit() {
    super.onInit();
    contentPane = ((GUIDialogWindow) background).getMainContentPane();
    contentPane.setTextBoxHeightLast((int) getHeight());
  }

  private void addDropdown(DropdownResult result, int y) {
    GUIAdvDropdown dropDown = new GUIAdvDropdown(getState(), contentPane, result);
    dropDown.setPos(0, y, 0);
    contentPane.getContent(0).attach(dropDown);
  }

  private void addTextBar(TextBarResult textBarResult, int y) {
    GUIAdvTextBar textBar = new GUIAdvTextBar(getState(), contentPane, textBarResult);
    textBar.setPos(0, y, 0);
    contentPane.getContent(0).attach(textBar);
  }

  private ArrayList<GUIElement> getItems() {
    ArrayList<GUIElement> elementList = new ArrayList<>();
    GameClientState gameClientState = GameClient.getClientState();

    for (MetaObjectManager.MetaObjectType objectType : getItemsFilter()) {
      GUIAncor anchor = new GUIAncor(gameClientState, 300.0F, 26.0F);
      elementList.add(anchor);
      GUITextOverlay textOverlay =
          new GUITextOverlay(100, 26, FontLibrary.getBoldArial12White(), gameClientState);

      textOverlay.setTextSimple(objectType.name().toUpperCase());
      anchor.setUserPointer(objectType.name().toUpperCase());
      GUIBlockSprite blockSprite = new GUIBlockSprite(gameClientState, objectType.type);
      blockSprite.getScale().set(0.4F, 0.4F, 0.0F);
      anchor.attach(blockSprite);
      textOverlay.getPos().x = 50.0F;
      textOverlay.getPos().y = 7.0F;
      anchor.attach(textOverlay);
    }
    return elementList;
  }

  private ArrayList<GUIElement> getBars() {
    ArrayList<GUIElement> barList = new ArrayList<>();
    short[] bars =
        new short[] {
          ElementManager.getItem("Bronze Bar").getId(),
          ElementManager.getItem("Silver Bar").getId(),
          ElementManager.getItem("Gold Bar").getId()
        };
    for (short id : bars) {
      ElementInformation info = ElementKeyMap.getInfo(id);
      GUIAncor anchor = new GUIAncor(GameClient.getClientState(), 200.0f, 26.0f);

      GUITextOverlay textOverlay =
          new GUITextOverlay(
              100, 26, FontLibrary.getBoldArial12White(), GameClient.getClientState());
      textOverlay.onInit();
      textOverlay.setTextSimple(info.getName());
      anchor.setUserPointer(info);
      anchor.attach(textOverlay);

      GUIBlockSprite blockSprite = new GUIBlockSprite(GameClient.getClientState(), id);
      blockSprite.getScale().set(0.4F, 0.4F, 0.0F);
      anchor.attach(blockSprite);

      textOverlay.getPos().x = 30.0F;
      textOverlay.getPos().y = 7.0F;
      barList.add(anchor);
    }
    return barList;
  }

  private MetaObjectManager.MetaObjectType[] getItemsFilter() {
    return MetaObjectManager.MetaObjectType.values();
  }
}
