package com.sighs.generalfeedback.provider;

import com.sighs.generalfeedback.api.FeedbackProvider;
import com.sighs.generalfeedback.init.Entry;
import com.sighs.generalfeedback.init.Form;
import com.sighs.generalfeedback.utils.FeedbackUtils;
import com.sighs.generalfeedback.utils.HttpUtil;

import java.util.HashMap;
import java.util.Map;

public class GiteeFeedbackProvider implements FeedbackProvider {
    
    @Override
    public boolean supports(Entry entry) {
        return entry.url.contains("gitee.com/api/v5");
    }
    
    @Override
    public void sendFeedback(Entry entry, Form form) {
        FeedbackUtils.showSendingToast();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String title = FeedbackUtils.generateIssueTitle(form);
        String body = FeedbackUtils.generateIssueBody(form);

        String apiUrl = entry.url;
        String repoFromUrl = null;
        final String prefix = "https://gitee.com/api/v5/repos/";
        if (apiUrl.startsWith(prefix)) {
            String rest = apiUrl.substring(prefix.length());
            String[] parts = rest.split("/");
            if (parts.length >= 3 && "issues".equals(parts[2])) {
                apiUrl = prefix + parts[0] + "/issues";
                repoFromUrl = parts[1];
            }
        }

        String jsonBody = "{"
                + "\"access_token\":\"" + entry.token + "\","
                + "\"title\":\"" + FeedbackUtils.escapeJson(title) + "\","
                + "\"body\":\"" + FeedbackUtils.escapeJson(body) + "\""
                + (repoFromUrl != null ? ",\"repo\":\"" + FeedbackUtils.escapeJson(repoFromUrl) + "\"" : "")
                + "}";

        HttpUtil.fetch(apiUrl, "POST", headers, jsonBody, null, 5000,
                response -> FeedbackUtils.handleSuccess(response, entry.id),
                FeedbackUtils::handleFailure
        );
    }
}