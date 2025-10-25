package com.sighs.generalfeedback.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemIconToast implements Toast {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/advancement");
    private final Component title;
    private final Component description;
    private final ItemStack icon;

    public ItemIconToast(Component title, Component description, ItemStack icon) {
        this.title = title;
        this.description = description;
        this.icon = icon;
    }

    @Override
    public Visibility render(GuiGraphics gui, ToastComponent toastComponent, long timer) {
        gui.blitSprite(BACKGROUND_SPRITE, 0, 0, this.width(), this.height());

        gui.renderFakeItem(icon, 8, 8);

        gui.drawString(toastComponent.getMinecraft().font, title.getString(), 30, 7, 0xFFD700);
        gui.drawString(toastComponent.getMinecraft().font, description.getString(), 30, 18, 0xFFFFFF);

        return timer >= 2000L ? Visibility.HIDE : Visibility.SHOW;
    }

    public static void show(Component title, Component description, ItemStack icon) {
        Minecraft.getInstance().getToasts().addToast(new ItemIconToast(title, description, icon));
    }

    public static void create(String title, String desc, ItemStack itemStack) {
        show(Component.translatable(title), Component.translatable(desc), itemStack);
    }
}
