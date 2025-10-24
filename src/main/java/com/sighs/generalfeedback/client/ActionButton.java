package com.sighs.generalfeedback.client;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ActionButton extends CustomButton {
    public ActionButton(int x, int y, int width, int height, Component text, OnPress onPress) {
        super(x, y, width, height, text, onPress);
    }

    @Override
    protected ResourceLocation getTexture() {
        return isHovered ? BUTTON_PRESSED_TEXTURE : BUTTON_NORMAL_TEXTURE;
    }
}