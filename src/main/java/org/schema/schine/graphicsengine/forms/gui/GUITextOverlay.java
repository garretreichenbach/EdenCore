package org.schema.schine.graphicsengine.forms.gui;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;
import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;
import org.schema.common.FastMath;
import org.schema.game.network.objects.ChatMessage;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.font.FontLibrary.FontSize;
import org.schema.schine.input.InputState;
import soe.edencore.data.player.PlayerData;
import soe.edencore.server.ServerDatabase;
import soe.edencore.utils.ColorUtils;

/**
 * GUITextOverlay.java (modified)
 * <Description>
 *
 * @author Schema
 */
public class GUITextOverlay extends GUIElement {
    private static final Object2ObjectOpenHashMap<UnicodeFont, int[]> fontWidths = new Object2ObjectOpenHashMap();
    private static final String itoa64 = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz ?,;+*#'=)({}\\&%$ï¿½\"!@-_.:,;|~^";
    public static UnicodeFont defaultFont;
    public boolean doDepthTest;
    public GUIResizableElement autoWrapOn;
    public boolean wrapSimple;
    long dirtyTime;
    private int width;
    private int height;
    private UnicodeFont font;
    private boolean firstDraw;
    private List<Object> text;
    private Color color;
    private int limitTextDraw;
    private boolean beginTextAtLast;
    private boolean blend;
    private int clipStartPX;
    private int clipEndPX;
    private boolean useUncachedDefaultFont;
    private final ObjectArrayList<Object> textCache;
    private final ObjectArrayList<String> textCacheCache;
    private boolean dirty;
    private int lastWrapWidth;
    private int maxLineWidth;
    private int textHeight;
    public boolean autoHeight;
    public int limitTextWidth;
    private String limstr;
    private int limAmount;
    private String origStr;
    public boolean debug;
    private boolean wasCacheDirty;

    public GUITextOverlay(int var1, int var2, InputState var3) {
        super(var3);
        this.doDepthTest = false;
        this.wrapSimple = true;
        this.dirtyTime = System.currentTimeMillis();
        this.firstDraw = true;
        this.color = new Color(Color.white);
        this.limitTextDraw = -1;
        this.blend = true;
        this.clipStartPX = -1;
        this.clipEndPX = -1;
        this.textCache = new ObjectArrayList();
        this.textCacheCache = new ObjectArrayList();
        this.limstr = "";
        this.limAmount = 3;
        this.width = var1;
        this.height = var2;
    }

    public GUITextOverlay(int var1, int var2, UnicodeFont var3, InputState var4) {
        this(var1, var2, var4);
        this.font = var3;
    }

    public GUITextOverlay(int var1, int var2, UnicodeFont var3, Color var4, InputState var5) {
        this(var1, var2, var3, var5);
        this.setColor(var4);
    }

    public GUITextOverlay(int var1, int var2, FontSize var3, Color var4, InputState var5) {
        this(var1, var2, var3.getFont(), var4, var5);
    }

    public GUITextOverlay(int var1, int var2, FontSize var3, InputState var4) {
        this(var1, var2, var3.getFont(), var4);
    }

    private int[] createFontTable(UnicodeFont var1) {
        int var3 = 0;

        for(int var2 = 0; var2 < 96; ++var2) {
            var3 = Math.max(var3, "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz ?,;+*#'=)({}\\&%$ï¿½\"!@-_.:,;|~^".charAt(var2));
        }

        int[] var4 = new int[var3 + 1];

        for(var3 = 0; var3 < 96; ++var3) {
            var4["./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz ?,;+*#'=)({}\\&%$ï¿½\"!@-_.:,;|~^".charAt(var3)] = this.getWidthOfFont(String.valueOf("./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz ?,;+*#'=)({}\\&%$ï¿½\"!@-_.:,;|~^".charAt(var3)));
        }

        return var4;
    }

    public void cleanUp() {
    }

    public String generateToolTip() {
        return this.origStr != null ? this.origStr : this.getText().get(0).toString();
    }

