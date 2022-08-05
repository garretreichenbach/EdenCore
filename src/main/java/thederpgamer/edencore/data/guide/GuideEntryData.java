package thederpgamer.edencore.data.guide;

import org.apache.commons.io.IOUtils;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/19/2022]
 */
public abstract class GuideEntryData {

	public String name;
	public GuideEntryCategory category;

	public GuideEntryData(String name, GuideEntryCategory category) {
		this.name = name;
		this.category = category;
	}

	public abstract void createEntryPane(GUIContentPane contentPane);

	public static GuideEntryData loadFromFile(final File subFile, String name, GuideEntryCategory category) {
		return new GuideEntryData(name, category) {
			@Override
			public void createEntryPane(GUIContentPane contentPane) {
				//Read all text from file
				String text = "";
				try {
					text = String.valueOf(IOUtils.readLines(new FileReader(subFile)));
				} catch(IOException exception) {
					exception.printStackTrace();
				}
				if(!text.isEmpty()) {
					String mainText = text.split("\\|")[0];
					String subText = text.split("\\|")[1];

					GUITextOverlay mainTextOverlay = new GUITextOverlay(10, 10, contentPane.getState());
					mainTextOverlay.onInit();
					mainTextOverlay.setFont(FontLibrary.FontSize.MEDIUM.getFont());
					mainTextOverlay.setTextSimple(mainText);
					mainTextOverlay.updateTextSize();
					contentPane.setTextBoxHeightLast(mainTextOverlay.getTextHeight());
					contentPane.getContent(0).attach(mainTextOverlay);

					GUITextOverlay subTextOverlay = new GUITextOverlay(10, 10, contentPane.getState());
					subTextOverlay.onInit();
					subTextOverlay.setFont(FontLibrary.FontSize.SMALL.getFont());
					subTextOverlay.setTextSimple(subText);
					subTextOverlay.updateTextSize();
					contentPane.addNewTextBox(subTextOverlay.getTextHeight());
					contentPane.getContent(1).attach(subTextOverlay);

					//Todo: Handle blockBehaviorConfig.xml variables
					/*
					statOverlay.setTextSimple(
							"Cannon Base Damage: 13.53 -> "
									+ WeaponElementManager.BASE_DAMAGE
									+ "\n"
									+ "Increased base damage of cannons to encourage their usage.\n"
									+ "Cannon Cannon Damage Nerf: 6.53 -> 4.45\n"
									+ "Made cannon cannon viable in combat.\n"
									+ "Cannon Cannon Projectile Width Nerf: 0.0 -> 1.15\n"
									+ "Balances previous buffs for Cannon Cannon\n"
									+ "Cannon Beam Damage Multiplier: 6.39 -> 8.0\n"
									+ "Cannon Beam was one of the worst weapons in the game, so increasing it's"
									+ " damage output will make it more viable.\n"
									+ "Cannon Beam Reload Multiplier: 9.0 -> 6.7\n"
									+ "Nerf reload to make Cannon Beam more effective.\n"
									+ "Cannon Missile Damage Multiplier: 8.51 -> 8.65\n"
									+ "Slightly buff Cannon Missile damage.\n"
									+ "Cannon Penetration Depth Exponent: 0.35 -> "
									+ WeaponElementManager.PROJECTILE_PENETRATION_DEPTH_EXP
									+ "\n"
									+ "Cannon Penetration Depth Exponent Multiplier: 0.4 -> "
									+ WeaponElementManager.PROJECTILE_PENETRATION_DEPTH_EXP_MULT
									+ "\n"
									+ "Decreased cannon penetration slightly in order to boost armor"
									+ " protection.");
					 */
				}
			}
		};
	}
}
