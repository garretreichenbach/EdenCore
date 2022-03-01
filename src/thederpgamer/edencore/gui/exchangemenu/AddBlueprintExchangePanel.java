package thederpgamer.edencore.gui.exchangemenu;

import api.common.GameClient;
import api.utils.gui.GUIInputDialogPanel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.schema.game.client.view.gui.GUIBlockSprite;
import org.schema.game.client.view.gui.advanced.tools.*;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.server.controller.BluePrintController;
import org.schema.game.server.data.blueprintnw.BlueprintEntry;
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
 * @version 1.0 - [09/17/2021]
 */
public class AddBlueprintExchangePanel extends GUIInputDialogPanel {

  private GUIContentPane contentPane;

  public short barId;
  public String currentBarText = "";

  public BlueprintEntry blueprintEntry;
  private boolean bpTextChanged;
  private String currentBPText = "";

  public String currentIconText = "";

  public AddBlueprintExchangePanel(InputState inputState, GUICallback guiCallback) {
    super(
        inputState,
        "blueprint_exchange_add_panel",
        "Add Exchange Entry",
        "",
        650,
        350,
        guiCallback);
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
        0);

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
        30);

    addTextBar(
        new TextBarResult() {

          @Override
          public TextBarCallback initCallback() {
            return callback;
          }

          @Override
          public String getToolTipText() {
            return "Search for blueprint";
          }

          @Override
          public String getName() {
            return "Search by name";
          }

          @Override
          public String onTextChanged(String text) {
            String t = text.trim();
            if (!t.equals(currentBPText)) {
              currentBPText = t;
              bpTextChanged = true;
            }
            return text;
          }
        },
        60);

    addDropdown(
        new DropdownResult() {
          private List<GUIElement> blueprints;

          @Override
          public DropdownCallback initCallback() {
            return new DropdownCallback() {
              @Override
              public void onChanged(Object value) {
                if (value instanceof BlueprintEntry) blueprintEntry = (BlueprintEntry) value;
              }
            };
          }

          @Override
          public String getToolTipText() {
            return "Select blueprint";
          }

          @Override
          public String getName() {
            return "Blueprint";
          }

          @Override
          public boolean needsListUpdate() {
            return bpTextChanged;
          }

          @Override
          public Collection<? extends GUIElement> getDropdownElements(GUIElement guiElement) {
            blueprints = getBlueprints();
            return blueprints;
          }

          @Override
          public int getDropdownHeight() {
            return 26;
          }

          @Override
          public Object getDefault() {
            if (blueprintEntry != null && blueprints.size() > 0) return blueprints.get(0);
            return null;
          }

          @Override
          public void flagListNeedsUpdate(boolean flag) {
            bpTextChanged = flag;
          }
        },
        90);

    addTextBar(
        new TextBarResult() {

          @Override
          public TextBarCallback initCallback() {
            return callback;
          }

          @Override
          public String getToolTipText() {
            return "Enter icon link";
          }

          @Override
          public String getName() {
            return "Icon link";
          }

          @Override
          public String onTextChanged(String text) {
            String t = text.trim();
            if (!t.equals(currentIconText)) currentIconText = t;
            return text;
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

  private ArrayList<GUIElement> getBlueprints() {
    ArrayList<GUIElement> blueprintList = new ArrayList<>();
    List<BlueprintEntry> blueprints = BluePrintController.active.readBluePrints();
    for (BlueprintEntry entry : blueprints) {
      if (currentBPText.isEmpty()
          || entry.getName().toLowerCase().contains(currentBPText.toLowerCase())) {
        GUIAncor anchor = new GUIAncor(GameClient.getClientState(), 300.0f, 26.0f);
        GUITextOverlay textOverlay =
            new GUITextOverlay(
                100, 26, FontLibrary.getBoldArial12White(), GameClient.getClientState());
        textOverlay.onInit();
        textOverlay.setTextSimple(entry.getName());
        anchor.setUserPointer(entry);
        anchor.attach(textOverlay);
        textOverlay.getPos().x = 7.0F;
        textOverlay.getPos().y = 7.0F;
        blueprintList.add(anchor);
      }
    }
    return blueprintList;
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
}
