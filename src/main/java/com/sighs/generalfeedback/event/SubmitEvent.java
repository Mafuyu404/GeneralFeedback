package com.sighs.generalfeedback.event;

import com.sighs.generalfeedback.init.Entry;
import com.sighs.generalfeedback.init.Form;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class SubmitEvent {
    private Entry entry;
    private Form form;
    private boolean canceled = false;

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

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public static final Event<SubmitCallback> EVENT = EventFactory.createArrayBacked(SubmitCallback.class,
            (listeners) -> (event) -> {
                for (SubmitCallback listener : listeners) {
                    listener.onSubmit(event);
                }
            });

    @FunctionalInterface
    public interface SubmitCallback {
        void onSubmit(SubmitEvent event);
    }
}
