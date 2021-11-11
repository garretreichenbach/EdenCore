package org.schema.game.client.view.gui.catalog;

import api.common.GameClient;
import org.schema.game.client.controller.PlayerGameOkCancelInput;
import org.schema.game.client.controller.PlayerGameTextInput;
import org.schema.game.client.controller.PlayerTextAreaInput;
import org.schema.game.client.controller.manager.ingame.catalog.CatalogControlManager;
import org.schema.game.client.controller.manager.ingame.catalog.CatalogPermissionEditDialog;
import org.schema.game.client.controller.manager.ingame.catalog.CatalogRateDialog;
import org.schema.game.client.data.GameClientState;
import org.schema.game.common.data.player.BlueprintPlayerHandleRequest;
import org.schema.game.common.data.player.catalog.CatalogPermission;
import org.schema.game.network.objects.remote.RemoteBlueprintPlayerRequest;
import org.schema.game.server.data.EntityRequest;
import org.schema.game.server.data.admin.AdminCommands;
import org.schema.schine.common.InputChecker;
import org.schema.schine.common.TextCallback;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.core.settings.PrefixNotFoundException;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUITextButton;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.utils.DataUtils;

import javax.vecmath.Vector4f;

/**
 * Modified version of CatalogExtendedPanel.
 */
public class CatalogExtendedPanel extends GUIElement implements GUICallback {

    private GameClientState state;
    private CatalogPermission permission;
    private boolean showAdminSettings;

    public CatalogExtendedPanel(InputState state, CatalogPermission f, boolean showAdminSettings) {
        super(state);
        this.showAdminSettings = showAdminSettings;
        this.state = (GameClientState) getState();
        this.permission = f;
    }

    private void load() {
        getPlayerCatalogControlManager().suspend(true);
        String description = Lng.str("Please type in a name for your new Ship!");
        PlayerGameTextInput pp = new PlayerGameTextInput("CatalogExtended_load", (GameClientState) getState(), 50, Lng.str("New Ship"), description
                , permission.getUid() + "_" + System.currentTimeMillis()) {
            @Override
            public String[] getCommandPrefixes() {
                return null;
            }

            @Override
            public boolean isOccluded() {
                return getState().getController().getPlayerInputs().indexOf(this) != getState().getController().getPlayerInputs().size() - 1;
            }			@Override
            public String handleAutoComplete(String s,
                                             TextCallback callback, String prefix) {
                return s;
            }

            @Override
            public void onDeactivate() {
                getPlayerCatalogControlManager().suspend(false);
            }



            @Override
            public void onFailedTextCheck(String msg) {
                setErrorMessage("SHIPNAME INVALID: " + msg);

            }

            @Override
            public boolean onInput(String entry) {
                if (getState().getCharacter() == null || getState().getCharacter().getPhysicsDataContainer() == null || !getState().getCharacter().getPhysicsDataContainer().isInitialized()) {
                    System.err.println("[ERROR] Character might not have been initialized");
                    return false;
                }
                System.err.println("[CLIENT] BUYING CATALOG ENTRY: " + permission.getUid() + " FOR " + getState().getPlayer().getNetworkObject());

                BlueprintPlayerHandleRequest req = new BlueprintPlayerHandleRequest();
                req.catalogName = permission.getUid();
                req.entitySpawnName = entry;
                req.save = false;
                req.toSaveShip = -1;
                req.directBuy = true;

                getState().getController().sendAdminCommand(AdminCommands.LOAD, req.catalogName, req.entitySpawnName);

                //				getState().getPlayer().getNetworkObject().catalogPlayerHandleBuffer.add(new RemoteBlueprintPlayerRequest(req, false));

                return true;
            }

        };
        pp.setInputChecker(new InputChecker() {
            @Override
            public boolean check(String entry, TextCallback callback) {
                if (EntityRequest.isShipNameValid(entry)) {
                    return true;
                } else {
                    callback.onFailedTextCheck(Lng.str("Must only contain letters or numbers or (_-)!"));
                    return false;
                }
            }
        });
        pp.activate();
    }

