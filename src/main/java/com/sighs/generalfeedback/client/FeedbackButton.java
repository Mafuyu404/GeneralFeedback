package com.sighs.generalfeedback.client;

import com.sighs.generalfeedback.init.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static com.sighs.generalfeedback.Generalfeedback.MODID;

public class FeedbackButton extends CustomButton {
    private static final ResourceLocation BUTTON_NORMAL_TEXTURE = new ResourceLocation(MODID, "textures/gui/feedback_0.png");
    private static final ResourceLocation BUTTON_PRESSED_TEXTURE = new ResourceLocation(MODID, "textures/gui/feedback_1.png");

    public FeedbackButton(int x, int y, int width, int height, Entry entry) {
        super(x, y, width, height, Component.nullToEmpty(""), button -> {
            Minecraft.getInstance().setScreen(new FeedbackScreen(entry));
        });
    }

    @Override
    protected ResourceLocation getTexture() {
        return isHovered ? BUTTON_PRESSED_TEXTURE : BUTTON_NORMAL_TEXTURE;
    }
}