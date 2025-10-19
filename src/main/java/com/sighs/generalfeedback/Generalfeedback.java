package com.sighs.generalfeedback;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(Generalfeedback.MODID)
public class Generalfeedback {
    public static final String MODID = "generalfeedback";

    public static final Logger LOGGER = LogUtils.getLogger();

    public Generalfeedback() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
