package com.sighs.generalfeedback.client;

import com.sighs.generalfeedback.init.Entry;
import com.sighs.generalfeedback.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static com.sighs.generalfeedback.Generalfeedback.MODID;

public class FeedbackButton extends Button {
    private static final ResourceLocation BUTTON_NORMAL_TEXTURE = new ResourceLocation(MODID, "textures/gui/feedback_0.png");
    private static final ResourceLocation BUTTON_PRESSED_TEXTURE = new ResourceLocation(MODID, "textures/gui/feedback_1.png");

    public FeedbackButton(int x, int y, int width, int height, Entry entry) {
        super(x, y, width, height,
                Component.nullToEmpty(""),
                button -> {
                    Minecraft.getInstance().setScreen(new FeedbackScreen(entry));
                },
                Button.DEFAULT_NARRATION
        );
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation texture = isHovered ? BUTTON_PRESSED_TEXTURE : BUTTON_NORMAL_TEXTURE;
        int textureSize = 18;
        int border = 6;

        GuiUtils.drawNinePatch(graphics, texture, getX(), getY(), getWidth(), getHeight(), textureSize, border);

        int textColor = isHovered ? 0xFFFFA0 : 0xFFFFFF;
        graphics.drawCenteredString(Minecraft.getInstance().font,
                getMessage(), getX() + getWidth() / 2,
                getY() + (getHeight() - 10) / 2, textColor);
    }

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
