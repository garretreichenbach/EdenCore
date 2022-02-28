package thederpgamer.edencore.drawer;

import api.common.GameClient;
import api.utils.game.PlayerUtils;
import com.bulletphysics.linearmath.Transform;
import java.nio.FloatBuffer;
import java.util.Observable;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.schema.common.util.ByteUtil;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.view.GameResourceLoader;
import org.schema.game.client.view.effects.ShieldDrawerManager;
import org.schema.game.client.view.shader.CubeMeshQuadsShader13;
import org.schema.game.common.controller.SegmentController;
import org.schema.schine.graphicsengine.core.*;
import org.schema.schine.graphicsengine.core.settings.EngineSettings;
import org.schema.schine.graphicsengine.shader.Shader;
import org.schema.schine.graphicsengine.shader.ShaderLibrary;
import org.schema.schine.graphicsengine.shader.Shaderable;
import thederpgamer.edencore.data.other.BuildSectorData;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [02/26/2022]
 */
public class BuildSectorBoundary extends Observable implements Drawable, Shaderable {

  public BuildSectorData sectorData;
  private Vector3f center = new Vector3f();

  public static final int MAX_NUM = 8;
  private static final FloatBuffer fbAlphas = BufferUtils.createFloatBuffer(MAX_NUM);
  private static final FloatBuffer fbDmg = BufferUtils.createFloatBuffer(MAX_NUM);
  private static final FloatBuffer fbPerc = BufferUtils.createFloatBuffer(MAX_NUM);
  private static final FloatBuffer fb = BufferUtils.createFloatBuffer(MAX_NUM * 3);
  private static final Vector3f[] tmpP = new Vector3f[16];
  private static final Transform[] contacts = new Transform[MAX_NUM];
  private static final Transform closestBorder = new Transform();

  static {
    for (int i = 0; i < tmpP.length; i++) {
      tmpP[i] = new Vector3f();
    }
  }

  public Shader s = null;
  private float[] alphas = new float[MAX_NUM];
  private Vector4f[] points = new Vector4f[MAX_NUM];
  private int collisionNum;
  private float minAlpha = 0.0f;
  private float maxDistance = 4f;
  private boolean pointsChanged = false;
  private float[] percent = new float[MAX_NUM];

  private Vector3f[] tempQuads = new Vector3f[4];

  public BuildSectorBoundary(BuildSectorData sectorData) {
    this.sectorData = sectorData;
    for (int i = 0; i < MAX_NUM; i++) {
      points[i] = new Vector4f();
      contacts[i] = null;
    }
  }

  public void addIntersect(Transform transform) {
    transform.inverse();
    center.set(transform.origin);
    addCollision(center);
    if (collisionNum > 0) {
      setChanged();
      notifyObservers(true);
    }
  }

  private void addCollision(Vector3f c) {
    if (collisionNum < MAX_NUM) {
      alphas[collisionNum] = 1.0f;
      points[collisionNum].set(c.x, c.y, c.z, 1.0f);
      percent[collisionNum] = 1.0f;
      contacts[collisionNum] = new Transform();
      contacts[collisionNum].origin.set(c);
      collisionNum++;
      pointsChanged = true;
    }
  }

  private boolean hasCollisionInRange(float x, float y, float z) {
    for (int i = 0; i < collisionNum; i++) {
      float xx = x - points[i].x;
      float yy = y - points[i].y;
      float zz = z - points[i].z;
      float len = xx * xx + yy * yy + zz * zz;
      if (len < 576) return true;
    }
    return false;
  }

  private boolean hasContact(Transform transform) {
    if (collisionNum == 0) return false;
    else return hasCollisionInRange(transform.origin.x, transform.origin.y, transform.origin.z);
  }

  @Override
  public void onInit() {}

  @Override
  public void draw() {
    for (int i = 0; i < MAX_NUM; i++) {
      if (contacts[i] != null && contacts[i].origin.length() > 0) drawContact(contacts[i]);
    }
  }

