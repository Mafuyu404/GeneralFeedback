package com.sighs.generalfeedback;

import com.mojang.logging.LogUtils;
import com.sighs.generalfeedback.loader.EntryCache;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;

public class Generalfeedback implements ModInitializer {
    public static final String MODID = "generalfeedback";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        ModConfig.init();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            EntryCache.loadAllRule();
        });
    }
}
