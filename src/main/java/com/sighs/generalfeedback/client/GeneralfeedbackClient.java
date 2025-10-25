package com.sighs.generalfeedback.client;

import com.sighs.generalfeedback.loader.EntryCache;
import com.sighs.generalfeedback.loader.ReloadEvent;
import net.fabricmc.api.ClientModInitializer;

public class GeneralfeedbackClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntryCache.loadAllRule();
        ReloadEvent.register();
    }
}
