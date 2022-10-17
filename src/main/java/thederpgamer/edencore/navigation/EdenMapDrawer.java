package thederpgamer.edencore.navigation;

import api.common.GameClient;
import api.listener.Listener;
import api.listener.events.input.MousePressEvent;
import api.listener.fastevents.FastListenerCommon;
import api.listener.fastevents.GameMapDrawListener;
import api.mod.StarLoader;
import com.bulletphysics.linearmath.Transform;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.controller.PlayerGameTextInput;
import org.schema.game.client.controller.PlayerOkCancelInput;
import org.schema.game.client.data.GameClientState;
import org.schema.game.client.view.effects.ConstantIndication;
import org.schema.game.client.view.gamemap.GameMapDrawer;
import org.schema.game.client.view.gui.shiphud.HudIndicatorOverlay;
import org.schema.game.common.data.player.SavedCoordinate;
import org.schema.game.common.data.world.VoidSystem;
import org.schema.schine.common.TextCallback;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.core.settings.PrefixNotFoundException;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalArea;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalButtonTablePane;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.EdenCore;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 21.09.2021
 * TIME: 13:55
 * clientside fastutil listener that draws to the map
 */
public class EdenMapDrawer implements GameMapDrawListener {

    public static EdenMapDrawer instance;
    private static final float sectorScale = 100f/ VoidSystem.SYSTEM_SIZE;
    private static final Vector3f halfSectorOffset = new Vector3f(sectorScale/2f,sectorScale/2f,sectorScale/2f);
    private final HashMap<Sprite,MapMarker[]> sprite_to_subsprites = new HashMap<>(); //internal mapping from sprite->subsprite for drawer
    private final HashMap<Long, MapMarker> publicMarkers = new HashMap<>(); //list of all markers to draw, provided by server
    private final HashMap<Long, MapMarker> privateMarkers = new HashMap<>(); //list of all markers to draw, provided by player
    private Vector3i centerOn;
    private MapMarker selected;
    private static MarkerCustomizeMenu markerMenu;

    public EdenMapDrawer() {
        super();
        instance = this;
        FastListenerCommon.gameMapListeners.add(this);
        NavigationEventManager.clientInit();
        addMouseListener();
    }

    /**
     * will make drawables for all custom, non public savedCoordinates
     */
    public void updatePrivateMarkers() {
        privateMarkers.clear();
        ObjectArrayList<SavedCoordinate> list = GameClientState.instance.getPlayer().getSavedCoordinates();
        for (SavedCoordinate c: list) {
            MapMarker privateMarker = new MapMarker(c.getSector(),c.getName(),MapIcon.OUTPOST,new Vector4f(0,1,0,1));
            privateMarkers.put(c.getSector().code(),privateMarker);
        }
    }

    /**
     * will add the marker, requires updateLists() to become effective
     * @param marker
     */
    public void addMarker(MapMarker marker, boolean publicMarker) {
        /*
        HashMap<Long,MapMarker> map;
        if (publicMarker) {
           map = publicMarkers;
        } else {
           map = privateMarkers;
        }
        publicMarkers.put(marker.sector.code(), marker);
         */
        if(publicMarker) publicMarkers.put(marker.sector.code(), marker);
        else privateMarkers.put(marker.sector.code(), marker);
    }

    /**
     * will remove a marker from the lists. requires updateInternalList to be applied
     * @param marker
     */
    public void removeMarker(MapMarker marker) {
        removeMarker(marker.sector);
    }

    public void removeMarker(Vector3i sector) {
        publicMarkers.remove(sector.code());
        privateMarkers.remove(sector.code());
    }

