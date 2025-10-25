package com.sighs.generalfeedback.client;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BooleanButton extends CustomButton {
    public boolean value = false;

    public BooleanButton(int x, int y, int width, int height, Component text, OnPress onPress) {
        super(x, y, width, height, text, onPress);
    }

    @Override
    protected ResourceLocation getTexture() {
        return value ? BUTTON_PRESSED_TEXTURE : BUTTON_NORMAL_TEXTURE;
    }
}