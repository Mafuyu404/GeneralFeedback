package com.sighs.generalfeedback.compat;

import com.sighs.generalfeedback.event.SubmitEvent;
import net.fabricmc.loader.api.FabricLoader;

public class KubeJSCompat {
    private static final String MOD_ID = "kubejs";
    private static boolean INSTALLED = false;

    public static void init() {
        INSTALLED = FabricLoader.getInstance().isModLoaded(MOD_ID);
    }

    public static SubmitEvent submit(SubmitEvent event) {
        if (INSTALLED) {
            return KubeJSCompatInner.submit(event);
        }
        return event;
    }
}
