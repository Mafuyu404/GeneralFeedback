package com.sighs.generalfeedback;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<Boolean> PAUSE_FEEDBACK_BUTTON;
    public static final ModConfigSpec.ConfigValue<Boolean> INVENTORY_FEEDBACK_BUTTON;
    public static final ModConfigSpec.ConfigValue<Boolean> DEATH_FEEDBACK_BUTTON;

    static {
        BUILDER.push("config");

        PAUSE_FEEDBACK_BUTTON = BUILDER
                .define("displayFeedbackButtonOnPauseScreen", true);
        INVENTORY_FEEDBACK_BUTTON = BUILDER
                .define("displayFeedbackButtonOnInventoryScreen", true);
        DEATH_FEEDBACK_BUTTON = BUILDER
                .define("displayFeedbackButtonOnDeathScreen", true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
