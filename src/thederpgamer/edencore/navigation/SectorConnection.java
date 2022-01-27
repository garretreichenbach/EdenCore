package thederpgamer.edencore.navigation;

import java.io.Serializable;
import javax.vecmath.Vector4f;
import org.schema.common.util.linAlg.Vector3i;

/** small container class for line drawing between sectors */
public class SectorConnection implements Serializable {
  Vector3i start;
  Vector3i end;
  Vector4f startColor;
  Vector4f endColor;

  public SectorConnection(
      Vector3i sectorStart, Vector3i sectorEnd, Vector4f startColor, Vector4f endColor) {
    this.start = sectorStart;
    this.end = sectorEnd;
    this.startColor = startColor;
    this.endColor = endColor;
  }

  public Vector3i getStart() {
    return start;
  }

  public void setStart(Vector3i start) {
    this.start = start;
  }

  public Vector3i getEnd() {
    return end;
  }

  public void setEnd(Vector3i end) {
    this.end = end;
  }

  public Vector4f getStartColor() {
    return startColor;
  }

  public void setStartColor(Vector4f startColor) {
    this.startColor = startColor;
  }

  public Vector4f getEndColor() {
    return endColor;
  }

  public void setEndColor(Vector4f endColor) {
    this.endColor = endColor;
  }
}
