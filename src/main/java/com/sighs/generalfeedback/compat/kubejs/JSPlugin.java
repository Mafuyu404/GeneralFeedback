package com.sighs.generalfeedback.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;

public class JSPlugin extends KubeJSPlugin {

    public void registerEvents() {
        FeedbackEvents.GROUP.register();
    }

    @Override
    public void registerBindings(BindingsEvent event) {

    }

    @Override
    public void registerTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {

    }
}