    public void draw() {
        if (this.isRenderable()) {
            this.drawText();
        }

        if (this.autoHeight) {
            this.setHeight(this.getTextHeight());
        }

        if (this.limitTextWidth > 0) {
            if (this.origStr == null) {
                this.origStr = this.getText().get(0).toString();
            }

            while(this.getMaxLineWidth() > this.limitTextWidth) {
                ++this.limAmount;
                this.limstr = this.origStr.substring(0, this.origStr.length() - this.limAmount) + "...";
                this.setTextSimple(this.limstr);
                this.updateTextSize();
            }
        }

    }

    public void onInit() {
        if (this.firstDraw) {
            if (this.font == null) {
                if (defaultFont == null) {
                    defaultFont = FontLibrary.getRegularArial12WhiteWithoutOutline();
                }

                if (this.useUncachedDefaultFont) {
                    this.font = FontLibrary.getRegularArial12WhiteWithoutOutlineUncached();
                } else {
                    this.font = defaultFont;
                }
            }

            this.font.setDisplayListCaching(true);
            if (this.text == null) {
                this.text = new ArrayList();
            }

            this.firstDraw = false;
        }
    }

    public void doOrientation() {
    }

    public float getHeight() {
        return (float)this.height;
    }

    public float getWidth() {
        return (float)this.width;
    }

    public boolean isPositionCenter() {
        return false;
    }

    public void setWidth(int var1) {
        this.width = var1;
    }

    public void setHeight(int var1) {
        this.height = var1;
    }

    private void addCache(final Object var1, final ColoredInterface var2) {
        this.wasCacheDirty = true;
        if (var2 == null) {
            this.textCache.add(var1);
        } else {
            this.textCache.add(new ColoredInterface() {
                public Vector4f getColor() {
                    return var2.getColor();
                }

                public String toString() {
                    return var1.toString();
                }
            });
        }
    }

    public void updateCacheForced() {
        this.createCache(true);
    }

    private void createCache(boolean var1) {
        if (this.text == null) {
            this.text = new ObjectArrayList();
            this.text.add("NULLOBJECT");
        }

        int var2;
        for(var2 = 0; var2 < this.text.size(); ++var2) {
            if (this.text.get(var2) == null) {
                this.text.set(var2, "NULL");
            }
        }

        label191: {
            this.dirty = false;
            if (!var1 && (this.autoWrapOn == null || this.lastWrapWidth == (int)this.autoWrapOn.getWidth())) {
                if (this.text.size() == this.textCacheCache.size()) {
                    var2 = 0;

                    while(true) {
                        if (var2 >= this.text.size()) {
                            break label191;
                        }

                        if (!this.text.get(var2).toString().equals(this.textCacheCache.get(var2))) {
                            break;
                        }

                        ++var2;
                    }
                }
            } else if (!this.wrapSimple) {
                this.dirtyTime = System.currentTimeMillis();
                break label191;
            }

            this.dirty = true;
        }

        if (this.dirtyTime > 0L && System.currentTimeMillis() - this.dirtyTime > 40L) {
            this.dirty = true;
            this.dirtyTime = 0L;
        }

        this.lastWrapWidth = (int)(this.autoWrapOn != null ? this.autoWrapOn.getWidth() : 0.0F);
        if (this.dirty) {
            this.textCache.clear();
            this.textCacheCache.clear();

            for(int var8 = 0; var8 < this.text.size(); ++var8) {
                this.textCacheCache.add(this.text.get(var8).toString());
                if (this.isBeginTextAtLast()) {
                    var2 = this.text.size() - 1 - var8;
                } else {
                    var2 = var8;
                }

                String var3 = this.text.get(var2).toString();
                String var5;
                int var6;
                int var7;
                String var10;
                if (this.autoWrapOn != null) {
                    if (!var3.contains("\n")) {
                        var3 = this.autoWrap(this.autoWrapOn, var3);
                    } else {
                        StringBuffer var4 = new StringBuffer();
                        var5 = var3;
                        var6 = 0;

                        for(var7 = 0; (var6 = var5.indexOf("\n", var6)) >= 0; var7 = var6) {
                            if (var6 == var7) {
                                var4.append("\n");
                            } else {
                                var10 = var5.subSequence(var7, var6).toString();
                                var4.append(this.autoWrap(this.autoWrapOn, var10));
                                var4.append("\n");
                            }

                            ++var6;
                        }

                        var10 = var5.subSequence(var7, var3.length()).toString();
                        var4.append(this.autoWrap(this.autoWrapOn, var10));
                        var3 = var4.toString();
                    }
                }

                assert var3 != null;

                if (!var3.contains("\n")) {
                    this.addCache(this.text.get(var2), (ColoredInterface)null);
                } else {
                    ColoredInterface var9 = null;
                    if (this.text.get(var2) instanceof ColoredInterface) {
                        var9 = (ColoredInterface)this.text.get(var2);
                    }

                    var5 = var3;
                    var6 = 0;

                    for(var7 = 0; (var6 = var5.indexOf("\n", var6)) >= 0; var7 = var6) {
                        if (var6 == var7) {
                            this.addCache("", (ColoredInterface)null);
                        } else {
                            var10 = var5.subSequence(var7, var6).toString();
                            this.addCache(var10, var9);
                        }

                        ++this.limitTextDraw;
                        ++var6;
                    }

                    assert var7 >= 0;

                    assert var5.length() > 0;

                    if (var7 == var5.length()) {
                        this.addCache("", (ColoredInterface)null);
                    } else {
                        assert var7 >= 0;

                        assert var5.length() > 0;

                        this.addCache(var5.subSequence(var7, var5.length()).toString(), var9);
                    }

                    this.limitTextDraw += 2;

                    assert !this.text.isEmpty();
                }
            }
        }

        if (this.dirty) {
            this.onDirty();
        }

    }

