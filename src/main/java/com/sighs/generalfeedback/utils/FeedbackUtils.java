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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class FeedbackUtils {
    public static HashMap<String, String> cache = new HashMap<>();

    public static void post(Entry entry, Form form) {
        ItemIconToast.show(Component.translatable("toast.generalfeedback.sending.title"), Component.translatable("toast.generalfeedback.sending.desc"), new ItemStack(Items.GUNPOWDER));

        try {
            HttpURLConnection connection = getConnection(entry, form);

            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {

                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    Generalfeedback.LOGGER.warn("Response:" + response);

                    // 发送成功！
                    Minecraft.getInstance().getToasts().clear();
                    ItemIconToast.show(Component.translatable("toast.generalfeedback.send_success.title"), Component.translatable("toast.generalfeedback.send_success.desc"), new ItemStack(Items.GLOWSTONE_DUST));
                    cache.remove(entry.id);
                }
            }

            Generalfeedback.LOGGER.warn(responseCode + ":" + entry.url);
            connection.disconnect();
        } catch (Exception e) {
            Generalfeedback.LOGGER.warn(String.valueOf(e));
            ItemIconToast.show(Component.translatable("toast.generalfeedback.send_fail.title"), Component.translatable("toast.generalfeedback.send_fail.desc"), new ItemStack(Items.REDSTONE));
        }
    }

    private static HttpURLConnection getConnection(Entry entry, Form form) throws IOException {
        URL url = new URL(entry.url);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");

        connection.setRequestProperty("Authorization", "Bearer " + entry.token);
        connection.setRequestProperty("Content-Type", "application/json");

        connection.setDoOutput(true);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

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

        try(OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connection;
    }

    public static void openFeedbackScreenOf(String id) {
        if (EntryCache.UnitMapCache.containsKey(id)) {
            Minecraft.getInstance().setScreen(new FeedbackScreen(EntryCache.UnitMapCache.get(id)));
        }
    }
}