  private void drawContact(Transform contact) {
    contact.inverse();
    GlUtil.glMatrixMode(GL11.GL_PROJECTION);
    GlUtil.glPushMatrix();

    float aspect = (float) GLFrame.getWidth() / (float) GLFrame.getHeight();
    GlUtil.gluPerspective(
        Controller.projectionMatrix,
        (Float) EngineSettings.G_FOV.getCurrentState(),
        aspect,
        10,
        1000,
        true);
    GlUtil.glMatrixMode(GL11.GL_MODELVIEW);
    Vector3i selectedPos = new Vector3i();

    selectedPos.x = ByteUtil.modU16(selectedPos.x);
    selectedPos.y = ByteUtil.modU16(selectedPos.y);
    selectedPos.z = ByteUtil.modU16(selectedPos.z);
    /*
    GlUtil.glBegin(GL11.GL_QUADS);
    tempQuads = getQuadCorners(contact);

     */
    ShaderLibrary.cubeShieldShader.setShaderInterface(this);
    ShaderLibrary.cubeShieldShader.load();
    updateShaderParameters(ShaderLibrary.cubeShieldShader);
    /*
    for(Vector3f quad : tempQuads) GL11.glVertex3f(quad.x, quad.y, quad.z);
    //for(Vector4f point : points) GL11.glVertex3f(point.x, point.y, point.z);

     */
    ShaderLibrary.cubeShieldShader.unload();
    // GlUtil.glEnd();

    GlUtil.glMatrixMode(GL11.GL_PROJECTION);
    GlUtil.glPopMatrix();
    GlUtil.glMatrixMode(GL11.GL_MODELVIEW);
  }

  private Vector3f[] getQuadCorners(Transform transform) {
    Vector3f[] directions =
        new Vector3f[] {
          GlUtil.getLeftVector(new Vector3f(), closestBorder),
          GlUtil.getUpVector(new Vector3f(), closestBorder),
          GlUtil.getForwardVector(new Vector3f(), closestBorder)
        };
    Transform currentPos = new Transform();
    if (PlayerUtils.getCurrentControl(GameClient.getClientPlayerState())
        instanceof SegmentController)
      currentPos.set(
          ((SegmentController) PlayerUtils.getCurrentControl(GameClient.getClientPlayerState()))
              .getWorldTransform());
    else GameClient.getClientPlayerState().getWordTransform(currentPos);
    Vector3f[] quadCorners = new Vector3f[4];
    quadCorners[0] =
        new Vector3f(
            (transform.origin.x * directions[0].x) - currentPos.origin.x,
            (transform.origin.x * directions[1].y) + currentPos.origin.y,
            (transform.origin.z * directions[2].z) + currentPos.origin.z);
    quadCorners[1] =
        new Vector3f(
            transform.origin.x + (currentPos.origin.x * directions[0].length()),
            transform.origin.y + (currentPos.origin.y * directions[1].length()),
            transform.origin.z + (currentPos.origin.z * directions[2].length()));
    quadCorners[2] =
        new Vector3f(
            transform.origin.x - (currentPos.origin.x * directions[0].length()),
            transform.origin.y - (currentPos.origin.y * directions[1].length()),
            transform.origin.z + (currentPos.origin.z * directions[2].length()));
    quadCorners[3] =
        new Vector3f(
            transform.origin.x + (currentPos.origin.x * directions[0].length()),
            transform.origin.y - (currentPos.origin.y * directions[1].length()),
            transform.origin.z + (currentPos.origin.z * directions[2].length()));
    return quadCorners;
  }

