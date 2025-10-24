package com.sighs.generalfeedback.mixin;

import com.sighs.generalfeedback.ModConfig;
import com.sighs.generalfeedback.client.FeedbackButton;
import com.sighs.generalfeedback.loader.EntryCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PauseScreen.class)
public class PauseScreenMixin extends Screen {
    protected PauseScreenMixin(Component p_96550_) {
        super(p_96550_);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/PauseScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;"))
    private void feedback(CallbackInfo ci) {
        if (ModConfig.getPauseFeedbackButton()) {
            if (EntryCache.UnitMapCache.containsKey("default")) {
                var window = Minecraft.getInstance().getWindow();
                int x = window.getGuiScaledWidth() / 2 - 88 - 18 - 18;
                int y = window.getGuiScaledHeight() / 2 + 13;
                addRenderableWidget(new FeedbackButton(
                        x, y, 18, 18,
                        EntryCache.UnitMapCache.get("default")
                ));
            }
        }
    }
}
