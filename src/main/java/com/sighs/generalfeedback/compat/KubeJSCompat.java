package com.sighs.generalfeedback.compat;

import com.sighs.generalfeedback.init.SubmitEvent;
import net.minecraftforge.fml.ModList;

public class KubeJSCompat {
    private static final String MOD_ID = "kubejs";
    private static boolean INSTALLED = false;

    public static void init() {
        INSTALLED = ModList.get().isLoaded(MOD_ID);
    }

    public static SubmitEvent submit(SubmitEvent event) {
        if (INSTALLED) {
            return KubeJSCompatInner.submit(event);
        }
        return event;
    }
}