    private String autoWrap(GUIResizableElement var1, String var2) {
        if (var2.length() <= 1) {
            return var2;
        } else {
            int var3;
            if (var1 instanceof GUIScrollablePanel) {
                var3 = (int)Math.max(1.0F, ((GUIScrollablePanel)var1).getClipWidth());
            } else {
                var3 = (int)Math.max(1.0F, var1.getWidth());
            }

            return this.wrap(var2, var3);
        }
    }

    private int getWidthOfFont(String var1) {
        return FontLibrary.getMetrics(this.font).stringWidth(var1);
    }

    private int findLenOnWidth(String var1, int var2) {
        if (this.getWidthOfFont(var1) < var2) {
            return var1.length() + 1;
        } else {
            int var3 = this.getWidthOfFont("l");
            var3 = var2 / var3;
            StringBuffer var4;
            (var4 = new StringBuffer(var1.substring(0, Math.min(var1.length(), var3)))).length();

            while(this.getWidthOfFont(var4.toString()) > var2) {
                var4.deleteCharAt(var4.length() - 1);
            }

            return Math.max(0, var4.length() - 1);
        }
    }

    private String wrap(String var1, int var2) {
        int[] var3;
        if ((var3 = (int[])fontWidths.get(this.font)) == null) {
            var3 = this.createFontTable(this.font);
            fontWidths.put(this.font, var3);
        }

        if ((var1 = var1.trim()).isEmpty()) {
            return "";
        } else {
            int var4;
            int var8;
            if (this.wrapSimple) {
                var4 = 0;

                for(int var5 = 14; var5 < var2 && var4 < var1.length(); ++var4) {
                    char var6 = var1.charAt(var4);
                    int var7 = -1;
                    if (var6 >= 0 && var6 < var3.length) {
                        var7 = var3[var6];
                    }

                    if (var7 <= 0) {
                        var7 = this.font.getSpaceWidth() + 5;
                    }

                    var5 += var7;
                }

                var8 = Math.max(0, var4 + 1);
            } else {
                var8 = this.findLenOnWidth(var1, var2);

                assert var8 >= 0 : "len invalid: " + var8 + ": \"" + var1 + "\"; PX: " + var2;
            }

            if (var1.length() < var8) {
                return var1;
            } else {
                assert var8 >= 0 : "len invalid: " + var8 + ": " + var1;

                if (var1.substring(0, var8).contains("\n")) {
                    return var1.substring(0, var1.indexOf("\n")).trim() + "\n\n" + this.wrap(var1.substring(var1.indexOf("\n") + 1), var8);
                } else {
                    return (var4 = Math.max(var1.lastIndexOf(" ", var8), var1.lastIndexOf("\t", var8))) < 0 ? var1 : var1.substring(0, var4).trim() + "\n" + this.wrap(var1.substring(var4), var2);
                }
            }
        }
    }

