package com.sighs.generalfeedback.loader;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;

public class ReloadEvent {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("reload")
                    .executes(context -> {
                        EntryCache.loadAllRule();
                        return 1;
                    }));
        });
    }
}