    /**
     * will copy internal mapping of sprite->subsprite hashset to sprite->subsprite[]
     */
    public void updateInternalList() {

        HashMap<Sprite, HashSet<MapMarker>> sprite_to_subsprites_set = new HashMap<>();
        sprite_to_subsprites.clear();
        //collect all markers, sorted by their sprite: PUBLIC
        for (Map.Entry<Long, MapMarker> entry: publicMarkers.entrySet()) {
            Sprite sprite = entry.getValue().getSprite();
            if (sprite == null)
                continue;

            //get set
            HashSet<MapMarker> subsprites = sprite_to_subsprites_set.get(sprite);
            if (subsprites == null) {
                subsprites = new HashSet<MapMarker>();
                sprite_to_subsprites_set.put(sprite,subsprites);
            }
            subsprites.add(entry.getValue());
        }

        // PRIVATE
        for (Map.Entry<Long, MapMarker> entry: privateMarkers.entrySet()) {
            Sprite sprite = entry.getValue().getSprite();
            //get set
            HashSet<MapMarker> subsprites = sprite_to_subsprites_set.get(sprite);
            if (subsprites == null) {
                subsprites = new HashSet<MapMarker>();
                sprite_to_subsprites_set.put(sprite,subsprites);
            }
            subsprites.add(entry.getValue());
        }

        //build the sprite vs SubSprite[] list for drawing
        for (Map.Entry<Sprite,HashSet<MapMarker>> entry: sprite_to_subsprites_set.entrySet()) {
            MapMarker[] arr = entry.getValue().toArray(new MapMarker[0]);
            //TODO remove
            if (entry.getKey() == null)
                continue;

            sprite_to_subsprites.put(entry.getKey(),arr);
        }
    }

    @Override
    public void system_PreDraw(GameMapDrawer gameMapDrawer, Vector3i vector3i, boolean b) {
        if (centerOn != null) {
            gameMapDrawer.getGameMapPosition().set(centerOn.x,centerOn.y,centerOn.z,true);
            centerOn = null;
        }
    }

    @Override
    public void system_PostDraw(GameMapDrawer gameMapDrawer, Vector3i vector3i, boolean b) {
        if(markerMenu != null) markerMenu.draw();
    }

    @Override
    public void galaxy_PreDraw(GameMapDrawer gameMapDrawer) {

    }

    @Override
    public void galaxy_PostDraw(GameMapDrawer gameMapDrawer) {

    }

    @Override
    public void galaxy_DrawLines(GameMapDrawer gameMapDrawer) {
        for (MapMarker marker: publicMarkers.values()) {
            if (marker instanceof LineDrawer) {
                ((LineDrawer) marker).drawLines(gameMapDrawer);
            }
        }
    }

    @Override
    public void galaxy_DrawSprites(GameMapDrawer gameMapDrawer) {
        for (Map.Entry<Sprite,MapMarker[]> entry: sprite_to_subsprites.entrySet()) {
            for (MapMarker m: entry.getValue()) {
                m.preDraw(gameMapDrawer);
            }
            DrawUtils.drawSprite(gameMapDrawer,entry.getKey(),entry.getValue());
        }
    }

    @Override
    public void galaxy_DrawQuads(GameMapDrawer gameMapDrawer) {

    }

    public void drawLinesSector(Vector3i from, Vector3i to, Vector4f startColor, Vector4f endColor) {
        Vector3f start = posFromSector(from,false);
        Vector3f end = posFromSector(to,false);
        DrawUtils.drawFTLLine(start,end,startColor,endColor);
    }

    public HashMap<Long,MapMarker> getPublicMarkers() {
        return publicMarkers;
    }

    public void drawText(Vector3i sector, String text) {
        drawText(posFromSector(sector,true),text);
    }

    public void drawText(Vector3f mapPos, String text) {
        Transform t = new Transform();
        t.setIdentity();
        t.origin.set(mapPos);
        ConstantIndication indication = new ConstantIndication(t, Lng.str(text));
        HudIndicatorOverlay.toDrawMapTexts.add(indication);
    }

    //map navigation util stuff

    /**
     * set waypoint to this sector
     * @param sector
     */
    public void navigateTo(Vector3i sector) {
        GameClient.getClientController().getClientGameData().setWaypoint(sector);
    }

