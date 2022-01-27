package thederpgamer.edencore.manager;

import api.utils.textures.StarLoaderTexture;
import java.io.IOException;
import java.util.HashMap;
import javax.vecmath.Vector3f;
import org.schema.schine.graphicsengine.core.ResourceException;
import org.schema.schine.graphicsengine.forms.Mesh;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.resource.ResourceLoader;
import thederpgamer.edencore.EdenCore;

/**
 * Manages mod resources.
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/17/2021]
 */
public class ResourceManager {

  private static final String[] textureNames = {};

  private static final String[] spriteNames = {"default-sprite"};

  private static final String[] modelNames = {};

  private static final HashMap<String, StarLoaderTexture> textureMap = new HashMap<>();
  private static final HashMap<String, Sprite> spriteMap = new HashMap<>();
  private static final HashMap<String, Mesh> meshMap = new HashMap<>();

  public static void loadResources(final ResourceLoader resourceLoader) {

    StarLoaderTexture.runOnGraphicsThread(
        new Runnable() {
          @Override
          public void run() {
            // Load Textures
            for (String textureName : textureNames) {
              try {
                if (textureName.endsWith("icon")) {
                  textureMap.put(
                      textureName,
                      StarLoaderTexture.newIconTexture(
                          EdenCore.getInstance()
                              .getJarBufferedImage(
                                  "thederpgamer/edencore/resources/textures/"
                                      + textureName
                                      + ".png")));
                } else {
                  textureMap.put(
                      textureName,
                      StarLoaderTexture.newBlockTexture(
                          EdenCore.getInstance()
                              .getJarBufferedImage(
                                  "thederpgamer/edencore/resources/textures/"
                                      + textureName
                                      + ".png")));
                }
              } catch (Exception exception) {
                LogManager.logException(
                    "Failed to load texture \"" + textureName + "\"", exception);
              }
            }

            // Load Sprites
            for (String spriteName : spriteNames) {
              try {
                Sprite sprite =
                    StarLoaderTexture.newSprite(
                        EdenCore.getInstance()
                            .getJarBufferedImage(
                                "thederpgamer/edencore/resources/sprites/" + spriteName + ".png"),
                        EdenCore.getInstance(),
                        spriteName);
                sprite.setPositionCenter(false);
                sprite.setName(spriteName);
                spriteMap.put(spriteName, sprite);
              } catch (Exception exception) {
                LogManager.logException("Failed to load sprite \"" + spriteName + "\"", exception);
              }
            }

            // Load models
            for (String modelName : modelNames) {
              try {
                Vector3f offset = new Vector3f();
                if (modelName.contains("~")) {
                  String meshName = modelName.substring(0, modelName.indexOf('~'));
                  String offsetString =
                      modelName.substring(modelName.indexOf('(') + 1, modelName.lastIndexOf(')'));
                  String[] values = offsetString.split(", ");
                  assert values.length == 3;
                  offset.x = Float.parseFloat(values[0]);
                  offset.y = Float.parseFloat(values[1]);
                  offset.z = Float.parseFloat(values[2]);
                  resourceLoader
                      .getMeshLoader()
                      .loadModMesh(
                          EdenCore.getInstance(),
                          meshName,
                          EdenCore.getInstance()
                              .getJarResource(
                                  "thederpgamer/edencore/resources/models/" + meshName + ".zip"),
                          null);
                  Mesh mesh =
                      resourceLoader.getMeshLoader().getModMesh(EdenCore.getInstance(), meshName);
                  mesh.getTransform().origin.add(offset);
                  meshMap.put(meshName, mesh);
                } else {
                  resourceLoader
                      .getMeshLoader()
                      .loadModMesh(
                          EdenCore.getInstance(),
                          modelName,
                          EdenCore.getInstance()
                              .getJarResource(
                                  "thederpgamer/edencore/resources/models/" + modelName + ".zip"),
                          null);
                  Mesh mesh =
                      resourceLoader.getMeshLoader().getModMesh(EdenCore.getInstance(), modelName);
                  mesh.setFirstDraw(true);
                  meshMap.put(modelName, mesh);
                }
              } catch (ResourceException | IOException exception) {
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
    // Todo: Get entity preview
    return null;
  }

  public static Mesh getMesh(String name) {
    if (meshMap.containsKey(name)) return (Mesh) meshMap.get(name).getChilds().get(0);
    else return null;
  }
}
