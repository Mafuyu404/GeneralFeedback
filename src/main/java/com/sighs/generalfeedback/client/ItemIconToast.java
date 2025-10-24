package com.sighs.generalfeedback.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ItemIconToast implements Toast {
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
        gui.blit(TEXTURE, 0, 0, 0, 0, this.width(), this.height());

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