    /**
     * move map camera and selection to this sector on next draw
     * @param sector
     */
    public void centerOn(Vector3i sector) {
        centerOn = new Vector3i(sector);
        if(markerMenu != null) {
            markerMenu.cleanUp();
            markerMenu = null;
        }
    }

    public void setSelected(MapMarker selected) {
        this.selected = selected;
    }

    public void unSelect(MapMarker selected) {
        if (selected.equals(this.selected))
            this.selected = null;
    }
    private void addMouseListener() {
        StarLoader.registerListener(MousePressEvent.class, new Listener<MousePressEvent>() {
            @Override
            public void onEvent(MousePressEvent event) {
				try {
					if(GameClientState.instance != null && GameClientState.instance.getWorldDrawer() != null && GameClientState.instance.getWorldDrawer().getGameMapDrawer() != null) {
						if(GameClientState.instance.getWorldDrawer().getGameMapDrawer().isMapActive()) {
                            if(event.getRawEvent().pressedMiddleMouse()) createMarkerMenu(selected, event);
                            else if(event.getRawEvent().pressedLeftMouse()) {
                                if((markerMenu == null || !markerMenu.isMouseInside(event)) && selected != null) centerOn(selected.getSector());
                            }
                        }
					}
				} catch(NullPointerException ignored) {
				}

                //TODO make sure mouse is actually over the marker.

                //turned of auto-nav until event is more reliable. to much accidental navigation to somewhere i dont wanna go.
              //  if (event.getRawEvent().pressedRightMouse() && selected != null) {
              //      navigateTo(selected.getSector());
              //  }
            }
        }, EdenCore.getInstance());
    }

    /**
     * Menu for customizing markers.
     *
     * @param selected The marker to customize
     */
    public void createMarkerMenu(MapMarker selected, MousePressEvent event) {
        if(selected == null) {
            for(SavedCoordinate savedCoordinate : GameClient.getClientPlayerState().getSavedCoordinates()) {
                if(GameClient.getClientState().getWorldDrawer().getGameMapDrawer().getGameMapPosition().get(new Vector3i()).equals(savedCoordinate.getSector())) {
                    selected = new MapMarker(savedCoordinate.getSector(), savedCoordinate.getName(), MapIcon.OUTPOST, new Vector4f(0, 0, 1, 1));
                    selected.setDrawIndication(true);
                    addMarker(selected, false);
                    updateInternalList();
                    break;
                }
            }
        }
        if(selected != null && (selected.isCustomizable() || GameClient.getClientPlayerState().isAdmin())) {
            (markerMenu = new MarkerCustomizeMenu(selected)).onInit();
            markerMenu.setPos(event.getRawEvent().x - 5, event.getRawEvent().y + 5, 0);
            markerMenu.draw();
        }
    }

    //helper stuff //TODO move to UTIL
    public static Vector3f posFromSector(Vector3i sector, boolean isSprite) {

        Vector3f out = sector.toVector3f();
        if (isSprite) {
            out.add(new Vector3f(-VoidSystem.SYSTEM_SIZE_HALF,-VoidSystem.SYSTEM_SIZE_HALF,-VoidSystem.SYSTEM_SIZE_HALF));
        }
        out.scale(sectorScale); out.add(halfSectorOffset);
        return out;
    }

    private static Vector4f darkYellow = new Vector4f(0.5f,0.5f,0,1);
    private static Vector4f brightYellow = new Vector4f(0.97f,1.0f,0,1);
    private static Vector4f darkRed = new Vector4f(0.5f,0f,0,1);
    private static Vector4f brightRed = new Vector4f(1f,0f,0,1);
    private static Vector4f brightGreen = new Vector4f(0,1,0,1);
    private static Vector4f darkGreen = new Vector4f(0,0.5f,0,1);
    private static Vector4f grey = new Vector4f(0.5f,0.5f,0.5f,1);
    public static float scale32px = 0.2f;

    public static class MarkerCustomizeMenu extends GUIAncor {

        private MapMarker marker;

