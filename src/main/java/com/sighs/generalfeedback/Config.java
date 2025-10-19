package com.sighs.generalfeedback;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.ConfigValue<Boolean> PAUSE_FEEDBACK_BUTTON;
    public static final ForgeConfigSpec.ConfigValue<Boolean> INVENTORY_FEEDBACK_BUTTON;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DEATH_FEEDBACK_BUTTON;

    static {
        BUILDER.push("Render Radius");

        PAUSE_FEEDBACK_BUTTON = BUILDER
                .comment("displayFeedbackButtonOnInventoryScreen")
                .define("enable", true);
        INVENTORY_FEEDBACK_BUTTON = BUILDER
                .comment("displayFeedbackButtonOnPauseScreen")
                .define("enable", true);
        DEATH_FEEDBACK_BUTTON = BUILDER
                .comment("displayFeedbackButtonOnDeathScreen")
                .define("enable", true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
