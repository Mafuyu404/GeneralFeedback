package com.sighs.generalfeedback.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ItemIconToast implements Toast {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/advancement");
    private final Component title;
    private final Component description;
    private final ItemStack icon;
    private Toast.Visibility wantedVisibility = Toast.Visibility.HIDE;

    public ItemIconToast(Component title, Component description, ItemStack icon) {
        this.title = title;
        this.description = description;
        this.icon = icon;
    }

    public static void show(Component title, Component description, ItemStack icon) {
        Minecraft.getInstance().getToastManager().addToast(new ItemIconToast(title, description, icon));
    }

    public static void create(String title, String desc, ItemStack itemStack) {
        show(Component.translatable(title), Component.translatable(desc), itemStack);
    }

    @Override
    public Visibility getWantedVisibility() {
        return this.wantedVisibility;
    }

    @Override
    public void update(ToastManager toastManager, long l) {
        this.wantedVisibility = l >= 2000L ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public void render(GuiGraphics guiGraphics, Font font, long l) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, 0, 0, this.width(), this.height());

        guiGraphics.renderFakeItem(icon, 8, 8);

        guiGraphics.drawString(font, title.getString(), 30, 7, 0xFFFFD700);
        guiGraphics.drawString(font, description.getString(), 30, 18, 0xFFFFFFFF);
    }
}
