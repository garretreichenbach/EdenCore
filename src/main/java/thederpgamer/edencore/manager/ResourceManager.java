package thederpgamer.edencore.manager;

import api.utils.textures.StarLoaderTexture;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.schema.schine.graphicsengine.core.ResourceException;
import org.schema.schine.graphicsengine.forms.Mesh;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.resource.ResourceLoader;
import thederpgamer.edencore.EdenCore;

import javax.imageio.ImageIO;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

/**
 * Manages mod resources.
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/17/2021]
 */
public class ResourceManager {
	private static final String[] textureNames = { };
	private static final String[] spriteNames = {"default-sprite", "map-sprites"};
	private static final String[] modelNames = { };
	private static final String[] fontNames = {"NotoSans-Regular", "NotoColorEmoji-Regular",};
	private static final HashMap<String, StarLoaderTexture> textureMap = new HashMap<>();
	private static final HashMap<String, Sprite> spriteMap = new HashMap<>();
	private static final HashMap<String, Mesh> meshMap = new HashMap<>();
	private static final HashMap<String, UnicodeFont> fontMap = new HashMap<>();

	public static void loadResources(final ResourceLoader resourceLoader) {
		//Load Fonts
		for(String fontName : fontNames) {
			try {
				Font font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(EdenCore.class.getResourceAsStream("/fonts/" + fontName + ".ttf")));
				UnicodeFont unicodeFont = new UnicodeFont(font.deriveFont(12.0f));
				unicodeFont.getEffects().add(new ColorEffect(new Color(255, 255, 255)));
				unicodeFont.addGlyphs(0x4E00, 0x9FBF);
				unicodeFont.addAsciiGlyphs();
				unicodeFont.loadGlyphs();
				fontMap.put(fontName, unicodeFont);
			} catch(Exception exception) {
				LogManager.logException("Failed to load font \"" + fontName + "\"", exception);
			}
		}
		StarLoaderTexture.runOnGraphicsThread(new Runnable() {
			@Override
			public void run() {
				//Load Textures
				for(String textureName : textureNames) {
					try {
						if(textureName.endsWith("icon")) {
							textureMap.put(textureName, StarLoaderTexture.newIconTexture(ImageIO.read(Objects.requireNonNull(EdenCore.class.getResourceAsStream("/textures/" + textureName + ".png")))));
						} else {
							textureMap.put(textureName, StarLoaderTexture.newBlockTexture(ImageIO.read(Objects.requireNonNull(EdenCore.class.getResourceAsStream("/textures/" + textureName + ".png")))));
						}
					} catch(Exception exception) {
						LogManager.logException("Failed to load texture \"" + textureName + "\"", exception);
					}
				}
				//Load Sprites
				for(String spriteName : spriteNames) {
					try {
						Sprite sprite = StarLoaderTexture.newSprite(ImageIO.read(Objects.requireNonNull(EdenCore.class.getResourceAsStream("/sprites/" + spriteName + ".png"))), EdenCore.getInstance(), spriteName);
						sprite.setPositionCenter(false);
						sprite.setName(spriteName);
						spriteMap.put(spriteName, sprite);
					} catch(Exception exception) {
						LogManager.logException("Failed to load sprite \"" + spriteName + "\"", exception);
					}
				}
				//Load models
				for(String modelName : modelNames) {
					try {
						Vector3f offset = new Vector3f();
						if(modelName.contains("~")) {
							String meshName = modelName.substring(0, modelName.indexOf('~'));
							String offsetString = modelName.substring(modelName.indexOf('(') + 1, modelName.lastIndexOf(')'));
							String[] values = offsetString.split(", ");
							assert values.length == 3;
							offset.x = Float.parseFloat(values[0]);
							offset.y = Float.parseFloat(values[1]);
							offset.z = Float.parseFloat(values[2]);
							resourceLoader.getMeshLoader().loadModMesh(EdenCore.getInstance(), meshName, EdenCore.class.getResourceAsStream("/models/" + meshName + ".zip"), null);
							Mesh mesh = resourceLoader.getMeshLoader().getModMesh(EdenCore.getInstance(), meshName);
							mesh.getTransform().origin.add(offset);
							meshMap.put(meshName, mesh);
						} else {
							resourceLoader.getMeshLoader().loadModMesh(EdenCore.getInstance(), modelName, EdenCore.class.getResourceAsStream("/models/" + modelName + ".zip"), null);
							Mesh mesh = resourceLoader.getMeshLoader().getModMesh(EdenCore.getInstance(), modelName);
							mesh.setFirstDraw(true);
							meshMap.put(modelName, mesh);
						}
					} catch(ResourceException | IOException exception) {
						LogManager.logException("Failed to load model \"" + modelName + "\"", exception);
					}
				}
			}
		});
	}

	public static StarLoaderTexture getTexture(String name) {
		return textureMap.get(name);
	}

	public static Sprite getSprite(String name) {
		return spriteMap.get(name);
	}

	public static Sprite getSpriteExternal(String name) {
		//Todo: Get entity preview
		return null;
	}

	public static Mesh getMesh(String name) {
		if(meshMap.containsKey(name)) return (Mesh) meshMap.get(name).getChilds().get(0);
		else return null;
	}

	public static UnicodeFont getFont(String fontName) {
		return fontMap.get(fontName);
	}
}
