package com.sighs.generalfeedback.compat;

import com.sighs.generalfeedback.compat.kubejs.FeedbackEvents;
import com.sighs.generalfeedback.compat.kubejs.SubmitEventJS;
import com.sighs.generalfeedback.init.Entry;
import com.sighs.generalfeedback.init.Form;
import com.sighs.generalfeedback.init.SubmitEvent;
import dev.latvian.mods.kubejs.event.EventJS;

public class KubeJSCompatInner {
    public static SubmitEvent submit(SubmitEvent event) {
        SubmitEventJS eventJS = new SubmitEventJS(event.getEntry(), event.getForm());
        FeedbackEvents.SUBMIT_EVENT.post(eventJS);
        SubmitEvent _event = new SubmitEvent(eventJS.getEntry(), eventJS.getForm());
        _event.setCanceled(eventJS.isCanceled());
        return _event;
    }
}
