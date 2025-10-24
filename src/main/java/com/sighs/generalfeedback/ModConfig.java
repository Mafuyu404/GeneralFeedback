package com.sighs.generalfeedback;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = Generalfeedback.MODID)
public class ModConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public boolean pauseFeedbackButton = true;

    @ConfigEntry.Gui.Tooltip
    public boolean inventoryFeedbackButton = true;

    @ConfigEntry.Gui.Tooltip
    public boolean deathFeedbackButton = true;

    private static ModConfig instance;

    public static void init() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        instance = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    public static ModConfig getInstance() {
        return instance;
    }

    public static boolean getPauseFeedbackButton() {
        return getInstance().pauseFeedbackButton;
    }

    public static boolean getInventoryFeedbackButton() {
        return getInstance().inventoryFeedbackButton;
    }

    public static boolean getDeathFeedbackButton() {
        return getInstance().deathFeedbackButton;
    }
}
