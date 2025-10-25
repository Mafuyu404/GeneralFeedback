package com.sighs.generalfeedback.utils;

import com.sighs.generalfeedback.Generalfeedback;
import com.sighs.generalfeedback.api.FeedbackProvider;
import com.sighs.generalfeedback.client.FeedbackScreen;
import com.sighs.generalfeedback.client.ItemIconToast;
import com.sighs.generalfeedback.init.Entry;
import com.sighs.generalfeedback.init.Form;
import com.sighs.generalfeedback.loader.EntryCache;
import com.sighs.generalfeedback.provider.GitHubFeedbackProvider;
import com.sighs.generalfeedback.provider.GiteeFeedbackProvider;
import com.sighs.generalfeedback.provider.VikaFeedbackProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeedbackUtils {
    public static HashMap<String, String> cache = new HashMap<>();

    private static final List<FeedbackProvider> providers = new ArrayList<>();

    // 这里注册所有 provider
    static {
        providers.add(new VikaFeedbackProvider());
        providers.add(new GitHubFeedbackProvider());
        providers.add(new GiteeFeedbackProvider());
    }

    public static void post(Entry entry, Form form) {
        for (FeedbackProvider provider : providers) {
            if (provider.supports(entry)) {
                provider.sendFeedback(entry, form);
                return;
            }
        }
        handleFailure("Unsupported feedback provider for URL: " + entry.url);
    }

    public static void showSendingToast() {
        ItemIconToast.show(Component.translatable("toast.generalfeedback.sending.title"),
                Component.translatable("toast.generalfeedback.sending.desc"),
                new ItemStack(Items.GUNPOWDER));
    }

    public static void handleSuccess(String response, String entryId) {
        sendSuccess(response);
        cache.remove(entryId);
    }

    public static void handleFailure(String error) {
        sendFail(error);
    }

    // 转义 JSON 特殊字符
    public static String escapeJson(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // 将字符串列表转换为 JSON 数组
    public static String toJsonArray(List<String> items) {
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

    public static String generateIssueTitle(Form form) {
        return (form.mark != 0 ? ("(" + form.mark + "★) ") : "")
                + Minecraft.getInstance().player.getDisplayName().getString() + ": "
                + form.feedback.substring(0, Math.min(form.feedback.length(), 30))
                + (form.feedback.length() > 30 ? "......" : "");
    }

    public static String generateIssueBody(Form form) {
        return form.feedback + "\n\nFrom:\n" + form.contact;
    }

    private static void sendSuccess(String response) {
        Minecraft.getInstance().execute(() -> {
            Generalfeedback.LOGGER.warn("Response:{}", response);
            // 发送成功！
            Minecraft.getInstance().getToastManager().clear();
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
