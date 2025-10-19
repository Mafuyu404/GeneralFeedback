package com.sighs.generalfeedback.compat.kubejs;

import com.sighs.generalfeedback.init.Entry;
import com.sighs.generalfeedback.init.Form;
import dev.latvian.mods.kubejs.client.ClientEventJS;
import dev.latvian.mods.rhino.util.HideFromJS;

public class SubmitEventJS extends ClientEventJS {
    @HideFromJS
    private Entry entry;
    @HideFromJS
    private Form form;
    @HideFromJS
    private boolean isCanceled = false;

    public SubmitEventJS(Entry entry, Form form) {
        this.entry = entry;
        this.form = form;
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean cancelled) {
        isCanceled = cancelled;
    }
}
