package com.sighs.generalfeedback.provider;

import com.sighs.generalfeedback.api.FeedbackProvider;
import com.sighs.generalfeedback.init.Entry;
import com.sighs.generalfeedback.init.Form;
import com.sighs.generalfeedback.utils.FeedbackUtils;
import com.sighs.generalfeedback.utils.HttpUtil;

import java.util.HashMap;
import java.util.Map;

public class GitHubFeedbackProvider implements FeedbackProvider {
    
    @Override
    public boolean supports(Entry entry) {
        return entry.url.contains("api.github.com");
    }
    
    @Override
    public void sendFeedback(Entry entry, Form form) {
        FeedbackUtils.showSendingToast();

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + entry.token);
        headers.put("Content-Type", "application/json");

        String title = FeedbackUtils.generateIssueTitle(form);
        String body = FeedbackUtils.generateIssueBody(form);

        String jsonBody = "{"
                + "\"title\": \"" + FeedbackUtils.escapeJson(title) + "\","
                + "\"body\": \"" + FeedbackUtils.escapeJson(body) + "\""
                + "}";

        HttpUtil.fetch(entry.url, "POST", headers, jsonBody, null, 5000,
                response -> FeedbackUtils.handleSuccess(response, entry.id),
                FeedbackUtils::handleFailure
        );
    }
}