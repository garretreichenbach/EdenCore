package thederpgamer.edencore.data.guide;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/19/2022]
 */
public enum GuideEntryCategory {
  CONFIG_CHANGES("Config Changes"),
  BUILD_SECTORS("Build Sectors"),
  EXCHANGE("Exchange"),
  // EVENTS("Events"),
  RESOURCES("Resources"),
  FTL("FTL"),
  MISC("Misc");

  public String display;

  GuideEntryCategory(String display) {
    this.display = display;
  }
}