  public void update(Timer timer) {
    for (int i = 0; i < collisionNum; i++) {
      alphas[i] -= timer.getDelta() * 0.7f;
      if (alphas[i] <= 0) {
        alphas[i] = 0;
        alphas[i] = alphas[collisionNum - 1];
        points[i].set(points[collisionNum - 1]);
        contacts[i] = null;
        collisionNum--;
        pointsChanged = true;
      }
    }

    if (collisionNum <= 0) {
      setChanged();
      notifyObservers(false);
    }
  }

  @Override
  public void updateShader(DrawableScene drawableScene) {}

  @Override
  public void updateShaderParameters(Shader shader) {
    if (shader != null) {
      GlUtil.glActiveTexture(GL13.GL_TEXTURE0);
      GlUtil.glBindTexture(
          GL11.GL_TEXTURE_2D,
          Controller.getResLoader()
              .getSprite("shield_tex")
              .getMaterial()
              .getTexture()
              .getTextureId());
      GlUtil.glActiveTexture(GL13.GL_TEXTURE1);
      GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, GameResourceLoader.effectTextures[0].getTextureId());
      GlUtil.glActiveTexture(GL13.GL_TEXTURE2);
      GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, GameResourceLoader.effectTextures[1].getTextureId());
      if (shader.recompiled) {
        GlUtil.updateShaderInt(shader, "m_ShieldTex", 0);
        GlUtil.updateShaderInt(shader, "m_Distortion", 1);
        GlUtil.updateShaderInt(shader, "m_Noise", 2);
        GlUtil.updateShaderCubeNormalsBiNormalsAndTangentsBoolean(shader);
        FloatBuffer fb = GlUtil.getDynamicByteBuffer(6 * 3 * 4, 0).asFloatBuffer();
        fb.rewind();
        for (int i = 0; i < CubeMeshQuadsShader13.quadPosMark.length; i++) {
          fb.put(CubeMeshQuadsShader13.quadPosMark[i].x);
          fb.put(CubeMeshQuadsShader13.quadPosMark[i].y);
          fb.put(CubeMeshQuadsShader13.quadPosMark[i].z);
        }

        fb.rewind();
        GlUtil.updateShaderFloats3(shader, "quadPosMark", fb);

        shader.recompiled = false;
      }

      for (int i = 0; i < collisionNum; i++) {
        fbAlphas.put(i, alphas[i]);
        if (pointsChanged) {
          fb.put(i * 3, points[i].x);
          fb.put(i * 3 + 1, points[i].y);
          fb.put(i * 3 + 2, points[i].z);
          fbDmg.put(i, points[i].w);
          fbPerc.put(i, percent[i]);
        }
      }

      fbAlphas.rewind();
      GlUtil.updateShaderFloats1(shader, "m_CollisionAlphas", fbAlphas);

      if (pointsChanged) {
        fb.rewind();
        GlUtil.updateShaderFloats3(shader, "m_Collisions", fb);
        pointsChanged = false;
        GlUtil.updateShaderInt(shader, "m_CollisionNum", collisionNum);

        fbDmg.rewind();
        GlUtil.updateShaderFloats1(shader, "m_Damages", fbDmg);

        fbPerc.rewind();
        GlUtil.updateShaderFloats1(shader, "m_Percent", fbPerc);
      }
      GlUtil.updateShaderFloat(shader, "m_MinAlpha", minAlpha);
      GlUtil.updateShaderFloat(shader, "m_MaxDistance", maxDistance);
      GlUtil.updateShaderFloat(shader, "m_Time", ShieldDrawerManager.time);
    }
  }

  @Override
  public void cleanUp() {}

  @Override
  public void onExit() {
    GlUtil.glActiveTexture(GL13.GL_TEXTURE2);
    GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    GlUtil.glActiveTexture(GL13.GL_TEXTURE1);
    GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    GlUtil.glActiveTexture(GL13.GL_TEXTURE0);
    GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, 0);
  }

  @Override
  public boolean isInvisible() {
    return false;
  }

  public void setClosestBorder(Transform closest) {
    closestBorder.set(closest);
  }
}
