package thederpgamer.edencore.gui.buildsectormenu;

import api.common.GameClient;
import api.network.packets.PacketUtil;
import api.utils.game.PlayerUtils;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import org.schema.game.common.controller.SegmentController;
import org.schema.schine.graphicsengine.core.GLFrame;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.manager.ClientCacheManager;
import thederpgamer.edencore.network.client.buildsector.RequestMoveFromBuildSectorPacket;
import thederpgamer.edencore.network.client.buildsector.RequestMoveToBuildSectorPacket;
import thederpgamer.edencore.utils.DataUtils;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/22/2021]
 */
public class BuildSectorScrollableList extends ScrollableTableList<BuildSectorData> {

  private final BuildSectorMenuPanel panel;
  private final GUIElement p;

  public BuildSectorScrollableList(InputState state, GUIElement p, BuildSectorMenuPanel panel) {
    super(state, (float) (GLFrame.getWidth() / 1.5), (float) (GLFrame.getHeight() / 1.5), p);
    this.p = p;
    this.panel = panel;
    p.attach(this);
  }

  private GUIHorizontalButtonTablePane redrawButtonPane(
      final BuildSectorData sectorData, GUIAncor anchor) {
    GUIHorizontalButtonTablePane buttonPane =
        new GUIHorizontalButtonTablePane(getState(), 2, 1, anchor);
    buttonPane.onInit();

    buttonPane.addButton(
        0,
        0,
        "ENTER SECTOR",
        GUIHorizontalArea.HButtonColor.BLUE,
        new GUICallback() {
          @Override
          public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
            if (mouseEvent.pressedLeftMouse()
                && sectorData.hasPermission(GameClient.getClientPlayerState().getName(), "ENTER")
                && !DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
              if (PlayerUtils.getCurrentControl(GameClient.getClientPlayerState())
                  instanceof SegmentController)
                PlayerUtils.sendMessage(
                    GameClient.getClientPlayerState(), "You can't do this while in an entity.");
              else {
                PacketUtil.sendPacketToServer(new RequestMoveToBuildSectorPacket(sectorData));
                panel.recreateTabs();
              }
            }
          }

          @Override
          public boolean isOccluded() {
            return DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())
                || !getState().getController().getPlayerInputs().isEmpty();
          }
        },
        new GUIActivationCallback() {
          @Override
          public boolean isVisible(InputState inputState) {
            return true;
          }

          @Override
          public boolean isActive(InputState inputState) {
            return !DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())
                && getState().getController().getPlayerInputs().isEmpty();
          }
        });

    buttonPane.addButton(
        1,
        0,
        "LEAVE SECTOR",
        GUIHorizontalArea.HButtonColor.ORANGE,
        new GUICallback() {
          @Override
          public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
            if (mouseEvent.pressedLeftMouse()
                && DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
              if (PlayerUtils.getCurrentControl(GameClient.getClientPlayerState())
                  instanceof SegmentController)
                PlayerUtils.sendMessage(
                    GameClient.getClientPlayerState(), "You can't do this while in an entity.");
              else {
                PacketUtil.sendPacketToServer(new RequestMoveFromBuildSectorPacket());
                panel.recreateTabs();
              }
            }
          }

          @Override
          public boolean isOccluded() {
            return !DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())
                || !getState().getController().getPlayerInputs().isEmpty();
          }
        },
        new GUIActivationCallback() {
          @Override
          public boolean isVisible(InputState inputState) {
            return true;
          }

          @Override
          public boolean isActive(InputState inputState) {
            return DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())
                && getState().getController().getPlayerInputs().isEmpty();
          }
        });
    return buttonPane;
  }

  @Override
  protected Collection<BuildSectorData> getElementList() {
    if (ClientCacheManager.accessibleSectors.isEmpty())
      ClientCacheManager.accessibleSectors.add(
          DataUtils.getBuildSector(GameClient.getClientPlayerState().getName()));
    return ClientCacheManager.accessibleSectors;
  }

  @Override
  public void initColumns() {
    addColumn(
        "Sectors",
        15.0f,
        new Comparator<BuildSectorData>() {
          @Override
          public int compare(BuildSectorData o1, BuildSectorData o2) {
            return o1.ownerName.compareTo(o2.ownerName);
          }
        });

    /*
    addColumn("Type", 10.0f, new Comparator<BuildSectorData>() {
        @Override
        public int compare(BuildSectorData o1, BuildSectorData o2) {
            return 0;
        }
    });
     */

    addTextFilter(
        new GUIListFilterText<BuildSectorData>() {
          @Override
          public boolean isOk(String s, BuildSectorData buildSectorData) {
            return buildSectorData.ownerName.toLowerCase().contains(s.toLowerCase());
          }
        },
        "SEARCH BY OWNER",
        ControllerElement.FilterRowStyle.FULL);
  }

  @Override
  public void updateListEntries(GUIElementList guiElementList, Set<BuildSectorData> set) {
    guiElementList.deleteObservers();
    guiElementList.addObserver(this);
    for (BuildSectorData sectorData : set) {
      if (sectorData != null
          && sectorData.hasPermission(GameClient.getClientPlayerState().getName(), "ENTER")) {
        GUITextOverlayTable ownerTextElement;
        (ownerTextElement = new GUITextOverlayTable(10, 10, this.getState()))
            .setTextSimple(sectorData.ownerName + "'s Build Sector");
        GUIClippedRow ownerRowElement;
        (ownerRowElement = new GUIClippedRow(this.getState())).attach(ownerTextElement);

        BuildSectorScrollableListRow listRow =
            new BuildSectorScrollableListRow(getState(), sectorData, ownerRowElement);
        GUIAncor anchor = new GUIAncor(getState(), p.getWidth() - 28.0f, 28.0f);
        anchor.attach(redrawButtonPane(sectorData, anchor));
        listRow.expanded = new GUIElementList(getState());
        listRow.expanded.add(new GUIListElement(anchor, getState()));
        listRow.expanded.attach(anchor);
        listRow.onInit();
        guiElementList.addWithoutUpdate(listRow);
      }
    }
    guiElementList.updateDim();
  }

  public class BuildSectorScrollableListRow extends ScrollableTableList<BuildSectorData>.Row {

    public BuildSectorScrollableListRow(
        InputState state, BuildSectorData sectorData, GUIElement... elements) {
      super(state, sectorData, elements);
      this.highlightSelect = true;
      this.highlightSelectSimple = true;
      this.setAllwaysOneSelected(true);
    }

    @Override
    public void extended() {
      if (!isOccluded()) super.extended();
      else super.unexpend();
    }

    @Override
    public void collapsed() {
      if (!isOccluded()) super.collapsed();
      else super.extended();
    }

    @Override
    public boolean isOccluded() {
      return panel.textInput != null && panel.textInput.isActive();
    }
  }
}