    private void buyEntry() {

        if (!((GameClientState) getState()).isInShopDistance()) {
            ((GameClientState) getState()).getController().popupAlertTextMessage(Lng.str("ERROR:\nCannot buy!\nYou are not near a shop!"), 0);
        }

        getPlayerCatalogControlManager().suspend(true);
        String description = "Please type in a name for your new Ship!";
        PlayerGameTextInput pp = new PlayerGameTextInput("CatalogExtendedPanel_buyEntry", (GameClientState) getState(), 50, Lng.str("New Ship"), description
                , permission.getUid() + "_" + System.currentTimeMillis()) {
            @Override
            public String[] getCommandPrefixes() {
                return null;
            }

            @Override
            public String handleAutoComplete(String s,
                                             TextCallback callback, String prefix) {
                return s;
            }

            @Override
            public boolean isOccluded() {
                return getState().getController().getPlayerInputs().indexOf(this) != getState().getController().getPlayerInputs().size() - 1;
            }

            @Override
            public void onDeactivate() {
                getPlayerCatalogControlManager().suspend(false);
            }

            @Override
            public void onFailedTextCheck(String msg) {
                setErrorMessage("SHIPNAME INVALID: " + msg);

            }

            @Override
            public boolean onInput(String entry) {
                if (getState().getCharacter() == null || getState().getCharacter().getPhysicsDataContainer() == null || !getState().getCharacter().getPhysicsDataContainer().isInitialized()) {
                    System.err.println("[ERROR] Character might not have been initialized");
                    return false;
                }
                System.err.println("[CLIENT] BUYING CATALOG ENTRY: " + permission.getUid() + " FOR " + getState().getPlayer().getNetworkObject());

                BlueprintPlayerHandleRequest req = new BlueprintPlayerHandleRequest();
                req.catalogName = permission.getUid();
                req.entitySpawnName = entry;
                req.save = false;
                req.toSaveShip = -1;
                req.directBuy = true;

                getState().getPlayer().getNetworkObject().catalogPlayerHandleBuffer.add(new RemoteBlueprintPlayerRequest(req, false));

                return true;
            }

        };
        pp.setInputChecker(new InputChecker() {
            @Override
            public boolean check(String entry, TextCallback callback) {
                if (EntityRequest.isShipNameValid(entry)) {
                    return true;
                } else {
                    callback.onFailedTextCheck("Must only contain Letters or numbers or (_-)!");
                    return false;
                }
            }
        });
        pp.activate();
    }

    private void buyEntryAsMeta() {

        if (!((GameClientState) getState()).isInShopDistance()) {
            ((GameClientState) getState()).getController().popupAlertTextMessage("ERROR:\nCannot buy!\nYou are not near a shop!", 0);
        }

        (new PlayerGameOkCancelInput("CatalogExtendedPanel_buyEntryAsMeta", (GameClientState) getState(), Lng.str("Buy Blueprint %s",  permission.getUid()), Lng.str("Do you want to buy this blueprint?\nThis will put the blueprint in your\ninventory. Right click on it to provide\nthe necessary materials.")) {

            @Override
            public void onDeactivate() {
            }			@Override
            public boolean isOccluded() {
                return false;
            }

            @Override
            public void pressedOK() {
                BlueprintPlayerHandleRequest req = new BlueprintPlayerHandleRequest();
                req.catalogName = permission.getUid();
                req.entitySpawnName = "";
                req.save = false;
                req.toSaveShip = -1;
                req.directBuy = false;

                getState().getPlayer().getNetworkObject().catalogPlayerHandleBuffer.add(new RemoteBlueprintPlayerRequest(req, false));

                deactivate();
            }


        }).activate();

    }

    @Override
    public void callback(GUIElement callingGuiElement, MouseEvent event) {
        if (event.pressedLeftMouse()) {
            if ("buy".equals(callingGuiElement.getUserPointer())) {
                if (state.getGameState().isBuyBBWithCredits()) {
                    buyEntry();
                } else {
                    buyEntryAsMeta();
                }
            } else if ("description".equals(callingGuiElement.getUserPointer())) {
                editDescription();
            } else if ("permission".equals(callingGuiElement.getUserPointer())) {
                editPermission();
            } else if ("delete".equals(callingGuiElement.getUserPointer())) {
                deleteEntry();
            } else if ("owner".equals(callingGuiElement.getUserPointer())) {
                changeOwner();
            } else if ("rate".equals(callingGuiElement.getUserPointer())) {
                rate();
            } else if ("adminload".equals(callingGuiElement.getUserPointer())) {
                load();
            }

        }
    }

    @Override
    public boolean isOccluded() {
        // TODO Auto-generated method stub
        return false;
    }

