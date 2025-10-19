package com.sighs.generalfeedback.loader;

import com.sighs.generalfeedback.Generalfeedback;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Generalfeedback.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LoadEvent {
    @SubscribeEvent
    public static void onLoad(FMLClientSetupEvent event) {
        EntryCache.loadAllRule();
    }
}
