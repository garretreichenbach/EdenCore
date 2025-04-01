package thederpgamer.edencore.gui.exchangemenu;

import api.common.GameClient;
import org.schema.game.client.controller.PlayerGameDropDownInput;
import org.schema.game.client.controller.PlayerInput;
import org.schema.game.client.controller.manager.ingame.catalog.CatalogControlManager;
import org.schema.game.client.data.GameClientState;
import org.schema.game.client.view.gui.GUIInputPanel;
import org.schema.game.client.view.gui.advanced.tools.BlockDisplayResult;
import org.schema.game.client.view.gui.advanced.tools.BlockSelectCallback;
import org.schema.game.client.view.gui.advanced.tools.GUIAdvBlockDisplay;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.meta.weapon.Weapon;
import org.schema.game.common.data.player.catalog.CatalogPermission;
import org.schema.game.server.data.blueprintnw.BlueprintType;
import org.schema.schine.common.OnInputChangedCallback;
import org.schema.schine.common.TextCallback;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.core.settings.PrefixNotFoundException;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.DataManager;
import thederpgamer.edencore.data.exchangedata.ExchangeData;
import thederpgamer.edencore.data.exchangedata.ExchangeDataManager;
import thederpgamer.edencore.gui.elements.GUIAdvMetaItemDisplay;
import thederpgamer.edencore.gui.elements.MetaItemDisplayResult;
import thederpgamer.edencore.gui.elements.MetaItemSelectCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class AddExchangeItemDialog extends PlayerInput {

	private final AddExchangeBlueprintPanel panel;
	protected static int mode;

	public AddExchangeItemDialog(GameClientState state, int mode) {
		super(state);
		AddExchangeItemDialog.mode = mode; // Mode can be used to determine the type of exchange data being added (ships, stations, etc.)
		(panel = new AddExchangeBlueprintPanel(getState(), this)).onInit();
	}

	@Override
	public void callback(GUIElement callingGuiElement, MouseEvent event) {
		if(event.pressedLeftMouse() && callingGuiElement.getUserPointer() instanceof String) {
			switch((String) callingGuiElement.getUserPointer()) {
				case "OK":
					ExchangeData data = panel.getExchangeData();
					if(data != null) {
						if(ExchangeDataManager.getInstance(false).existsName(data.getName())) {
							GameClient.showPopupMessage(Lng.str("An item by that name already exists!"), 0);
							deactivate();
							return;
						}
						data.setProducer(GameClient.getClientPlayerState().getFactionName());
						ExchangeDataManager.getInstance(false).addData(data, false);
						ExchangeDataManager.getInstance(false).sendPacket(data, DataManager.ADD_DATA, true);
						deactivate();
					}
					break;
				case "CANCEL":
				case "X":
					deactivate();
					break;
			}
		}
	}

	@Override
	public void onDeactivate() {

	}

	@Override
	public AddExchangeBlueprintPanel getInputPanel() {
		return panel;
	}

	@Override
	public void handleMouseEvent(MouseEvent mouseEvent) {

	}

	public static class AddExchangeBlueprintPanel extends GUIInputPanel {

		private ExchangeData exchangeData;
		private GUIActivatableTextBar nameInput;
		private GUIActivatableTextBar descriptionInput;
		private GUIActivatableTextBar priceInput;
		private GUIActivatableTextBar countInput;

		public AddExchangeBlueprintPanel(InputState state, GUICallback guiCallback) {
			super("Add_Exchange_Blueprint_Panel", state, 500, 500, guiCallback, Lng.str("Add Blueprint"), "");
		}

		@Override
		public void onInit() {
			super.onInit();
			GUIContentPane contentPane = ((GUIDialogWindow) background).getMainContentPane();
			exchangeData = new ExchangeData();
			if(mode == ExchangeDialog.SHIPS || mode == ExchangeDialog.STATIONS) {
				GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 1, 1, contentPane.getContent(0));
				buttonPane.onInit();
				buttonPane.addButton(0, 0, Lng.str("SELECT ENTRY"), GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
					@Override
					public void callback(GUIElement callingGuiElement, MouseEvent event) {
						if(event.pressedLeftMouse()) {
							List<CatalogPermission> catalogEntries = getCatalogEntries();
							GUIAncor[] entries = new GUIAncor[catalogEntries.size()];
							for(int i = 0; i < entries.length; i++) {
								entries[i] = new GUIAncor(getState(), 300, 24);
								entries[i].setUserPointer(catalogEntries.get(i));
								GUITextOverlay t = new GUITextOverlay(30, 10, FontLibrary.FontSize.SMALL, getState());
								t.setTextSimple(catalogEntries.get(i).getUid());
								t.setPos(4, 4, 0);
								t.setUserPointer(catalogEntries.get(i));
								entries[i].attach(t);
							}

							(new PlayerGameDropDownInput("Add_Blueprint_Dialog_Select_Entry", (GameClientState) getState(), Lng.str("Select Blueprint"), 24, Lng.str("Select Blueprint"), entries) {

								public CatalogControlManager getPlayerCatalogControlManager() {
									return getState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getCatalogControlManager();
								}

								@Override
								public void onDeactivate() {
									getPlayerCatalogControlManager().suspend(false);
								}

								@Override
								public boolean isOccluded() {
									return false;
								}

								@Override
								public void pressedOK(GUIListElement current) {
									if(current != null && current.getContent().getUserPointer() != null) {
										if(current.getContent().getUserPointer() instanceof CatalogPermission) {
											CatalogPermission permission = (CatalogPermission) current.getContent().getUserPointer();
											exchangeData.setFromCatalogEntry(permission);
											nameInput.setText(permission.getUid());
											descriptionInput.setText(permission.description);
											priceInput.setText(String.valueOf(permission.price));
											exchangeData.setClassification(permission.getClassification());
										}
									}
									deactivate();
								}
							}).activate();
						}
					}

					private List<CatalogPermission> getCatalogEntries() {
						List<CatalogPermission> catalogEntries = new ArrayList<>();
						if(mode == ExchangeDialog.SHIPS) {
							for(CatalogPermission permission : GameClient.getClientPlayerState().getCatalog().getPersonalCatalog()) {
								if(permission.type == BlueprintType.SHIP) {
									// Only allow ships to be added to the exchange data
									catalogEntries.add(permission);
								}
							}
						} else if(mode == ExchangeDialog.STATIONS) {
							for(CatalogPermission permission : GameClient.getClientPlayerState().getCatalog().getPersonalCatalog()) {
								if(permission.type == BlueprintType.SPACE_STATION) {
									// Only allow stations to be added to the exchange data
									catalogEntries.add(permission);
								}
							}
						}
						return catalogEntries;
					}

					@Override
					public boolean isOccluded() {
						return false;
					}
				}, new GUIActivationCallback() {
					@Override
					public boolean isVisible(InputState state) {
						return true;
					}

					@Override
					public boolean isActive(InputState state) {
						return true;
					}
				});
				contentPane.getContent(0).attach(buttonPane);

				nameInput = new GUIActivatableTextBar(getState(), FontLibrary.FontSize.SMALL, 32, 1, "Name", contentPane.getContent(0), new TextCallback() {
					@Override
					public String[] getCommandPrefixes() {
						return new String[0];
					}

					@Override
					public String handleAutoComplete(String s, TextCallback callback, String prefix) throws PrefixNotFoundException {
						return "";
					}

					@Override
					public void onFailedTextCheck(String msg) {

					}

					@Override
					public void onTextEnter(String entry, boolean send, boolean onAutoComplete) {

					}

					@Override
					public void newLine() {

					}
				}, contentPane.getTextboxes().get(0), new OnInputChangedCallback() {
					@Override
					public String onInputChanged(String s) {
						exchangeData.setName(s); // Set the name in the exchange data object
						return s; // Return the current string to be displayed in the text box
					}
				});

				nameInput.setPos(0, buttonPane.getPos().y + buttonPane.getHeight() + 4, 0);
				nameInput.setText(exchangeData.getName());
				contentPane.getContent(0).attach(nameInput);

				priceInput = new GUIActivatableTextBar(getState(), FontLibrary.FontSize.SMALL, 10, 1, "Price", contentPane.getContent(0), new TextCallback() {
					@Override
					public String[] getCommandPrefixes() {
						return new String[0];
					}

					@Override
					public String handleAutoComplete(String s, TextCallback callback, String prefix) throws PrefixNotFoundException {
						return "";
					}

					@Override
					public void onFailedTextCheck(String msg) {

					}

					@Override
					public void onTextEnter(String entry, boolean send, boolean onAutoComplete) {

					}

					@Override
					public void newLine() {

					}
				}, contentPane.getTextboxes().get(0), new OnInputChangedCallback() {
					@Override
					public String onInputChanged(String s) {
						try {
							int price = Integer.parseInt(s.trim());
							if(price < 0) price = 0; // Ensure price is non-negative
							exchangeData.setPrice(price); // Set the price in the exchange data object
						} catch(NumberFormatException e) {
							exchangeData.setPrice(0); // Default to 0 if parsing fails
						}
						return String.valueOf(exchangeData.getPrice());
					}
				});
				priceInput.setPos(0, nameInput.getPos().y + nameInput.getHeight() + 4, 0);
				contentPane.getContent(0).attach(priceInput);

				descriptionInput = new GUIActivatableTextBar(getState(), FontLibrary.FontSize.SMALL, 256, 3, "Description", contentPane.getContent(0), new TextCallback() {
					@Override
					public String[] getCommandPrefixes() {
						return new String[0];
					}

					@Override
					public String handleAutoComplete(String s, TextCallback callback, String prefix) throws PrefixNotFoundException {
						return "";
					}

					@Override
					public void onFailedTextCheck(String msg) {

					}

					@Override
					public void onTextEnter(String entry, boolean send, boolean onAutoComplete) {

					}

					@Override
					public void newLine() {

					}
				}, contentPane.getTextboxes().get(0), new OnInputChangedCallback() {
					@Override
					public String onInputChanged(String s) {
						exchangeData.setDescription(s); // Set the description in the exchange data object
						return s; // Return the current string to be displayed in the text box
					}
				});
				descriptionInput.setPos(0, priceInput.getPos().y + priceInput.getHeight() + 4, 0);
				descriptionInput.setText(exchangeData.getDescription());
				contentPane.getContent(0).attach(descriptionInput);
			}
			else {
				nameInput = new GUIActivatableTextBar(getState(), FontLibrary.FontSize.SMALL, 32, 1, "Name", contentPane.getContent(0), new TextCallback() {
					@Override
					public String[] getCommandPrefixes() {
						return new String[0];
					}

					@Override
					public String handleAutoComplete(String s, TextCallback callback, String prefix) throws PrefixNotFoundException {
						return "";
					}

					@Override
					public void onFailedTextCheck(String msg) {

					}

					@Override
					public void onTextEnter(String entry, boolean send, boolean onAutoComplete) {

					}

					@Override
					public void newLine() {

					}
				}, contentPane.getTextboxes().get(0), new OnInputChangedCallback() {
					@Override
					public String onInputChanged(String s) {
						exchangeData.setName(s); // Set the name in the exchange data object
						return s; // Return the current string to be displayed in the text box
					}
				});

				nameInput.setPos(0, 4, 0);
				nameInput.setText(exchangeData.getName());
				contentPane.getContent(0).attach(nameInput);
				
				priceInput = new GUIActivatableTextBar(getState(), FontLibrary.FontSize.SMALL, 2, 1, "Price", contentPane.getContent(0), new TextCallback() {
					@Override
					public String[] getCommandPrefixes() {
						return new String[0];
					}

					@Override
					public String handleAutoComplete(String s, TextCallback callback, String prefix) throws PrefixNotFoundException {
						return "";
					}

					@Override
					public void onFailedTextCheck(String msg) {

					}

					@Override
					public void onTextEnter(String entry, boolean send, boolean onAutoComplete) {

					}

					@Override
					public void newLine() {

					}
				}, contentPane.getTextboxes().get(0), new OnInputChangedCallback() {
					@Override
					public String onInputChanged(String s) {
						try {
							int price = Integer.parseInt(s.trim());
							if(price < 0) price = 0; // Ensure price is non-negative
							exchangeData.setPrice(price); // Set the price in the exchange data object
						} catch(NumberFormatException e) {
							exchangeData.setPrice(0); // Default to 0 if parsing fails
						}
						return String.valueOf(exchangeData.getPrice());
					}
				});
				priceInput.setPos(0, nameInput.getPos().y + nameInput.getHeight() + 4, 0);
				contentPane.getContent(0).attach(priceInput);
				
				countInput = new GUIActivatableTextBar(getState(), FontLibrary.FontSize.SMALL, 10, 1, "Count", contentPane.getContent(0), new TextCallback() {
					@Override
					public String[] getCommandPrefixes() {
						return new String[0];
					}

					@Override
					public String handleAutoComplete(String s, TextCallback callback, String prefix) throws PrefixNotFoundException {
						return "";
					}

					@Override
					public void onFailedTextCheck(String msg) {

					}

					@Override
					public void onTextEnter(String entry, boolean send, boolean onAutoComplete) {

					}

					@Override
					public void newLine() {

					}
				}, contentPane.getTextboxes().get(0), new OnInputChangedCallback() {
					@Override
					public String onInputChanged(String s) {
						try {
							int count = Integer.parseInt(s.trim());
							if(count < 1) count = 1; // Ensure count is at least 1
							exchangeData.setItemCount(count); // Set the item count in the exchange data object
						} catch(NumberFormatException e) {
							exchangeData.setItemCount(1); // Default to 1 if parsing fails
						}
						return String.valueOf(exchangeData.getItemCount());
					}
				});
				countInput.setPos(0, priceInput.getPos().y + priceInput.getHeight() + 4, 0);
				contentPane.getContent(0).attach(countInput);
				
				if(mode == ExchangeDialog.ITEMS) {
					GUIAdvBlockDisplay blockDisplay = new GUIAdvBlockDisplay(getState(), contentPane.getContent(0), new BlockDisplayResult() {
						@Override
						public short getCurrentValue() {
							return exchangeData.getItemId();
						}

						@Override
						public short getDefault() {
							return ElementKeyMap.CORE_ID;
						}

						@Override
						public BlockSelectCallback initCallback() {
							return new BlockSelectCallback() {
								@Override
								public void onTypeChanged(short i) {
									if(ElementKeyMap.isValidType(i)) exchangeData.setItemId(i);
								}
							};
						}

						@Override
						public String getToolTipText() {
							return Lng.str("Select Block");
						}
					});
					blockDisplay.setPos(0, countInput.getPos().y + countInput.getHeight() + 4, 0);
					contentPane.getContent(0).attach(blockDisplay);
				} else {
					GUIAdvMetaItemDisplay itemDisplay = new GUIAdvMetaItemDisplay(getState(), contentPane.getContent(0), new MetaItemDisplayResult() {
						@Override
						public short getCurrentValue() {
							return exchangeData.getItemId();
						}

						@Override
						public short getDefault() {
							return Weapon.WeaponSubType.LASER.type;
						}

						@Override
						public MetaItemSelectCallback initCallback() {
							return new MetaItemSelectCallback() {
								@Override
								public void onTypeChanged(short i) {
									exchangeData.setItemId(i);
								}
							};
						}

						@Override
						public String getToolTipText() {
							return Lng.str("Select Weapon");
						}
					});
					itemDisplay.setPos(0, countInput.getPos().y + countInput.getHeight() + 4, 0);
					contentPane.getContent(0).attach(itemDisplay);
				}
			}
		}

		private boolean isValid() {
			return !exchangeData.getCatalogName().isEmpty() && !exchangeData.getName().isEmpty() && !exchangeData.getDescription().isEmpty() && exchangeData.getPrice() > 0 && ((mode == ExchangeDialog.SHIPS || mode == ExchangeDialog.STATIONS) || ((mode == ExchangeDialog.ITEMS || mode == ExchangeDialog.WEAPONS) && exchangeData.getItemCount() > 0 && ElementKeyMap.isValidType(exchangeData.getItemId())));
		}

		public ExchangeData getExchangeData() {
			if(isValid()) return exchangeData;
			else return null;
		}
	}
}
