package com.sighs.generalfeedback.mixin;

import com.sighs.generalfeedback.ModConfig;
import com.sighs.generalfeedback.client.FeedbackButton;
import com.sighs.generalfeedback.loader.EntryCache;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractRecipeBookScreen<InventoryMenu> {

    public InventoryScreenMixin(InventoryMenu recipeBookMenu, RecipeBookComponent<?> recipeBookComponent, Inventory inventory, Component component) {
        super(recipeBookMenu, recipeBookComponent, inventory, component);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void onInit(CallbackInfo ci) {
        if (ModConfig.getInventoryFeedbackButton()) {
            int leftPos = this.leftPos;
            if (EntryCache.UnitMapCache.containsKey("default")) {
                this.addRenderableWidget(new FeedbackButton(
                        leftPos + 153, this.height / 2 - 22, 18, 18,
                        EntryCache.UnitMapCache.get("default")
                ));
            }
        }
    }
}
