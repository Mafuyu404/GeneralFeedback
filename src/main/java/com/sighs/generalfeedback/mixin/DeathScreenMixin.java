package com.sighs.generalfeedback.mixin;

import com.sighs.generalfeedback.Config;
import com.sighs.generalfeedback.client.ActionButton;
import com.sighs.generalfeedback.loader.EntryCache;
import com.sighs.generalfeedback.utils.FeedbackUtils;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DeathScreen.class)
public class DeathScreenMixin extends Screen {
    protected DeathScreenMixin(Component p_96550_) {
        super(p_96550_);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void feedback(CallbackInfo ci) {
        if (Config.DEATH_FEEDBACK_BUTTON.get()) {
            if (EntryCache.UnitMapCache.containsKey("default")) {
                int x = this.width / 2 - 100;
                int y = this.height / 4 + 48;
                addRenderableWidget(new ActionButton(
                        x, y, 200, 20,
                        Component.translatable("gui.generalfeedback.feedback"),
                        button -> FeedbackUtils.openFeedbackScreenOf("default")
                ));
            }
        }
    }
}
