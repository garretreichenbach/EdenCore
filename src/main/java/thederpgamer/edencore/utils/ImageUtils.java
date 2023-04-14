package thederpgamer.edencore.utils;

import api.common.GameClient;
import api.utils.textures.StarLoaderTexture;
import org.lwjgl.opengl.GL11;
import org.schema.game.common.controller.SegmentController;
import org.schema.schine.graphicsengine.camera.Camera;
import org.schema.schine.graphicsengine.camera.viewer.FixedViewer;
import org.schema.schine.graphicsengine.core.FrameBufferObjects;
import org.schema.schine.graphicsengine.core.GLFrame;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.network.StateInterface;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.manager.ResourceManager;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/17/2021]
 */
public class ImageUtils {
	public static final int SPRITE_WIDTH = 100;
	public static final int SPRITE_HEIGHT = 100;
	private final static ConcurrentHashMap<String, Sprite> imgCache = new ConcurrentHashMap<>();
	private final static ConcurrentLinkedQueue<String> downloadingImages = new ConcurrentLinkedQueue<>();

	@Nullable
	public static Sprite getImage(String url) {
		Sprite bufferedImage = imgCache.get(url);
		if(bufferedImage != null) return bufferedImage;
		else {
			fetchImage(url);
			return ResourceManager.getSprite("default-sprite");
		}
	}

	private static void fetchImage(final String url) {
		if(!downloadingImages.contains(url)) {
			new Thread() {
				@Override
				public void run() {
					try {
						downloadingImages.add(url);
						final BufferedImage bufferedImage = fromURL(url);
						StarLoaderTexture.runOnGraphicsThread(new Runnable() {
							@Override
							public void run() {
								try {
									Sprite sprite = StarLoaderTexture.newSprite(bufferedImage, EdenCore.getInstance(), url);
									sprite.setPositionCenter(false);
									sprite.setWidth(SPRITE_WIDTH);
									sprite.setHeight(SPRITE_HEIGHT);
									imgCache.put(url, sprite);
								} catch(Exception ignored) {}
							}
						});
						downloadingImages.remove(url);
					} catch(Exception ignored) {}
				}
			}.start();
		}
	}

	private static BufferedImage fromURL(String u) {
		BufferedImage image = null;
		try {
			URL url = new URL(u);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setRequestProperty("User-Agent", "NING/1.0");
			InputStream stream = urlConnection.getInputStream();
			image = ImageIO.read(stream);
		} catch(IOException ignored) {}
		return image;
	}

	public static BufferedImage generateEntityPreview(SegmentController entity) {
		try {
			FrameBufferObjects frameBuffer = new FrameBufferObjects("EntityPreview", 512, 512);
			frameBuffer.initialize();
			frameBuffer.enable();
			GL11.glClearColor(0, 0, 0, 0);
			GL11.glViewport(0, 0, 512, 512);
			GlUtil.glDisable(GL11.GL_LIGHTING);
			GlUtil.glDisable(GL11.GL_DEPTH_TEST);
			GlUtil.glPushMatrix();
			GlUtil.glMatrixMode(GL11.GL_PROJECTION);
			GlUtil.glPushMatrix();
			GlUtil.glLoadIdentity();
			GlUtil.gluOrtho2D(0, 512, 512, 0);
			GlUtil.glMatrixMode(GL11.GL_MODELVIEW);
			GlUtil.glLoadIdentity();
			EntityPreviewCamera camera = new EntityPreviewCamera(GameClient.getClientState(), entity);
			entity.drawDebugTransform();
			GlUtil.glMatrixMode(GL11.GL_PROJECTION);
			GlUtil.glPopMatrix();
			GlUtil.glMatrixMode(GL11.GL_MODELVIEW);
			GlUtil.glPopMatrix();
			GlUtil.glEnable(GL11.GL_DEPTH_TEST);
			GlUtil.glEnable(GL11.GL_LIGHTING);
			GL11.glViewport(0, 0, GLFrame.getWidth(), GLFrame.getHeight());
			frameBuffer.disable();
			frameBuffer.cleanUp();
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	public static class EntityPreviewCamera extends Camera {
		public EntityPreviewCamera(StateInterface stateInterface, SegmentController segmentController) {
			super(stateInterface, new FixedViewer(segmentController));
		}

		@Override
		public void update(Timer timer, boolean server) {
		}
	}
}
