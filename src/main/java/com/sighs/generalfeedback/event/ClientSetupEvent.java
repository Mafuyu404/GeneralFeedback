package com.sighs.generalfeedback.event;

import com.sighs.generalfeedback.Generalfeedback;
import com.sighs.generalfeedback.compat.KubeJSCompat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = Generalfeedback.MODID)
public class ClientSetupEvent {
    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(KubeJSCompat::init);
    }
}
