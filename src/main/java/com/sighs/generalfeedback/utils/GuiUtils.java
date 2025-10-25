package com.sighs.generalfeedback.utils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class GuiUtils {
    public static void drawNinePatch(GuiGraphics graphics, ResourceLocation texture,
                                     int x, int y, int width, int height,
                                     int textureSize, int border) {

        // 角落（不拉伸）
        blitRegion(graphics, texture, x, y, border, border, 0, 0, border, border, textureSize, textureSize); // 左上
        blitRegion(graphics, texture, x + width - border, y, border, border, textureSize - border, 0, border, border, textureSize, textureSize); // 右上
        blitRegion(graphics, texture, x, y + height - border, border, border, 0, textureSize - border, border, border, textureSize, textureSize); // 左下
        blitRegion(graphics, texture, x + width - border, y + height - border, border, border, textureSize - border, textureSize - border, border, border, textureSize, textureSize); // 右下

        // 上边（横向拉伸）
        if (width > border * 2) {
            blitRegion(graphics, texture, x + border, y, width - border * 2, border, border, 0, textureSize - 2 * border, border, textureSize, textureSize);
        }

        // 下边（横向拉伸）
        if (width > border * 2) {
            blitRegion(graphics, texture, x + border, y + height - border, width - border * 2, border, border, textureSize - border, textureSize - 2 * border, border, textureSize, textureSize);
        }

        // 左边（纵向拉伸）
        if (height > border * 2) {
            blitRegion(graphics, texture, x, y + border, border, height - border * 2, 0, border, border, textureSize - 2 * border, textureSize, textureSize);
        }

        // 右边（纵向拉伸）
        if (height > border * 2) {
            blitRegion(graphics, texture, x + width - border, y + border, border, height - border * 2, textureSize - border, border, border, textureSize - 2 * border, textureSize, textureSize);
        }

        // 中心（双轴拉伸）
        if (width > border * 2 && height > border * 2) {
            blitRegion(graphics, texture, x + border, y + border, width - border * 2, height - border * 2, border, border, textureSize - 2 * border, textureSize - 2 * border, textureSize, textureSize);
        }
    }

    private static void blitRegion(GuiGraphics g, ResourceLocation tex,
                                   int x, int y, int w, int h,
                                   int u, int v, int uw, int vh, int texW, int texH) {
        g.blit(RenderType::guiTextured, tex, x, y, (float) u, (float) v, w, h, uw, vh, texW, texH);
    }
}