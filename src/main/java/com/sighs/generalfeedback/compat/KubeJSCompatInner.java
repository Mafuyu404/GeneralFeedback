package com.sighs.generalfeedback.compat;

import com.sighs.generalfeedback.compat.kubejs.FeedbackEvents;
import com.sighs.generalfeedback.compat.kubejs.SubmitEventJS;
import com.sighs.generalfeedback.event.SubmitEvent;

public class KubeJSCompatInner {
    public static SubmitEvent submit(SubmitEvent event) {
        SubmitEventJS eventJS = new SubmitEventJS(event.getEntry(), event.getForm());
        FeedbackEvents.SUBMIT_EVENT.post(eventJS);
        SubmitEvent _event = new SubmitEvent(eventJS.getEntry(), eventJS.getForm());
        _event.setCanceled(eventJS.isCanceled());
        return _event;
    }
}
