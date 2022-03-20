package thederpgamer.edencore.gui.guidemenu;

import api.common.GameClient;
import api.utils.gui.GUIControlManager;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import thederpgamer.edencore.data.guide.GuideEntryCategory;
import thederpgamer.edencore.data.guide.GuideEntryData;

import java.util.ArrayList;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/19/2022]
 */
public class GuideMenuControlManager extends GUIControlManager {

	public static final ArrayList<GuideEntryData> guideEntries = new ArrayList<>();

	public GuideMenuControlManager() {
		super(GameClient.getClientState());
	}

	@Override
	public GuideMenuPanel createMenuPanel() {
		loadGuides();
		return new GuideMenuPanel(getState());
	}

	private void loadGuides() {
		{ //Config Changes
			guideEntries.add(new GuideEntryData("Armor", GuideEntryCategory.CONFIG_CHANGES) {
				@Override
				public void createEntryPane(GUIContentPane contentPane) {
					GUITextOverlay titleOverlay = new GUITextOverlay(50, 50, getState());
					titleOverlay.setFont(FontLibrary.FontSize.BIG.getFont());
					titleOverlay.setTextSimple("Armor:");
					//Todo
					contentPane.getContent(0).attach(titleOverlay);
				}
			});
		}

		{ //Build Sectors
			guideEntries.add(new GuideEntryData("Basic Usage", GuideEntryCategory.BUILD_SECTORS) {
				@Override
				public void createEntryPane(GUIContentPane contentPane) {
					GUITextOverlay titleOverlay = new GUITextOverlay(50, 50, getState());
					titleOverlay.setFont(FontLibrary.FontSize.BIG.getFont());
					titleOverlay.setTextSimple("Basic Usage:");
					//Todo
					contentPane.getContent(0).attach(titleOverlay);
				}
			});

			guideEntries.add(new GuideEntryData("Management", GuideEntryCategory.BUILD_SECTORS) {
				@Override
				public void createEntryPane(GUIContentPane contentPane) {
					GUITextOverlay titleOverlay = new GUITextOverlay(50, 50, getState());
					titleOverlay.setFont(FontLibrary.FontSize.BIG.getFont());
					titleOverlay.setTextSimple("Management:");
					//Todo
					contentPane.getContent(0).attach(titleOverlay);
				}
			});
		}

		{ //Exchange
			guideEntries.add(new GuideEntryData("Prize Bars", GuideEntryCategory.EXCHANGE) {
				@Override
				public void createEntryPane(GUIContentPane contentPane) {
					GUITextOverlay titleOverlay = new GUITextOverlay(50, 50, getState());
					titleOverlay.setFont(FontLibrary.FontSize.BIG.getFont());
					titleOverlay.setTextSimple("Prize Bars:");
					//Todo
					contentPane.getContent(0).attach(titleOverlay);
				}
			});
		}

		{ //Resources
			guideEntries.add(new GuideEntryData("Resource Types", GuideEntryCategory.RESOURCES) {
				@Override
				public void createEntryPane(GUIContentPane contentPane) {
					GUITextOverlay titleOverlay = new GUITextOverlay(50, 50, getState());
					titleOverlay.setFont(FontLibrary.FontSize.BIG.getFont());
					titleOverlay.setTextSimple("Resource Types:");
					//Todo
					contentPane.getContent(0).attach(titleOverlay);
				}
			});
		}

		{ //FTL
			guideEntries.add(new GuideEntryData("Warpspace Mechanics", GuideEntryCategory.FTL) {
				@Override
				public void createEntryPane(GUIContentPane contentPane) {
					GUITextOverlay titleOverlay = new GUITextOverlay(50, 50, getState());
					titleOverlay.setFont(FontLibrary.FontSize.BIG.getFont());
					titleOverlay.setTextSimple("Warpspace Mechanics:");
					//Todo
					contentPane.getContent(0).attach(titleOverlay);
				}
			});
		}

		{ //Misc
			guideEntries.add(new GuideEntryData("Banking System", GuideEntryCategory.MISC) {
				@Override
				public void createEntryPane(GUIContentPane contentPane) {
					GUITextOverlay titleOverlay = new GUITextOverlay(50, 50, getState());
					titleOverlay.setFont(FontLibrary.FontSize.BIG.getFont());
					titleOverlay.setTextSimple("Banking System:");
					//Todo
					contentPane.getContent(0).attach(titleOverlay);
				}
			});

			guideEntries.add(new GuideEntryData("Decor", GuideEntryCategory.MISC) {
				@Override
				public void createEntryPane(GUIContentPane contentPane) {
					GUITextOverlay titleOverlay = new GUITextOverlay(50, 50, getState());
					titleOverlay.setFont(FontLibrary.FontSize.BIG.getFont());
					titleOverlay.setTextSimple("Decor:");
					//Todo
					contentPane.getContent(0).attach(titleOverlay);
				}
			});
		}
	}
}