    public void onDirty() {
    }

    void drawText() {
        if (this.firstDraw) {
            this.onInit();
        }

        if (this.getFont() == null) {
            try {
                throw new NullPointerException("Font not initialized: " + this.getText());
            } catch (Exception var12) {
                var12.printStackTrace();
            }
        } else {
            GlUtil.glDisable(2896);
            if (translateOnlyMode) {
                this.translate();
            } else {
                GlUtil.glPushMatrix();
                this.transform();
            }

            try {
                if (this.isBlend()) {
                    GlUtil.glEnable(3042);
                    GlUtil.glBlendFunc(770, 771);
                    if (!this.doDepthTest) {
                        GlUtil.glDisable(2929);
                    }
                } else {
                    GlUtil.glDisable(3042);
                }

                Vector2f var1;
                float var2 = (var1 = (Vector2f)FontLibrary.offsetMap.get(this.getFont().getFontFile() + this.getFont().getFont().getSize2D())) != null ? var1.x : 0.0F;
                float var18 = var1 != null ? var1.y : 0.0F;
                int var3 = this.limitTextDraw;
                this.createCache(false);
                if(this.dirty) this.maxLineWidth = 0;

                int var4 = 0;
                int var5;
                if(this.clipStartPX >= 0) {
                    var18 = (float)((var5 = FastMath.fastFloor((float)this.clipStartPX / (float)this.getFont().getLineHeight())) * this.getFont().getLineHeight());
                    var4 = var5;
                }

                for(; var4 < this.textCache.size(); ++var4) {
                    if(this.wasCacheDirty) {
                        try {
                            if(this.textCache.get(var4) == null || this.textCache.get(var4).toString() == null) {
                                try {
                                    throw new Exception("TEXT NULL ON GUI OVERLAY " + this.textCache);
                                } catch(Exception var14) {
                                    var14.printStackTrace();
                                    break;
                                }
                            }
                        } catch(Exception var15) {
                            System.err.println("TEXT NULL ON GUI OVERLAY");
                            var15.printStackTrace();
                        }
                    }

                    if(this.clipEndPX >= 0 && var18 > (float) this.clipEndPX) break;

                     if(this.textCache.get(var4) instanceof ColoredInterface) {
                        Vector4f var19 = ((ColoredInterface)this.textCache.get(var4)).getColor();
                        this.color.r = var19.x;
                        this.color.g = var19.y;
                        this.color.b = var19.z;
                        this.color.a = var19.w;
                        if(this.color.a <= 0.0F) continue;
                    }

                    if(var3 >= 0 && var4 >= var3) break;

                    if(this.textCache.get(var4) == null || this.textCache.get(var4).toString() == null) {
                        try {
                            throw new Exception("TEXT NULL ON GUI OVERLAY " + this.textCache);
                        } catch (Exception var13) {
                            var13.printStackTrace();
                            break;
                        }
                    }
                    String var20 = this.textCache.get(var4).toString();
                    StringBuilder builder = new StringBuilder();

                    if(textCache.get(var4) instanceof ChatMessage) {
                        ChatMessage chatMessage = (ChatMessage) textCache.get(var4);
                        PlayerData playerData = ServerDatabase.getPlayerData(chatMessage.sender);
                        if(playerData != null) var20 = playerData.getRank().chatPrefix + "&1 [" + chatMessage.sender + "]: " + chatMessage.text;
                    }

                    char[] charArray = var20.toCharArray();
                    Color color = Color.white;
                    float xPos = var2;
                    for(int i = 0; i < charArray.length; i ++) {
                        if(charArray[i] == '&' && charArray.length > i + 1) {
                            i ++;
                            color = ColorUtils.fromCode(Character.toLowerCase(charArray[i]));
                        } else {
                            GlUtil.glColor4f(new Vector4f(color.r, color.g, color.b, color.a));
                            this.font.drawDisplayList(xPos, var18, "" + charArray[i], color, 0, 1);
                            xPos += getFont().getWidth("" + charArray[i]);
                            builder.append(charArray[i]);
                        }
                    }

                    var18 += (float)this.getFont().getLineHeight();
                    if(this.dirty) this.maxLineWidth = Math.max(this.maxLineWidth, this.getFont().getWidth(builder.toString()));
                }

                if(this.clipStartPX >= 0 && this.clipEndPX >= 0) {
                    var5 = this.textCache.size();
                    this.textHeight = var5 * this.getFont().getLineHeight();
                } else {
                    this.textHeight = (int)var18;
                }

                if(this.isMouseUpdateEnabled()) this.checkMouseInside();

                GlUtil.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GlUtil.glDisable(3042);
                GlUtil.glEnable(2896);
            } catch(Exception var16) {
                var16.printStackTrace();
            } finally {
                if (translateOnlyMode) {
                    this.translateBack();
                } else {
                    GlUtil.glPopMatrix();
                }

            }

            this.wasCacheDirty = false;
        }
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color var1) {
        this.color.r = var1.r;
        this.color.g = var1.g;
        this.color.b = var1.b;
        this.color.a = var1.a;
    }

