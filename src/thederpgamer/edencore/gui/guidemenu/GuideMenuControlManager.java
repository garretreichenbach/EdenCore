package thederpgamer.edencore.gui.guidemenu;

import api.common.GameClient;
import api.utils.gui.GUIControlManager;
import java.util.ArrayList;
import org.schema.common.util.StringTools;
import org.schema.game.common.controller.elements.VoidElementManager;
import org.schema.game.common.controller.elements.weapon.WeaponElementManager;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import thederpgamer.edencore.data.guide.GuideEntryCategory;
import thederpgamer.edencore.data.guide.GuideEntryData;

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
    { // Config Changes
      guideEntries.add(
          new GuideEntryData("Armor Changes", GuideEntryCategory.CONFIG_CHANGES) {
            @Override
            public void createEntryPane(GUIContentPane contentPane) {
              GUITextOverlay statOverlay = new GUITextOverlay(10, 10, getState());
              statOverlay.onInit();
              statOverlay.setFont(FontLibrary.FontSize.MEDIUM.getFont());
              statOverlay.setTextSimple(
                  "Armor Thickness Bonus: 0.0 -> "
                      + StringTools.formatPointZero(VoidElementManager.ARMOR_THICKNESS_BONUS)
                      + "\n"
                      + "A flat armor thickness bonus multiplier added on top of the base"
                      + " calculations.\n"
                      + "Armor Beam Damage Resistance: 0.0 -> "
                      + StringTools.formatPointZero(VoidElementManager.ARMOR_BEAM_DAMAGE_SCALING)
                      + "\n"
                      + "An additional bonus multiplier for armor resistance against beams"
                      + " specifically.");
              statOverlay.updateTextSize();
              contentPane.setTextBoxHeightLast(statOverlay.getTextHeight());
              contentPane.getContent(0).attach(statOverlay);

              GUITextOverlay descriptionOverlay = new GUITextOverlay(10, 10, getState());
              descriptionOverlay.onInit();
              descriptionOverlay.setFont(FontLibrary.FontSize.SMALL.getFont());
              descriptionOverlay.setTextSimple(
                  "These changes buff armor and make it more worthwhile to create armored bulkheads"
                      + " in your ships. Without these changes, armor is practically useless."
                      + " Additionally, buffing armor defense against beam damage specifically"
                      + " encourages players to use cannons for penetrating armor instead of"
                      + " relying mostly on beams for block damage.");
              descriptionOverlay.updateTextSize();
              contentPane.addNewTextBox(descriptionOverlay.getTextHeight());
              contentPane.getContent(1).attach(descriptionOverlay);
            }
          });

      guideEntries.add(
          new GuideEntryData("Reactor Changes", GuideEntryCategory.CONFIG_CHANGES) {
            @Override
            public void createEntryPane(GUIContentPane contentPane) {
              GUITextOverlay statOverlay = new GUITextOverlay(10, 10, getState());
              statOverlay.onInit();
              statOverlay.setFont(FontLibrary.FontSize.MEDIUM.getFont());
              statOverlay.setTextSimple(
                  "Reactor Recharge Percent Per Second: 1.0 -> + "
                      + StringTools.formatPointZero(
                          VoidElementManager.REACTOR_RECHARGE_PERCENT_PER_SECOND)
                      + "\n"
                      + "Increased reactor regen to make reactor design less of a pain and allow"
                      + " for more flexibility in system design.\n"
                      + "Reactor Recharge Multiplier When Empty: 1.0 -> "
                      + StringTools.formatPointZero(
                          VoidElementManager.REACTOR_RECHARGE_EMPTY_MULTIPLIER)
                      + "\n"
                      + "Increased how much initial bonus recharge a reactor gets after a power"
                      + " outage.\n"
                      + "Reactor Chamber Block Ratio: 0.5 -> "
                      + StringTools.formatPointZero(
                          VoidElementManager.REACTOR_CHAMBER_BLOCKS_PER_MAIN_REACTOR_AND_LEVEL)
                      + "\n"
                      + "Decreased the ratio of chamber blocks needed per reactor block as chambers"
                      + " took up far too much space and forced players to dedicate too much"
                      + " internalsystem space just for chambers.\n"
                      + "Reactor Blocks Needed Per Level: 100 -> "
                      + VoidElementManager.REACTOR_LEVEL_CALC_LINEAR_BLOCKS_NEEDED_PER_LEVEL
                      + "\n"
                      + "Increased this in order to balance above changes.\n"
                      + "Reactor HP Deduction Factor: 0.7 -> "
                      + StringTools.formatPointZero(VoidElementManager.HP_DEDUCTION_LOG_FACTOR)
                      + "\n"
                      + "Most battles get stretched out in the end as even if a ship is mostly"
                      + " destroyed, if the reactor is still alive the ship doesn't overheat, which"
                      + " creates a sort of \"limbo\" period where the ship is unable to function"
                      + " but also is not destroyed. By increasing how much damage Reactor HP takes"
                      + " we can mitigate this problem.");
              statOverlay.updateTextSize();
              contentPane.setTextBoxHeightLast(statOverlay.getTextHeight());
              contentPane.getContent(0).attach(statOverlay);

              GUITextOverlay descriptionOverlay = new GUITextOverlay(10, 10, getState());
              descriptionOverlay.onInit();
              descriptionOverlay.setFont(FontLibrary.FontSize.SMALL.getFont());
              descriptionOverlay.setTextSimple(
                  "These changes seek to balance reactors and to make the design process smoother"
                      + " and less of a headache.");
              descriptionOverlay.updateTextSize();
              contentPane.addNewTextBox(descriptionOverlay.getTextHeight());
              contentPane.getContent(1).attach(descriptionOverlay);
            }
          });

      guideEntries.add(
          new GuideEntryData("Cannon Changes", GuideEntryCategory.CONFIG_CHANGES) {
            @Override
            public void createEntryPane(GUIContentPane contentPane) {
              GUITextOverlay statOverlay = new GUITextOverlay(10, 10, getState());
              statOverlay.onInit();
              statOverlay.setFont(FontLibrary.FontSize.MEDIUM.getFont());
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
              // Todo: Figure out how to fetch these directly from blockBehaviorConfig.xml so I
              // don't have to update them every time the config is changed
              statOverlay.updateTextSize();
              contentPane.setTextBoxHeightLast(statOverlay.getTextHeight());
              contentPane.getContent(0).attach(statOverlay);

              GUITextOverlay descriptionOverlay = new GUITextOverlay(10, 10, getState());
              descriptionOverlay.onInit();
              descriptionOverlay.setFont(FontLibrary.FontSize.SMALL.getFont());
              descriptionOverlay.setTextSimple("Some buffs in order to make cannons viable again.");
              descriptionOverlay.updateTextSize();
              contentPane.addNewTextBox(descriptionOverlay.getTextHeight());
              contentPane.getContent(1).attach(descriptionOverlay);
            }
          });

      guideEntries.add(
          new GuideEntryData("Misc Changes", GuideEntryCategory.CONFIG_CHANGES) {
            @Override
            public void createEntryPane(GUIContentPane contentPane) {
              GUITextOverlay statOverlay = new GUITextOverlay(10, 10, getState());
              statOverlay.onInit();
              statOverlay.setFont(FontLibrary.FontSize.MEDIUM.getFont());
              statOverlay.setTextSimple(
                  "Basic Hull Armor Value: 0.0 -> 1.0\n"
                      + "Minor fix to prevent an exploit involving cannons being able to infinitely"
                      + " penetrate basic armor.\n"
                      + "Weapon Module Mass: 1.0 -> 0.7\n"
                      + "Made weapons less heavy in order to allow more flexibility in systems"
                      + " design.\n"
                      + "Acid Damage Max Propagation: 200 -> "
                      + WeaponElementManager.ACID_DAMAGE_MAX_PROPAGATION
                      + "\n"
                      + "Reduced acid damage a bit to balance weapon buffs.");
              statOverlay.updateTextSize();
              contentPane.setTextBoxHeightLast(statOverlay.getTextHeight());
              contentPane.getContent(0).attach(statOverlay);

              GUITextOverlay descriptionOverlay = new GUITextOverlay(10, 10, getState());
              descriptionOverlay.onInit();
              descriptionOverlay.setFont(FontLibrary.FontSize.SMALL.getFont());
              descriptionOverlay.setTextSimple(
                  "Misc stat changes to fix exploits and improve gameplay.");
              descriptionOverlay.updateTextSize();
              contentPane.addNewTextBox(descriptionOverlay.getTextHeight());
              contentPane.getContent(1).attach(descriptionOverlay);
            }
          });
    }

    { // Build Sectors
      guideEntries.add(
          new GuideEntryData("Basic Usage", GuideEntryCategory.BUILD_SECTORS) {
            @Override
            public void createEntryPane(GUIContentPane contentPane) {}
          });

      guideEntries.add(
          new GuideEntryData("Management", GuideEntryCategory.BUILD_SECTORS) {
            @Override
            public void createEntryPane(GUIContentPane contentPane) {}
          });
    }

    { // Exchange
      guideEntries.add(
          new GuideEntryData("Prize Bars", GuideEntryCategory.EXCHANGE) {
            @Override
            public void createEntryPane(GUIContentPane contentPane) {}
          });
    }

    { // Resources
      guideEntries.add(
          new GuideEntryData("Resource Types", GuideEntryCategory.RESOURCES) {
            @Override
            public void createEntryPane(GUIContentPane contentPane) {}
          });
    }

    { // FTL
      guideEntries.add(
          new GuideEntryData("Warpspace Mechanics", GuideEntryCategory.FTL) {
            @Override
            public void createEntryPane(GUIContentPane contentPane) {}
          });
    }

    { // Misc
      guideEntries.add(
          new GuideEntryData("Banking System", GuideEntryCategory.MISC) {
            @Override
            public void createEntryPane(GUIContentPane contentPane) {}
          });

      guideEntries.add(
          new GuideEntryData("Decor", GuideEntryCategory.MISC) {
            @Override
            public void createEntryPane(GUIContentPane contentPane) {}
          });
    }
  }
}
