package com.sighs.generalfeedback.init;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class SubmitEvent extends Event {
    private Entry entry;
    private Form form;

    public SubmitEvent(Entry entry, Form form) {
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
}
