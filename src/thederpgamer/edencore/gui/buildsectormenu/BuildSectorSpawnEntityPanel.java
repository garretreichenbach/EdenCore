package thederpgamer.edencore.gui.buildsectormenu;

import api.common.GameClient;
import api.utils.gui.GUIInputDialogPanel;
import org.schema.game.client.view.gui.advanced.tools.*;
import org.schema.game.common.data.player.faction.FactionManager;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDialogWindow;
import org.schema.schine.input.InputState;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/01/2022]
 */
public class BuildSectorSpawnEntityPanel extends GUIInputDialogPanel {

  private GUIContentPane contentPane;
  private String currentNameText = "";
  private boolean spawnAsOwnFaction = true;
  private boolean spawnDocked = false;
  private boolean active = false;

  public BuildSectorSpawnEntityPanel(InputState inputState, GUICallback guiCallback) {
    super(inputState, "build_sector_entity_spawn_panel", "Spawn Entity", "", 600, 150, guiCallback);
  }

  @Override
  public boolean isActive() {
    return super.isActive() && active;
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
            return "Enter Name";
          }

          @Override
          public String getName() {
            return "Name";
          }

          @Override
          public String onTextChanged(String text) {
            String t = text.trim();
            if (!t.equals(currentNameText)) currentNameText = t;
            return text;
          }
        });

    addCheckbox(
        new CheckboxResult() {
          @Override
          public boolean getCurrentValue() {
            return spawnAsOwnFaction;
          }

          @Override
          public void setCurrentValue(boolean b) {
            spawnAsOwnFaction = b;
          }

          @Override
          public boolean getDefault() {
            return spawnAsOwnFaction;
          }

          @Override
          public CheckboxCallback initCallback() {
            return callback;
          }

          @Override
          public String getName() {
            return "Spawn as own faction";
          }

          @Override
          public String getToolTipText() {
            return "Spawn as own faction";
          }
        },
        0);

    addCheckbox(
        new CheckboxResult() {
          @Override
          public boolean getCurrentValue() {
            return spawnDocked;
          }

          @Override
          public void setCurrentValue(boolean b) {
            spawnDocked = b;
          }

          @Override
          public boolean getDefault() {
            return spawnDocked;
          }

          @Override
          public CheckboxCallback initCallback() {
            return callback;
          }

          @Override
          public String getName() {
            return "Spawn on rail dock";
          }

          @Override
          public String getToolTipText() {
            return "Spawn on rail dock";
          }
        },
        300);
    active = true;
  }

  @Override
  public void draw() {
    super.draw();
    active = true;
  }

  @Override
  public void cleanUp() {
    super.cleanUp();
    active = false;
  }

  private void addTextBar(TextBarResult textBarResult) {
    GUIAdvTextBar textBar = new GUIAdvTextBar(getState(), contentPane, textBarResult);
    textBar.setPos(0, 0, 0);
    contentPane.getContent(0).attach(textBar);
  }

  private void addCheckbox(CheckboxResult checkboxResult, int xPos) {
    GUIAdvCheckbox checkbox = new GUIAdvCheckbox(getState(), contentPane, checkboxResult);
    checkbox.setPos(xPos, 30, 0);
    contentPane.getContent(0).attach(checkbox);
  }

  public String getSpawnName() {
    return currentNameText;
  }

  public void setSpawnName(String spawnName) {
    currentNameText = spawnName;
  }

  public int spawnAsFaction() {
    if (spawnAsOwnFaction) return GameClient.getClientPlayerState().getFactionId();
    else return FactionManager.ID_NEUTRAL;
  }

  public boolean spawnDocked() {
    return spawnDocked;
  }
}