    private void changeOwner() {
        getPlayerCatalogControlManager().suspend(true);
        String description = Lng.str("Change the owner of \"%s\"",  permission.getUid());
        PlayerGameTextInput pp = new PlayerGameTextInput("CatalogExtendedPanel_changeOwner", (GameClientState) getState(), 50, Lng.str("Change Owner"), description
                , permission.ownerUID) {
            @Override
            public String[] getCommandPrefixes() {
                return null;
            }

            @Override
            public String handleAutoComplete(String s,
                                             TextCallback callback, String prefix) {
                return s;
            }

            @Override
            public boolean isOccluded() {
                return getState().getController().getPlayerInputs().indexOf(this) != getState().getController().getPlayerInputs().size() - 1;
            }

            @Override
            public void onDeactivate() {
                getPlayerCatalogControlManager().suspend(false);
            }

            @Override
            public void onFailedTextCheck(String msg) {
                setErrorMessage(Lng.str("ONWER INVALID: %s",  msg));

            }

            @Override
            public boolean onInput(String entry) {
                if (showAdminSettings && getState().getPlayer().getNetworkObject().isAdminClient.get()) {
                    CatalogPermission p = new CatalogPermission(permission);
                    p.ownerUID = entry;
                    p.changeFlagForced = true;
                    getState().getCatalogManager().clientRequestCatalogEdit(p);
                } else {
                    System.err.println("ERROR: CANNOT CHANGE OWNER (PERMISSION DENIED)");
                }

                return true;
            }

        };
        pp.setInputChecker(new InputChecker() {
            @Override
            public boolean check(String entry, TextCallback callback) {
                if (EntityRequest.isShipNameValid(entry)) {
                    return true;
                } else {
                    callback.onFailedTextCheck(Lng.str("Must only contain letters or numbers or (_-)!"));
                    return false;
                }
            }
        });
        pp.activate();
    }

    @Override
    public void cleanUp() {

    }

    @Override
    public void draw() {
        drawAttached();
    }

    @Override
    public void onInit() {

        GUITextButton buy = new GUITextButton(state, 70, 20,
                                              new Vector4f(0.3f, 0.6f, 0.3f, 0.9f),
                                              new Vector4f(0.99f, 0.99f, 0.99f, 1.0f),
                                              FontLibrary.getBoldArial16White(),
                                              "BUY", this, getPlayerCatalogControlManager());
        buy.setUserPointer("buy");
        buy.setTextPos(14, 1);
        buy.getPos().y = -7;
        GUITextButton loadAdmin = new GUITextButton(state, 120, 20,
                                                    new Vector4f(0.7f, 0.2f, 0.2f, 0.9f),
                                                    new Vector4f(0.99f, 0.99f, 0.99f, 1.0f),
                                                    FontLibrary.getBoldArial16White(),
                                                    "Admin-Load", this, getPlayerCatalogControlManager()) {

            /* (non-Javadoc)
             * @see org.schema.schine.graphicsengine.forms.gui.GUITextButton#draw()
             */
            @Override
            public void draw() {
                //INSERTED CODE
                if(((GameClientState) getState()).getPlayer().getNetworkObject().isAdminClient.get() || (DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState()) && DataUtils.getPlayerCurrentBuildSector(GameClient.getClientPlayerState()).hasPermission(GameClient.getClientPlayerState().getName(), "SPAWN"))) {
                    super.draw();
                }
                //
            }

        };
        loadAdmin.setUserPointer("adminload");

        GUITextButton ownerButton = new GUITextButton(state, 80, 20, Lng.str("Owner"), this, getPlayerCatalogControlManager());
        ownerButton.setUserPointer("owner");
        ownerButton.setTextPos(5, 1);

        GUITextButton descriptionButton = new GUITextButton(state, 90, 20, Lng.str("Description"), this, getPlayerCatalogControlManager());
        descriptionButton.setUserPointer("description");
        descriptionButton.setTextPos(5, 1);

        GUITextButton permissionButton = new GUITextButton(state, 90, 20, Lng.str("Permissions"), this, getPlayerCatalogControlManager());
        permissionButton.setUserPointer("permission");
        permissionButton.setTextPos(5, 1);

        GUITextButton rateButton = new GUITextButton(state, 45, 20, Lng.str("Rate"), this, getPlayerCatalogControlManager());
        rateButton.setUserPointer("rate");
        rateButton.setTextPos(5, 1);

        GUITextButton delete = new GUITextButton(state, 65, 20,
                                                 new Vector4f(0.7f, 0.2f, 0.2f, 0.9f),
                                                 new Vector4f(0.99f, 0.99f, 0.99f, 1.0f),
                                                 FontLibrary.getRegularArial15White(),
                                                 "delete", this, getPlayerCatalogControlManager());
        delete.setUserPointer("delete");
        delete.setTextPos(6, 1);

