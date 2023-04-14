package thederpgamer.edencore.data.guide;

import org.apache.commons.io.IOUtils;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.GUIOverlay;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import thederpgamer.edencore.utils.ImageUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	public static GuideEntryData loadFromFile(final File subFile, String name, GuideEntryCategory category) {
		return new GuideEntryData(name, category) {
			@Override
			public void createEntryPane(GUIContentPane contentPane) {
				//Read all text from file
				ArrayList<String> text = new ArrayList<>();
				try {
					text = (ArrayList<String>) IOUtils.readLines(new FileReader(subFile));
				} catch(IOException exception) {
					exception.printStackTrace();
				}
				if(!text.isEmpty()) {
					GUITextOverlay overlay = new GUITextOverlay(10, 10, contentPane.getState());
					overlay.autoWrapOn = contentPane.getContent(0);
					overlay.setFont(FontLibrary.FontSize.MEDIUM.getFont());
					overlay.onInit();
					StringBuilder builder = new StringBuilder();
					Pattern pattern = Pattern.compile("(?i)(?><img\\s+)src=\"(.*?)\"|width=\"(.*?)\"|height=\"(.*?)\">");
					//This jumbled regex mess is apparently able to find all images in the text as well as any other args that are used for the image
					int lineNumber = 0;
					for(String line : text) {
						Matcher matcher = pattern.matcher(line);
						boolean found = false;
						while(matcher.find()) {
							found = true;
							int breakHeight = 0;
							if(!matcher.group(1).isEmpty()) {
								try {
									String imagePath = matcher.group(1);
									int width = Integer.parseInt(matcher.group(2));
									int height = Integer.parseInt(matcher.group(3));
									//Add the image to the content pane
									Sprite sprite = ImageUtils.getImage(imagePath);
									if(sprite != null) {
										sprite.setWidth(width);
										sprite.setHeight(height);
										GUIOverlay imageOverlay = new GUIOverlay(sprite, contentPane.getState());
										imageOverlay.onInit();
										contentPane.getContent(0).attach(imageOverlay);
										imageOverlay.setPos(imageOverlay.getPos().x, (17 * lineNumber) + 2, 0); //y = (height per line * line number) + 2
										breakHeight = height / 17; //breakHeight = imageHeight / height per line
									} else {
										//Todo: Check if still downloading, if so delay opening of gui until it is finished. If image is invalid, display error message in log and skip it.
									}
								} catch(Exception exception) {
									exception.printStackTrace();
								}
								for(int i = 0; i < breakHeight; i++) builder.append("\n");
								lineNumber += breakHeight;
							}
						}
						if(!found) builder.append(line);
						lineNumber++;
					}
					//Todo: Handle blockBehaviorConfig.xml variables
					overlay.setTextSimple(builder.toString());
					overlay.updateTextSize();
					contentPane.setTextBoxHeightLast(overlay.getTextHeight());
					contentPane.getContent(0).attach(overlay);
				}
			}
		};
	}

	public abstract void createEntryPane(GUIContentPane contentPane);
}
