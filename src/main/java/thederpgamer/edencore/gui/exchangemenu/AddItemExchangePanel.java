package thederpgamer.edencore.gui.exchangemenu;

import api.common.GameClient;
import api.utils.gui.GUIInputDialogPanel;
import org.apache.commons.lang3.text.WordUtils;
import org.schema.game.client.data.GameClientState;
import org.schema.game.client.view.gui.GUIBlockSprite;
import org.schema.game.client.view.gui.advanced.tools.*;
import org.schema.game.client.view.gui.inventory.InventorySlotOverlayElement;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.meta.MetaObjectManager;
import org.schema.game.common.data.element.meta.weapon.Weapon;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.GUIAncor;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDialogWindow;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.element.ElementManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/24/2021]
 */
public class AddItemExchangePanel extends GUIInputDialogPanel {
	public short barId;
	public String currentBarText = "";
	public short itemId;
	public Weapon.WeaponSubType subType;
	private GUIContentPane contentPane;
	private boolean itemTextChanged;

	public AddItemExchangePanel(InputState inputState, GUICallback guiCallback) {
		super(inputState, "item_exchange_add_panel", "Add Exchange Entry", "", 650, 350, guiCallback);
	}

	@Override
	public void onInit() {
		super.onInit();
		contentPane = ((GUIDialogWindow) background).getMainContentPane();
		contentPane.setTextBoxHeightLast((int) getHeight());
		addTextBar(new TextBarResult() {
			@Override
			public TextBarCallback initCallback() {
				return super.callback;
			}

			@Override
			public String getName() {
				return "Price";
			}

			@Override
			public String getToolTipText() {
				return "Enter price";
			}

			@Override
			public String onTextChanged(String text) {
				String t = text.trim();
				if(!t.equals(currentBarText)) currentBarText = t;
				return text;
			}
		}, 30);
		addDropdown(new DropdownResult() {
			private List<GUIElement> bars;

			@Override
			public DropdownCallback initCallback() {
				return new DropdownCallback() {
					@Override
					public void onChanged(Object value) {
						if(value instanceof ElementInformation) barId = ((ElementInformation) value).getId();
					}
				};
			}

			@Override
			public String getName() {
				return "Bar type";
			}

			@Override
			public String getToolTipText() {
				return "Select bar type";
			}

			@Override
			public Object getDefault() {
				if(barId != 0 && bars.size() > 0) return bars.get(0);
				return null;
			}

			@Override
			public Collection<? extends GUIElement> getDropdownElements(GUIElement guiElement) {
				bars = getBars();
				return bars;
			}

			@Override
			public boolean needsListUpdate() {
				return false;
			}

			@Override
			public void flagListNeedsUpdate(boolean flag) {
			}

			@Override
			public int getDropdownHeight() {
				return 26;
			}
		}, 60);
		addDropdown(new DropdownResult() {
			private List<GUIElement> items;

			@Override
			public DropdownCallback initCallback() {
				return new DropdownCallback() {
					@Override
					public void onChanged(Object value) {
						if(value instanceof Weapon.WeaponSubType) {
							itemId = MetaObjectManager.MetaObjectType.WEAPON.type;
							subType = (Weapon.WeaponSubType) value;
						}
					}
				};
			}

			@Override
			public String getName() {
				return "Item";
			}

			@Override
			public String getToolTipText() {
				return "Select item";
			}

			@Override
			public Object getDefault() {
				if(itemId != 0 && items.size() > 0) return items.get(0);
				return null;
			}

			@Override
			public Collection<? extends GUIElement> getDropdownElements(GUIElement guiElement) {
				items = getItems();
				return items;
			}

			@Override
			public boolean needsListUpdate() {
				return itemTextChanged;
			}

			@Override
			public void flagListNeedsUpdate(boolean flag) {
				itemTextChanged = flag;
			}

			@Override
			public int getDropdownHeight() {
				return 26;
			}
		}, 90);
	}

	private void addTextBar(TextBarResult textBarResult, int y) {
		GUIAdvTextBar textBar = new GUIAdvTextBar(getState(), contentPane, textBarResult);
		textBar.setPos(0, y, 0);
		contentPane.getContent(0).attach(textBar);
	}

	private void addDropdown(DropdownResult result, int y) {
		GUIAdvDropdown dropDown = new GUIAdvDropdown(getState(), contentPane, result);
		dropDown.setPos(0, y, 0);
		contentPane.getContent(0).attach(dropDown);
	}

	private ArrayList<GUIElement> getBars() {
		ArrayList<GUIElement> barList = new ArrayList<>();
		short[] bars = {ElementManager.getItem("Bronze Bar").getId(), ElementManager.getItem("Silver Bar").getId(), ElementManager.getItem("Gold Bar").getId()};
		for(short id : bars) {
			ElementInformation info = ElementKeyMap.getInfo(id);
			GUIAncor anchor = new GUIAncor(GameClient.getClientState(), 200.0f, 26.0f);
			GUITextOverlay textOverlay = new GUITextOverlay(100, 26, FontLibrary.getBoldArial12White(), GameClient.getClientState());
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

	private ArrayList<GUIElement> getItems() {
		ArrayList<GUIElement> elementList = new ArrayList<>();
		GameClientState gameClientState = GameClient.getClientState();
		for(Weapon.WeaponSubType subType : Weapon.WeaponSubType.values()) {
			GUIAncor anchor = new GUIAncor(gameClientState, 300.0F, 26.0F);
			elementList.add(anchor);
			GUITextOverlay textOverlay = new GUITextOverlay(100, 26, FontLibrary.getBoldArial12White(), gameClientState);
			textOverlay.setTextSimple(WordUtils.capitalize(subType.name().toLowerCase().replace("_", " ")) + " Weapon");
			anchor.setUserPointer(subType);
			InventorySlotOverlayElement blockSprite = new InventorySlotOverlayElement(false, getState(), false, anchor);
			blockSprite.setMeta(MetaObjectManager.MetaObjectType.WEAPON.type);
			blockSprite.setSubSlotType(subType.type);
			blockSprite.getScale().set(0.4F, 0.4F, 0.0F);
			blockSprite.setLayer(-1);
			blockSprite.setSlot(0);
			anchor.attach(blockSprite);
			textOverlay.getPos().x = 50.0F;
			textOverlay.getPos().y = 7.0F;
			anchor.attach(textOverlay);
		}
		return elementList;
	}
}
