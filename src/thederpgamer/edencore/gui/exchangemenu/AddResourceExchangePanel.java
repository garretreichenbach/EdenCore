package thederpgamer.edencore.gui.exchangemenu;

import api.common.GameClient;
import api.utils.gui.GUIInputDialogPanel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.schema.game.client.data.GameClientState;
import org.schema.game.client.view.gui.GUIBlockSprite;
import org.schema.game.client.view.gui.advanced.tools.*;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;
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
 * @version 1.0 - [09/18/2021]
 */
public class AddResourceExchangePanel extends GUIInputDialogPanel {

  private GUIContentPane contentPane;

  public short barId;
  public String currentBarText = "";

  public short itemId;
  private boolean itemTextChanged;
  public String currentItemAmountText = "";

  public AddResourceExchangePanel(InputState inputState, GUICallback guiCallback) {
    super(
        inputState, "resource_exchange_add_panel", "Add Exchange Entry", "", 650, 350, guiCallback);
  }

  @Override
  public void onInit() {
    super.onInit();
    contentPane = ((GUIDialogWindow) background).getMainContentPane();
    contentPane.setTextBoxHeightLast((int) getHeight());

    addTextBar(
        new TextBarResult() {

          @Override
          public TextBarCallback initCallback() {
            return callback;
          }

          @Override
          public String getToolTipText() {
            return "Enter price";
          }

          @Override
          public String getName() {
            return "Price";
          }

          @Override
          public String onTextChanged(String text) {
            String t = text.trim();
            if (!t.equals(currentBarText)) currentBarText = t;
            return text;
          }
        },
        30);

    addDropdown(
        new DropdownResult() {
          private List<GUIElement> bars;

          @Override
          public DropdownCallback initCallback() {
            return new DropdownCallback() {
              @Override
              public void onChanged(Object value) {
                if (value instanceof ElementInformation)
                  barId = ((ElementInformation) value).getId();
              }
            };
          }

          @Override
          public String getToolTipText() {
            return "Select bar type";
          }

          @Override
          public String getName() {
            return "Bar type";
          }

          @Override
          public boolean needsListUpdate() {
            return false;
          }

          @Override
          public Collection<? extends GUIElement> getDropdownElements(GUIElement guiElement) {
            bars = getBars();
            return bars;
          }

          @Override
          public int getDropdownHeight() {
            return 26;
          }

          @Override
          public Object getDefault() {
            if (barId != 0 && bars.size() > 0) return bars.get(0);
            return null;
          }

          @Override
          public void flagListNeedsUpdate(boolean flag) {}
        },
        60);

    addTextBar(
        new TextBarResult() {

          @Override
          public TextBarCallback initCallback() {
            return callback;
          }

          @Override
          public String getToolTipText() {
            return "Enter resource amount";
          }

          @Override
          public String getName() {
            return "Resource amount";
          }

          @Override
          public String onTextChanged(String text) {
            String t = text.trim();
            if (!t.equals(currentItemAmountText)) {
              currentItemAmountText = t;
            }
            return text;
          }
        },
        90);

    addDropdown(
        new DropdownResult() {
          private List<GUIElement> resources;

          @Override
          public DropdownCallback initCallback() {
            return new DropdownCallback() {
              @Override
              public void onChanged(Object value) {
                if (value instanceof ElementInformation)
                  itemId = ((ElementInformation) value).getId();
              }
            };
          }

          @Override
          public String getToolTipText() {
            return "Select resource";
          }

          @Override
          public String getName() {
            return "Resource";
          }

          @Override
          public boolean needsListUpdate() {
            return itemTextChanged;
          }

          @Override
          public Collection<? extends GUIElement> getDropdownElements(GUIElement guiElement) {
            resources = getResources();
            return resources;
          }

          @Override
          public int getDropdownHeight() {
            return 26;
          }

          @Override
          public Object getDefault() {
            if (itemId != 0 && resources.size() > 0) return resources.get(0);
            return null;
          }

          @Override
          public void flagListNeedsUpdate(boolean flag) {
            itemTextChanged = flag;
          }
        },
        120);
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

  private ArrayList<GUIElement> getResources() {
    ArrayList<GUIElement> elementList = new ArrayList<>();
    GameClientState gameClientState = GameClient.getClientState();

    for (ElementInformation elementInfo : getResourcesFilter()) {

      GUIAncor anchor = new GUIAncor(gameClientState, 300.0F, 26.0F);
      elementList.add(anchor);
      GUITextOverlay textOverlay =
          new GUITextOverlay(100, 26, FontLibrary.getBoldArial12White(), gameClientState);

      textOverlay.setTextSimple(elementInfo.getName());
      anchor.setUserPointer(elementInfo);
      GUIBlockSprite blockSprite = new GUIBlockSprite(gameClientState, elementInfo.getId());
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

  private ArrayList<ElementInformation> getResourcesFilter() {
    ArrayList<ElementInformation> filter = new ArrayList<>();
    ArrayList<ElementInformation> elementList = new ArrayList<>();
    ElementKeyMap.getCategoryHirarchy()
        .getChild("Manufacturing")
        .getInfoElementsRecursive(elementList);
    for (ElementInformation info : elementList) if (!info.isDeprecated()) filter.add(info);
    return filter;
  }
}
