package com.sighs.generalfeedback.client;

import com.sighs.generalfeedback.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static com.sighs.generalfeedback.Generalfeedback.MODID;

public abstract class CustomButton extends Button {
    protected static final ResourceLocation BUTTON_NORMAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/button_0.png");
    protected static final ResourceLocation BUTTON_PRESSED_TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/button_1.png");

    private static final int TEXTURE_SIZE = 18;
    private static final int BORDER = 6;

    public CustomButton(int x, int y, int width, int height, Component text, OnPress onPress) {
        super(x, y, width, height, text, onPress, Button.DEFAULT_NARRATION);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation texture = getTexture();

        GuiUtils.drawNinePatch(graphics, texture, getX(), getY(), getWidth(), getHeight(), TEXTURE_SIZE, BORDER);

        int textColor = isHoveredOrFocused() ? 0xFFFFA0 : 0xFFFFFF;
        graphics.drawCenteredString(Minecraft.getInstance().font,
                getMessage(),
                getX() + getWidth() / 2,
                getY() + (getHeight() - 10) / 2,
                textColor);
    }

    protected abstract ResourceLocation getTexture();


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible && this.clicked(mouseX, mouseY)) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }
}