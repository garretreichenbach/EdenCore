package thederpgamer.edencore.gui.buildsectormenu;

import api.common.GameClient;
import api.network.packets.PacketUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import org.schema.common.util.StringTools;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.manager.ClientCacheManager;
import thederpgamer.edencore.manager.LogManager;
import thederpgamer.edencore.network.client.RequestClientCacheUpdatePacket;
import thederpgamer.edencore.network.client.RequestEntityDeletePacket;
import thederpgamer.edencore.utils.BuildSectorUtils;
import thederpgamer.edencore.utils.DataUtils;
import thederpgamer.edencore.utils.EntityUtils;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/29/2021]
 */
public class BuildSectorEntitiesScrollableList extends ScrollableTableList<SegmentController> {

  private final BuildSectorMenuPanel panel;
  private BuildSectorData sectorData;

  public enum EntityType {
    ALL,
    SHIP,
    SPACE_STATION,
    DOCKED,
    TURRET
  }

  public BuildSectorEntitiesScrollableList(
      InputState state, BuildSectorData sectorData, GUIElement p, BuildSectorMenuPanel panel) {
    super(state, 800, 500, p);
    this.panel = panel;
    this.sectorData = sectorData;
    p.attach(this);
  }

  private GUIHorizontalButtonTablePane redrawButtonPane(
      final SegmentController segmentController, GUIAncor anchor) {
    GUIHorizontalButtonTablePane buttonPane =
        new GUIHorizontalButtonTablePane(getState(), 3, 1, anchor);
    buttonPane.onInit();
    try {
      if (sectorData == null)
        sectorData = DataUtils.getPlayerCurrentBuildSector(GameClient.getClientPlayerState());
      if (sectorData != null) {
        buttonPane.addButton(
            0,
            0,
            "WARP",
            GUIHorizontalArea.HButtonColor.YELLOW,
            new GUICallback() {
              @Override
              public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if (mouseEvent.pressedLeftMouse()
                    && segmentController.existsInState()
                    && segmentController.getSector(new Vector3i()).equals(sectorData.sector)) {
                  if (sectorData.hasPermission(
                      GameClient.getClientPlayerState().getName(), "EDIT")) {
                    getState().getController().queueUIAudio("0022_menu_ui - select 1");
                    EntityUtils.warpPlayerIntoEntity(
                        GameClient.getClientPlayerState(), segmentController);
                  } else getState().getController().queueUIAudio("0022_menu_ui - error 1");
                }
              }

              @Override
              public boolean isOccluded() {
                return !getState().getController().getPlayerInputs().isEmpty()
                    || !sectorData.hasPermission(
                        GameClient.getClientPlayerState().getName(), "EDIT");
              }
            },
            new GUIActivationCallback() {
              @Override
              public boolean isVisible(InputState inputState) {
                return true;
              }

              @Override
              public boolean isActive(InputState inputState) {
                return getState().getController().getPlayerInputs().isEmpty()
                    && sectorData.hasPermission(
                        GameClient.getClientPlayerState().getName(), "EDIT");
              }
            });

        buttonPane.addButton(
            1,
            0,
            "DELETE",
            GUIHorizontalArea.HButtonColor.RED,
            new GUICallback() {
              @Override
              public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if (mouseEvent.pressedLeftMouse()) {
                  if (segmentController.existsInState()
                      && segmentController.getSector(new Vector3i()).equals(sectorData.sector)) {
                    if (sectorData.hasPermission(
                        GameClient.getClientPlayerState().getName(), "DELETE")) {
                      // PacketUtil.sendPacketToServer(new RequestClientCacheUpdatePacket());
                      PacketUtil.sendPacketToServer(
                          new RequestEntityDeletePacket(segmentController));
                      getState().getController().queueUIAudio("0022_menu_ui - select 2");
                      /*
                      segmentController.railController.destroyDockedRecursive();
                      for (ElementDocking dock :
                          segmentController.getDockingController().getDockedOnThis()) {
                        dock.from.getSegment().getSegmentController().markForPermanentDelete(true);
                        dock.from
                            .getSegment()
                            .getSegmentController()
                            .setMarkedForDeleteVolatile(true);
                      }
                      segmentController.markForPermanentDelete(true);
                      segmentController.setMarkedForDeleteVolatile(true);
                       */
                      flagDirty();
                      handleDirty();
                    } else getState().getController().queueUIAudio("0022_menu_ui - error 1");
                  } else panel.recreateTabs();
                }
              }

              @Override
              public boolean isOccluded() {
                return !getState().getController().getPlayerInputs().isEmpty()
                    || !sectorData.hasPermission(
                        GameClient.getClientPlayerState().getName(), "DELETE");
              }
            },
            new GUIActivationCallback() {
              @Override
              public boolean isVisible(InputState inputState) {
                return true;
              }

              @Override
              public boolean isActive(InputState inputState) {
                return getState().getController().getPlayerInputs().isEmpty()
                    && sectorData.hasPermission(
                        GameClient.getClientPlayerState().getName(), "DELETE");
              }
            });

        buttonPane.addButton(
            2,
            0,
            "TOGGLE AI",
            GUIHorizontalArea.HButtonColor.BLUE,
            new GUICallback() {
              @Override
              public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if (mouseEvent.pressedLeftMouse() && segmentController.existsInState()) {
                  if (sectorData.hasPermission(
                      GameClient.getClientPlayerState().getName(), "TOGGLE_AI")) {
                    getState().getController().queueUIAudio("0022_menu_ui - select 3");
                    BuildSectorUtils.toggleAI(
                        segmentController, !segmentController.isAIControlled());
                  } else getState().getController().queueUIAudio("0022_menu_ui - error 1");
                }
              }

              @Override
              public boolean isOccluded() {
                return !getState().getController().getPlayerInputs().isEmpty()
                    || !sectorData.hasPermission(
                        GameClient.getClientPlayerState().getName(), "TOGGLE_AI");
              }
            },
            new GUIActivationCallback() {
              @Override
              public boolean isVisible(InputState inputState) {
                return true;
              }

              @Override
              public boolean isActive(InputState inputState) {
                return getState().getController().getPlayerInputs().isEmpty()
                    && sectorData.hasPermission(
                        GameClient.getClientPlayerState().getName(), "TOGGLE_AI");
              }
            });
      }
    } catch (Exception exception) {
      LogManager.logException(
          "Failed to create build sector button pane due to an exception", exception);
    }
    return buttonPane;
  }

  @Override
  protected Collection<SegmentController> getElementList() {
    if (DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
      PacketUtil.sendPacketToServer(new RequestClientCacheUpdatePacket());
      try {
        for (SimpleTransformableSendableObject<?> object :
            GameClient.getClientState().getCurrentSectorEntities().values()) {
          if (object instanceof SegmentController
              && !ClientCacheManager.sectorEntities.contains(object))
            ClientCacheManager.sectorEntities.add((SegmentController) object);
        }
      } catch (Exception exception) {
        exception.printStackTrace();
      }
      return ClientCacheManager.sectorEntities;
    } else return new ArrayList<>();
  }

  @Override
  public void initColumns() {
    addColumn(
        "Name",
        15.0f,
        new Comparator<SegmentController>() {
          @Override
          public int compare(SegmentController o1, SegmentController o2) {
            return o1.getRealName().compareTo(o2.getRealName());
          }
        });

    addColumn(
        "Faction",
        12.0f,
        new Comparator<SegmentController>() {
          @Override
          public int compare(SegmentController o1, SegmentController o2) {
            String faction1Name =
                (o1.getFactionId() <= 0) ? "NO FACTION" : o1.getFaction().getName();
            String faction2Name =
                (o2.getFactionId() <= 0) ? "NO FACTION" : o2.getFaction().getName();
            return faction1Name.compareTo(faction2Name);
          }
        });

    addColumn(
        "Mass",
        7.0f,
        new Comparator<SegmentController>() {
          @Override
          public int compare(SegmentController o1, SegmentController o2) {
            return Double.compare(o1.getMassWithDocks(), o2.getMassWithDocks());
          }
        });

    addColumn(
        "Distance",
        8.0f,
        new Comparator<SegmentController>() {
          @Override
          public int compare(SegmentController o1, SegmentController o2) {
            return Float.compare(
                EntityUtils.getDistanceFromPlayer(GameClient.getClientPlayerState(), o1),
                EntityUtils.getDistanceFromPlayer(GameClient.getClientPlayerState(), o2));
          }
        });

    addColumn(
        "Type",
        7.5f,
        new Comparator<SegmentController>() {
          @Override
          public int compare(SegmentController o1, SegmentController o2) {
            return o1.getType().getName().compareTo(o2.getType().getName());
          }
        });

    addTextFilter(
        new GUIListFilterText<SegmentController>() {
          @Override
          public boolean isOk(String s, SegmentController segmentController) {
            return segmentController.getRealName().toLowerCase().contains(s.toLowerCase());
          }
        },
        "SEARCH BY NAME",
        ControllerElement.FilterRowStyle.LEFT);

    addDropdownFilter(
        new GUIListFilterDropdown<SegmentController, EntityType>(EntityType.values()) {
          @Override
          public boolean isOk(EntityType entityType, SegmentController segmentController) {
            switch (entityType) {
              case SHIP:
                return segmentController
                        .getType()
                        .equals(SimpleTransformableSendableObject.EntityType.SHIP)
                    && segmentController.railController.isRoot();
              case SPACE_STATION:
                return segmentController
                    .getType()
                    .equals(SimpleTransformableSendableObject.EntityType.SPACE_STATION);
              case DOCKED:
                return segmentController
                        .getType()
                        .equals(SimpleTransformableSendableObject.EntityType.SHIP)
                    && segmentController.railController.isDocked();
              case TURRET:
                return segmentController
                        .getType()
                        .equals(SimpleTransformableSendableObject.EntityType.SHIP)
                    && segmentController.railController.isTurretDocked();
              case ALL:
              default:
                return true;
            }
          }
        },
        new CreateGUIElementInterface<EntityType>() {
          @Override
          public GUIElement create(EntityType entityType) {
            GUIAncor anchor = new GUIAncor(getState(), 10.0F, 24.0F);
            GUITextOverlayTableDropDown dropDown;
            (dropDown = new GUITextOverlayTableDropDown(10, 10, getState()))
                .setTextSimple(entityType.name());
            dropDown.setPos(4.0F, 4.0F, 0.0F);
            anchor.setUserPointer(entityType);
            anchor.attach(dropDown);
            return anchor;
          }

          @Override
          public GUIElement createNeutral() {
            return null;
          }
        },
        ControllerElement.FilterRowStyle.RIGHT);

    activeSortColumnIndex = 0;
  }

  @Override
  public void updateListEntries(GUIElementList guiElementList, Set<SegmentController> set) {
    guiElementList.deleteObservers();
    guiElementList.addObserver(this);
    if (DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
      for (SegmentController segmentController : set) {
        try {
          if (segmentController != null && segmentController.railController != null) {
            GUITextOverlayTable nameTextElement;
            (nameTextElement = new GUITextOverlayTable(10, 10, this.getState()))
                .setTextSimple(segmentController.getRealName());
            GUIClippedRow nameRowElement;
            (nameRowElement = new GUIClippedRow(this.getState())).attach(nameTextElement);

            GUITextOverlayTable factionTextElement;
            (factionTextElement = new GUITextOverlayTable(10, 10, this.getState()))
                .setTextSimple(
                    (segmentController.getFactionId() == 0)
                        ? "NO FACTION"
                        : segmentController.getFaction().getName());
            GUIClippedRow factionRowElement;
            (factionRowElement = new GUIClippedRow(this.getState())).attach(factionTextElement);

            GUITextOverlayTable massTextElement;
            (massTextElement = new GUITextOverlayTable(10, 10, this.getState()))
                .setTextSimple(StringTools.massFormat(segmentController.getMassWithDocks()));
            GUIClippedRow massRowElement;
            (massRowElement = new GUIClippedRow(this.getState())).attach(massTextElement);

            GUITextOverlayTable distanceTextElement;
            (distanceTextElement = new GUITextOverlayTable(10, 10, this.getState()))
                .setTextSimple(
                    StringTools.formatDistance(
                        EntityUtils.getDistanceFromPlayer(
                            GameClient.getClientPlayerState(), segmentController)));
            GUIClippedRow distanceRowElement;
            (distanceRowElement = new GUIClippedRow(this.getState())).attach(distanceTextElement);

            GUITextOverlayTable typeTextElement;
            (typeTextElement = new GUITextOverlayTable(10, 10, this.getState()))
                .setTextSimple(segmentController.getTypeString().toUpperCase());
            GUIClippedRow typeRowElement;
            (typeRowElement = new GUIClippedRow(this.getState())).attach(typeTextElement);

            BuildSectorEntityListRow listRow =
                new BuildSectorEntityListRow(
                    getState(),
                    segmentController,
                    nameRowElement,
                    factionRowElement,
                    massRowElement,
                    distanceRowElement,
                    typeRowElement);
            GUIAncor anchor = new GUIAncor(getState(), 1160, 28.0f);
            anchor.attach(redrawButtonPane(segmentController, anchor));
            listRow.expanded = new GUIElementList(getState());
            listRow.expanded.add(new GUIListElement(anchor, getState()));
            listRow.expanded.attach(anchor);
            listRow.onInit();
            guiElementList.addWithoutUpdate(listRow);
          }
        } catch (Exception exception) {
          LogManager.logException(
              "Encountered an exception while trying to update build sector entities", exception);
        }
      }
    }
    guiElementList.updateDim();
  }

  public class BuildSectorEntityListRow extends ScrollableTableList<SegmentController>.Row {

    public BuildSectorEntityListRow(
        InputState state, SegmentController segmentController, GUIElement... elements) {
      super(state, segmentController, elements);
      this.highlightSelect = true;
      this.highlightSelectSimple = true;
      this.setAllwaysOneSelected(true);
    }
  }
}
