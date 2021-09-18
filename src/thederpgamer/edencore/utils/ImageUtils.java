package thederpgamer.edencore.utils;

import api.common.GameClient;
import org.lwjgl.opengl.GL11;
import org.schema.game.common.controller.SegmentController;
import org.schema.schine.graphicsengine.camera.Camera;
import org.schema.schine.graphicsengine.camera.viewer.FixedViewer;
import org.schema.schine.graphicsengine.core.FrameBufferObjects;
import org.schema.schine.graphicsengine.core.GLFrame;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.schine.network.StateInterface;

import java.awt.image.BufferedImage;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/17/2021]
 */
public class ImageUtils {

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

    public static class EntityPreviewCamera extends Camera  {

        public EntityPreviewCamera(StateInterface stateInterface, SegmentController segmentController) {
            super(stateInterface, new FixedViewer(segmentController));
        }

        @Override
        public void update(Timer timer, boolean server) {

        }
    }
}
