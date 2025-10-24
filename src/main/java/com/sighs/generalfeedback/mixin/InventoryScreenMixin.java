package com.sighs.generalfeedback.mixin;

import com.sighs.generalfeedback.ModConfig;
import com.sighs.generalfeedback.client.FeedbackButton;
import com.sighs.generalfeedback.loader.EntryCache;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InventoryScreen.class)
public class InventoryScreenMixin extends Screen {
    @Shadow
    @Final
    private RecipeBookComponent recipeBookComponent;

    protected InventoryScreenMixin(Component p_96550_) {
        super(p_96550_);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;"))
    private void feedback(CallbackInfo ci) {
        if (ModConfig.getInventoryFeedbackButton()) {
            int leftPos = this.recipeBookComponent.updateScreenPosition(this.width, 176);
            if (EntryCache.UnitMapCache.containsKey("default")) {
                addRenderableWidget(new FeedbackButton(
                        leftPos + 153, this.height / 2 - 22, 18, 18,
                        EntryCache.UnitMapCache.get("default")
                ));
            }
        }
    }
}
