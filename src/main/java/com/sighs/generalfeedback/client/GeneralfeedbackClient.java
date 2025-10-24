package com.sighs.generalfeedback.client;

import com.sighs.generalfeedback.compat.KubeJSCompat;
import com.sighs.generalfeedback.loader.EntryCache;
import com.sighs.generalfeedback.loader.ReloadEvent;
import net.fabricmc.api.ClientModInitializer;

public class GeneralfeedbackClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        KubeJSCompat.init();
        EntryCache.loadAllRule();
        ReloadEvent.register();
    }
}
