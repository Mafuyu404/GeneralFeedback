package com.sighs.generalfeedback.utils;

import com.sighs.generalfeedback.Generalfeedback;
import com.sighs.generalfeedback.client.FeedbackScreen;
import com.sighs.generalfeedback.init.Entry;
import com.sighs.generalfeedback.init.Form;
import com.sighs.generalfeedback.client.ItemIconToast;
import com.sighs.generalfeedback.loader.EntryCache;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackUtils {
    public static HashMap<String, String> cache = new HashMap<>();

    public static void post(Entry entry, Form form) {
        if (entry.url.contains("api.vika.cn")) {
            addVikaRecord(entry, form);
        }
        if (entry.url.contains("api.github.com")) {
            createGitHubIssue(entry, form);
        }
    }

    public static void addVikaRecord(Entry entry, Form form) {
        ItemIconToast.show(Component.translatable("toast.generalfeedback.sending.title"),
                Component.translatable("toast.generalfeedback.sending.desc"),
                new ItemStack(Items.GUNPOWDER));

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + entry.token);
        headers.put("Content-Type", "application/json");

        String jsonBody = "{"
                + "\"records\": [{"
                + "\"fields\": {"
                + "\"意见反馈\": \"" + form.feedback.replace("\n", "\\n") + "\","
                + "\"体验评分\": " + form.mark + ","
                + "\"落款\": \"" + form.contact.replace("\n", "\\n") + "\""
                + "}"
                + "}],"
                + "\"fieldKey\": \"name\""
                + "}";

        HttpUtil.fetch(entry.url, "POST", headers, jsonBody, null, 5000,
                response -> {
                    sendSuccess(response);
                    cache.remove(entry.id);
                },
                FeedbackUtils::sendFail
        );
    }

    public static void createGitHubIssue(Entry entry, Form form) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + entry.token);
        headers.put("Content-Type", "application/json");

        String title = (form.mark != 0 ? ("(" + form.mark + "★) ") : "")
                + Minecraft.getInstance().player.getDisplayName().getString() + ": "
                + form.feedback.substring(0, Math.min(form.feedback.length(), 30))
                + (form.feedback.length() > 30 ? "......" : "");
        String body = form.feedback + "\n\nFrom:\n" + form.contact;

        String jsonBody = "{"
                + "\"title\": \"" + escapeJson(title) + "\","
                + "\"body\": \"" + escapeJson(body) + "\""
                + "}";

        // 发送请求
        HttpUtil.fetch(entry.url, "POST", headers, jsonBody, null, 5000,
                response -> {
                    sendSuccess(response);
                    cache.remove(entry.id);
                },
                FeedbackUtils::sendFail
        );
    }

    // 辅助方法：转义 JSON 特殊字符
    private static String escapeJson(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // 辅助方法：将字符串列表转换为 JSON 数组
    private String toJsonArray(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(escapeJson(items.get(i))).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }

    private static void sendSuccess(String response) {
        Minecraft.getInstance().execute(() -> {
            Generalfeedback.LOGGER.warn("Response:{}", response);
            // 发送成功！
            Minecraft.getInstance().getToasts().clear();
            ItemIconToast.show(
                    Component.translatable("toast.generalfeedback.send_success.title"),
                    Component.translatable("toast.generalfeedback.send_success.desc"),
                    new ItemStack(Items.GLOWSTONE_DUST)
            );
        });
    }
    private static void sendFail(String error) {
        Minecraft.getInstance().execute(() -> {
            Generalfeedback.LOGGER.warn(error);
            ItemIconToast.show(
                    Component.translatable("toast.generalfeedback.send_fail.title"),
                    Component.translatable("toast.generalfeedback.send_fail.desc"),
                    new ItemStack(Items.REDSTONE)
            );
        });
    }

    public static void openFeedbackScreenOf(String id) {
        if (EntryCache.UnitMapCache.containsKey(id)) {
            Minecraft.getInstance().setScreen(new FeedbackScreen(EntryCache.UnitMapCache.get(id)));
        }
    }
}