        loadAdmin.getPos().x = buy.getPos().x;
        loadAdmin.getPos().y = buy.getPos().y + buy.getHeight() + 3;
        ownerButton.getPos().x = buy.getPos().x + 80;
        descriptionButton.getPos().x = ownerButton.getPos().x + 90;
        permissionButton.getPos().x = descriptionButton.getPos().x + 100;
        rateButton.getPos().x = permissionButton.getPos().x + 100;
        delete.getPos().x = rateButton.getPos().x + 70;

        GUITextOverlay description = new GUITextOverlay(10, 10, state);
        description.getPos().y = 35;
        description.setTextSimple(this.permission.description);

        attach(buy);
        attach(permissionButton);
        attach(delete);
        attach(description);
        attach(descriptionButton);
        attach(loadAdmin);
        attach(rateButton);
        if (showAdminSettings) {
            attach(ownerButton);
        }
    }

    private void deleteEntry() {
        boolean admin = showAdminSettings && ((GameClientState) getState()).getPlayer().getNetworkObject().isAdminClient.get();
        if (!admin && !((GameClientState) getState()).getPlayer().getName().equals(permission.ownerUID)) {
            ((GameClientState) getState()).getController().popupAlertTextMessage(Lng.str("ERROR:\nCannot delete!\nYou do not own this!"), 0);
        } else {

            getPlayerCatalogControlManager().suspend(true);
            PlayerGameOkCancelInput confirm = new PlayerGameOkCancelInput("CatalogExtendedPanel_deleteEntry", (GameClientState) getState(), Lng.str("Confirm"), Lng.str("Do you really want to delete this entry?\n(a backup will be created on the server)")) {

                @Override
                public boolean isOccluded() {
                    return false;
                }

                @Override
                public void onDeactivate() {
                    getPlayerCatalogControlManager().suspend(false);
                }

                @Override
                public void pressedOK() {
                    if (showAdminSettings && getState().getPlayer().getNetworkObject().isAdminClient.get()) {
                        CatalogPermission p = new CatalogPermission(permission);
                        p.changeFlagForced = true;
                        getState().getCatalogManager().clientRequestCatalogRemove(p);
                    } else {
                        CatalogPermission p = new CatalogPermission(permission);
                        p.ownerUID = getState().getPlayer().getName();
                        getState().getCatalogManager().clientRequestCatalogRemove(p);
                    }

                    deactivate();
                }
            };
            confirm.activate();
        }
    }

    private void editDescription() {
        getPlayerCatalogControlManager().suspend(true);
        PlayerTextAreaInput t = new PlayerTextAreaInput("CatalogExtendedPanel_editDescription", (GameClientState) getState(), 140, 3, "Edit entry Description",
                                                        Lng.str("Enter a description for this entry."), new String(permission.description)) {
            @Override
            public void onDeactivate() {
                getPlayerCatalogControlManager().suspend(false);
            }			@Override
            public String[] getCommandPrefixes() {
                return null;
            }

            @Override
            public boolean onInput(String entry) {
                CatalogPermission p = new CatalogPermission(permission);
                //				changed.ownerUID = getState().getPlayerName();
                p.description = entry;

                if (showAdminSettings && getState().getPlayer().getNetworkObject().isAdminClient.get()) {
                    p.changeFlagForced = true;
                    getState().getCatalogManager().clientRequestCatalogEdit(p);
                } else {
                    p.ownerUID = getState().getPlayer().getName();
                    getState().getCatalogManager().clientRequestCatalogEdit(p);
                }

                return true;
            }			@Override
            public String handleAutoComplete(String s, TextCallback callback,
                                             String prefix) throws PrefixNotFoundException {
                return null;
            }

            @Override
            public boolean isOccluded() {
                return false;
            }



            @Override
            public void onFailedTextCheck(String msg) {
            }


        };
        t.activate();

    }

    private void editPermission() {
        System.err.println("EDIT PERMISSION");
        getPlayerCatalogControlManager().suspend(true);
        CatalogPermissionEditDialog d = new CatalogPermissionEditDialog(state, permission);
        d.activate();
    }

    @Override
    public float getHeight() {
        return 80;
    }

    @Override
    public float getWidth() {
        return 510;
    }

    public CatalogControlManager getPlayerCatalogControlManager() {
        return ((GameClientState) getState()).getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager()
                                             .getCatalogControlManager();
    }

    private void rate() {
        getPlayerCatalogControlManager().suspend(true);
        CatalogRateDialog d = new CatalogRateDialog(state, permission, showAdminSettings);
        d.activate();
    }

}