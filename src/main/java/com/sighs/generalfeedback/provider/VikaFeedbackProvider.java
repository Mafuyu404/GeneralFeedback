package com.sighs.generalfeedback.provider;

import com.sighs.generalfeedback.api.FeedbackProvider;
import com.sighs.generalfeedback.init.Entry;
import com.sighs.generalfeedback.init.Form;
import com.sighs.generalfeedback.utils.FeedbackUtils;
import com.sighs.generalfeedback.utils.HttpUtil;

import java.util.HashMap;
import java.util.Map;

public class VikaFeedbackProvider implements FeedbackProvider {

    @Override
    public boolean supports(Entry entry) {
        return entry.url.contains("api.vika.cn");
    }

    @Override
    public void sendFeedback(Entry entry, Form form) {
        FeedbackUtils.showSendingToast();

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + entry.token);
        headers.put("Content-Type", "application/json");

        String jsonBody = "{"
                + "\"records\": [{"
                + "\"fields\": {"
                + "\"意见反馈\": \"" + FeedbackUtils.escapeJson(form.feedback) + "\","
                + "\"体验评分\": " + form.mark + ","
                + "\"落款\": \"" + FeedbackUtils.escapeJson(form.contact) + "\""
                + "}"
                + "}],"
                + "\"fieldKey\": \"name\""
                + "}";

        HttpUtil.fetch(entry.url, "POST", headers, jsonBody, null, 5000,
                response -> FeedbackUtils.handleSuccess(response, entry.id),
                FeedbackUtils::handleFailure
        );
    }
}