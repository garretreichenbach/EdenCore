package thederpgamer.edencore.navigation;

import api.mod.StarMod;
import api.utils.textures.StarLoaderTexture;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.edencore.EdenCore;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;

/**
 * offers sprites to draw onto the map
 */
public enum MapIcon {
    ERROR             ("edencore_sprites",0),
    WARPGATE          ("edencore_sprites",1),
    PRIVATE           ("edencore_sprites",2),
    MC_DONALDS("edencore_sprites",3),
    GATE_1  ("edencore_sprites",4),
    GATE_2  ("edencore_sprites",5),
    GATE_3  ("edencore_sprites",6),
    GATE_4  ("edencore_sprites",7),
    GATE_5  ("edencore_sprites",8),
    GATE_LIGHTS            ("edencore_sprites",9),
    GATE_WORMHOLE  ("edencore_sprites",10);

    int index;
    String resourceName;
    int subSpriteIndex;

    MapIcon(String resourceName, int subSpriteIndex) {
        this.resourceName = resourceName;
        this.subSpriteIndex = subSpriteIndex;
    }

    static HashMap<String,Sprite> sprites =new HashMap<>();
    //map indices and resource names to be loaded as sprites
    static {
        for (int i = 0; i < MapIcon.values().length; i++) {
            MapIcon.values()[i].index = i;
            sprites.put(MapIcon.values()[i].resourceName,null);
        }
    }

    public static void loadSprites() {
        for (String resourceName: sprites.keySet()) {
            final InputStream inputStream;
            StarMod mod = EdenCore.getInstance();
            try {
                inputStream = mod.getJarResource("thederpgamer/edencore/resources/sprites/"+resourceName+".png");
                BufferedImage img;
                img = ImageIO.read(inputStream);
                Sprite s = StarLoaderTexture.newSprite(img,mod,resourceName);
                s.setMultiSpriteMax(8,8); //TODO map this somewhere, file dependent?/make standard?
                s.setHeight(32);
                s.setWidth(32);
                sprites.put(resourceName,s);
            } catch (Exception e) { //resource doesnt exist
                e.printStackTrace();
                continue;
            }
        }
    }

    //getters and stuff

    public static MapIcon getByIndex(int index) {
        return MapIcon.values()[Math.abs(index)%MapIcon.values().length];
    }

    public int getSubSpriteIndex() {
        return subSpriteIndex;
    }

    public Sprite getSprite() {
        return sprites.get(resourceName);
    }
}