    public void setColor(Vector4f var1) {
        this.getColor().r = var1.x;
        this.getColor().g = var1.y;
        this.getColor().b = var1.z;
        this.getColor().a = var1.w;
    }

    public UnicodeFont getFont() {
        return this.font;
    }

    public void setFont(UnicodeFont var1) {
        this.font = var1;
    }

    public int getLimitTextDraw() {
        return this.limitTextDraw;
    }

    public void setLimitTextDraw(int var1) {
        if (var1 != this.limitTextDraw) {
            this.dirty = true;
        }

        this.limitTextDraw = var1;
    }

    public List<Object> getText() {
        return this.text;
    }

    public void setText(List<Object> var1) {
        this.text = var1;
    }

    public boolean isBeginTextAtLast() {
        return this.beginTextAtLast;
    }

    public void setBeginTextAtLast(boolean var1) {
        this.beginTextAtLast = var1;
    }

    public boolean isBlend() {
        return this.blend;
    }

    public void setBlend(boolean var1) {
        this.blend = var1;
    }

    public void setAWTColor(java.awt.Color var1) {
        this.color.a = (float)var1.getAlpha();
        this.color.r = (float)var1.getRed();
        this.color.b = (float)var1.getBlue();
        this.color.g = (float)var1.getGreen();
    }

    public void setClip(int var1, int var2) {
        this.clipStartPX = var1;
        this.clipEndPX = var2;
    }

    public void setColor(float var1, float var2, float var3, float var4) {
        this.getColor().r = var1;
        this.getColor().g = var2;
        this.getColor().b = var3;
        this.getColor().a = var4;
    }

    public void setTextSimple(Object var1) {
        if (this.getText() == null) {
            this.text = new ArrayList();
        } else {
            this.text.clear();
        }

        if (this.debug) {
            try {
                if (var1 == null) {
                    System.err.println("ERROR: STRING SET TO NULL");
                }

                var1.toString();
            } catch (NullPointerException var2) {
                var2.printStackTrace();
            }
        }

        this.text.add(var1);
    }

    public void useUncachedDefaultFont(boolean var1) {
        this.useUncachedDefaultFont = var1;
    }

    public void setTextFromArray(String[] var1) {
        this.text = new ArrayList(var1.length);

        for(int var2 = 0; var2 < var1.length; ++var2) {
            this.text.add(var1[var2]);
        }

    }

    public int getCurrentLineHeight(int var1) {
        return var1 * this.getFont().getLineHeight();
    }

    public int getMaxLineWidth() {
        return this.maxLineWidth;
    }

    public void updateTextSize() {
        this.createCache(false);
        this.maxLineWidth = 0;

        for(int var1 = 0; var1 < this.textCache.size(); ++var1) {
            assert this.textCache != null;

            assert this.textCache.get(var1) != null;
            char[] charArray = textCache.toString().toCharArray();
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < charArray.length; i ++) {
                if(charArray[i] == '&') {
                    i ++;
                } else {
                    builder.append(charArray[i]);
                }
            }

            this.maxLineWidth = Math.max(this.maxLineWidth, this.getFont().getWidth(builder.toString()));
        }

        this.textHeight = this.textCache.size() * this.getFont().getLineHeight();
    }

    public int getTextHeight() {
        return this.textHeight;
    }

    public int getCurrentLines() {
        return this.textCache.size();
    }
}