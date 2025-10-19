package com.sighs.generalfeedback.utils;

import com.sighs.generalfeedback.Generalfeedback;
import com.sighs.generalfeedback.init.Entry;
import com.sighs.generalfeedback.init.Form;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static it.unimi.dsi.fastutil.io.TextIO.BUFFER_SIZE;

public class HttpUtil {
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static boolean fetch(
            String url,
            String method,
            Map<String, String> headers,
            String jsonBody,
            Map<String, String> formData,
            int timeoutMillis,
            Consumer<String> callback,
            Consumer<String> error) {

        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
            HttpURLConnection connection = null;
            try {
                URL requestUrl = new URL(url);
                connection = (HttpURLConnection) requestUrl.openConnection();
                connection.setRequestMethod(method);
                connection.setConnectTimeout(timeoutMillis);
                connection.setReadTimeout(timeoutMillis);

                if (headers != null) {
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        connection.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }

                if (requiresBody(method)) {
                    connection.setDoOutput(true);
                    String contentType;
                    String bodyContent = "";

                    if (jsonBody != null) {
                        contentType = "application/json; charset=utf-8";
                        bodyContent = jsonBody;
                    } else if (formData != null) {
                        // 介是表单
                        contentType = "application/x-www-form-urlencoded; charset=UTF-8";
                        bodyContent = buildFormData(formData);
                    } else {
                        contentType = "text/plain; charset=UTF-8";
                    }

                    connection.setRequestProperty("Content-Type", contentType);

                    try (OutputStream os = connection.getOutputStream()) {
                        os.write(bodyContent.getBytes(StandardCharsets.UTF_8));
                        os.flush();
                    }
                }

                int responseCode = connection.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    // UTF-8，去掉会绝赞乱码
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        callback.accept(response.toString());
                        return true;
                    }
                } else {
                    error.accept("Unexpected HTTP response: " + responseCode);
                    Generalfeedback.LOGGER.warn("Unexpected HTTP response: " + responseCode);
                }
            } catch (Exception e) {
                error.accept("HTTP request failed: " + e.getMessage());
                Generalfeedback.LOGGER.warn("HTTP request failed: " + e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return false;
        }, executor);

        return true;
    }

    private static boolean requiresBody(String method) {
        return "POST".equalsIgnoreCase(method) ||
                "PUT".equalsIgnoreCase(method) ||
                "PATCH".equalsIgnoreCase(method);
    }

    private static String buildFormData(Map<String, String> formData) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            // 对键和值进行 URL 编码
            sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    public static void ccc(Entry entry, Form form) {
        try {
            URL url = new URL(entry.url);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 3. 设置请求方法
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Authorization", "Bearer " + entry.token);
            connection.setRequestProperty("Content-Type", "application/json");

            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // 6. 准备JSON请求体
            String jsonBody = "{"
                    + "\"records\": [{"
                    + "\"fields\": {"
                    + "\"意见反馈\": \"" + form.feedback.replace("\n", "\\n") + "\","
                    + "\"体验评分\": 4,"
                    + "\"落款\": \"无\""
                    + "}"
                    + "}],"
                    + "\"fieldKey\": \"name\""
                    + "}";

            // 7. 发送请求数据
            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 8. 获取响应状态码
            int responseCode = connection.getResponseCode();
            System.out.println("HTTP 响应码: " + responseCode);

            // 9. 读取响应内容
            if (responseCode >= 200 && responseCode < 300) {
                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {

                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("响应内容: " + response);
                }
            } else {
                System.err.println("请求失败，HTTP 错误码: " + responseCode);
            }

            // 10. 断开连接
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getString(Form form) {
        String jsonBody = """
                {
                  "records": [
                    {
                      "fields": {
                        "意见反馈": "feedback",
                        "体验评分": mark
                        "意见反馈": "contact",
                      }
                    }
                  ],
                  "fieldKey": "name"
                }""";

        jsonBody = jsonBody.replaceAll("feedback", form.feedback);
        jsonBody = jsonBody.replaceAll("mark", String.valueOf(form.mark));
        jsonBody = jsonBody.replaceAll("contact", form.contact);
        return jsonBody;
    }
}