        public MarkerCustomizeMenu(MapMarker marker) {
            super(GameClient.getClientState());
            this.marker = marker;
        }

        @Override
        public void onInit() {
            super.onInit();
            GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 3, 2, this);
            buttonPane.onInit();
            buttonPane.addButton(0, 0, "CHANGE NAME", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        (new PlayerGameTextInput("PLAYER_CHANGE_MARKER_NAME_INPUT", (GameClientState) getState(), 30, "Set marker name", "Enter a new name for this marker", marker.getName()) {
                            @Override
                            public String[] getCommandPrefixes() {
                                return new String[0];
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
                                if(s != null && !s.isEmpty() && !s.equals(marker.getName())) {
                                    marker.setName(s);
                                    return true;
                                } else return false;
                            }
                        }).activate();
                    }
                }

                @Override
                public boolean isOccluded() {
                    return markerMenu == null;
                }
            }, new GUIActivationCallback() {
                @Override
                public boolean isVisible(InputState inputState) {
                    return markerMenu != null;
                }

                @Override
                public boolean isActive(InputState inputState) {
                    return markerMenu != null;
                }
            });
            buttonPane.addButton(1, 0, "CHANGE ICON", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        (new ChangeMarkerIconMenu(getState(), marker)).onInit();
                    }
                }

                @Override
                public boolean isOccluded() {
                    return markerMenu == null;
                }
            }, new GUIActivationCallback() {
                @Override
                public boolean isVisible(InputState inputState) {
                    return markerMenu != null;
                }

                @Override
                public boolean isActive(InputState inputState) {
                    return markerMenu != null;
                }
            });
            buttonPane.addButton(2, 0, "CHANGE COLOR", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        (new ChangeMarkerColorMenu(getState(), marker)).onInit();
                    }
                }

                @Override
                public boolean isOccluded() {
                    return markerMenu == null;
                }
            }, new GUIActivationCallback() {
                @Override
                public boolean isVisible(InputState inputState) {
                    return markerMenu != null;
                }

                @Override
                public boolean isActive(InputState inputState) {
                    return markerMenu != null;
                }
            });
            buttonPane.addButton(0, 1, "SHARE PLAYER", GUIHorizontalArea.HButtonColor.PINK, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        (new ShareMarkerMenu(getState(), marker, false)).onInit();
                    }
                }

                @Override
                public boolean isOccluded() {
                    return markerMenu == null;
                }
            }, new GUIActivationCallback() {
                @Override
                public boolean isVisible(InputState inputState) {
                    return markerMenu != null;
                }

                @Override
                public boolean isActive(InputState inputState) {
                    return markerMenu != null;
                }
            });
            buttonPane.addButton(1, 1, "SHARE FACTION", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        (new ShareMarkerMenu(getState(), marker, true)).onInit();
                    }
                }

                @Override
                public boolean isOccluded() {
                    return markerMenu == null;
                }
            }, new GUIActivationCallback() {
                @Override
                public boolean isVisible(InputState inputState) {
                    return markerMenu != null;
                }

                @Override
                public boolean isActive(InputState inputState) {
                    return markerMenu != null;
                }
            });
            buttonPane.addButton(2, 1, "DELETE", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        (new PlayerOkCancelInput("DELETE_MARKER", getState(), "DELETE MARKER", "Are you sure you want to delete this marker?") {
                            @Override
                            public void onDeactivate() {

                            }

                            @Override
                            public void pressedOK() {
                                EdenMapDrawer.instance.removeMarker(marker);
                            }
                        }).activate();
                    }
                }

                @Override
                public boolean isOccluded() {
                    return markerMenu == null;
                }
            }, new GUIActivationCallback() {
                @Override
                public boolean isVisible(InputState inputState) {
                    return markerMenu != null;
                }

                @Override
                public boolean isActive(InputState inputState) {
                    return markerMenu != null;
                }
            });
            attach(buttonPane);
        }

        public boolean isMouseInside(MousePressEvent event) {
            return event.getRawEvent().x > getPos().x && event.getRawEvent().x < getPos().x + getWidth() && event.getRawEvent().y > getPos().y && event.getRawEvent().y < getPos().y + getHeight();
        }

        private static class ChangeMarkerIconMenu extends GUIAncor {

            private final MapMarker marker;
            private final GUIIconButton[] icons = new GUIIconButton[MapIcon.values().length];

            public ChangeMarkerIconMenu(InputState inputState, MapMarker marker) {
                super(inputState, 16 * 8, 16 * 8);
                this.marker = marker;
            }

            @Override
            public void onInit() {
                super.onInit();
                for(int i = 0; i < icons.length; i ++) {
                    GUIOverlay overlay = new GUIOverlay(marker.icon.getSprite(), getState());
                    overlay.onInit();
                    overlay.setUserPointer(MapIcon.values()[i]);
                    icons[i] = new GUIIconButton(getState(), 16, 16, overlay, new GUICallback() {
                        @Override
                        public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                            if(mouseEvent.pressedLeftMouse()) {
                                marker.setIcon((MapIcon) guiElement.getUserPointer());
                                cleanUp();
                            }
                        }

                        @Override
                        public boolean isOccluded() {
                            return false;
                        }
                    });
                    icons[i].onInit();
                    attach(icons[i]);
                    icons[i].setPos(new Vector3f((16 * (i % 8)) + 2, 18, 0));
                }
            }
        }

        private static class ChangeMarkerColorMenu extends GUIAncor {

            private final MapMarker marker;

            public ChangeMarkerColorMenu(InputState inputState, MapMarker marker) {
                super(inputState);
                this.marker = marker;
            }

            @Override
            public void onInit() {
                super.onInit();
                GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 3, 2, this);
                buttonPane.onInit();
                buttonPane.addButton(0, 0, "RED", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
                    @Override
                    public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                        if(mouseEvent.pressedLeftMouse()) {
                            marker.setColor(new Vector4f(1, 0, 0, 1));
                            cleanUp();
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
                buttonPane.addButton(1, 0, "GREEN", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
                    @Override
                    public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                        if(mouseEvent.pressedLeftMouse()) {
                            marker.setColor(new Vector4f(0, 1, 0, 1));
                            cleanUp();
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
                buttonPane.addButton(2, 0, "BLUE", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
                    @Override
                    public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                        if(mouseEvent.pressedLeftMouse()) {
                            marker.setColor(new Vector4f(0, 0, 1, 1));
                            cleanUp();
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
                buttonPane.addButton(0, 1, "YELLOW", GUIHorizontalArea.HButtonColor.YELLOW, new GUICallback() {
                    @Override
                    public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                        if(mouseEvent.pressedLeftMouse()) {
                            marker.setColor(new Vector4f(1, 1, 0, 1));
                            cleanUp();
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
                buttonPane.addButton(1, 1, "ORANGE", GUIHorizontalArea.HButtonColor.ORANGE, new GUICallback() {
                    @Override
                    public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                        if(mouseEvent.pressedLeftMouse()) {
                            marker.setColor(new Vector4f(1, 0.5f, 0, 1));
                            cleanUp();
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
                buttonPane.addButton(2, 1, "PURPLE", GUIHorizontalArea.HButtonColor.PINK, new GUICallback() {
                    @Override
                    public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                        if(mouseEvent.pressedLeftMouse()) {
                            marker.setColor(new Vector4f(1, 0, 1, 1));
                            cleanUp();
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
            }
        }

        private static class ShareMarkerMenu extends GUIAncor {

            private final MapMarker marker;
            private final boolean toFaction;

            public ShareMarkerMenu(InputState inputState, MapMarker marker, boolean toFaction) {
                super(inputState);
                this.marker = marker;
                this.toFaction = toFaction;
            }

            @Override
            public void onInit() {
                super.onInit();
                //Create text entry, then send a packet to the target player adding the point to their map
            }
        }
    }
}
