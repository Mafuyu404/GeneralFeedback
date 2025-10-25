package com.sighs.generalfeedback.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sighs.generalfeedback.init.Entry;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EntryLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("GeneralFeedback");

    public static List<Entry> loadAll() {
        return new ArrayList<>(loadFromDir(CONFIG_DIR));
    }

    private static List<Entry> loadFromDir(Path path) {
        List<Entry> allRule = new ArrayList<>();


        // 确保目录存在
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return allRule;
        }

        // 遍历所有JSON文件
        try (var stream = Files.newDirectoryStream(path, "*.json")) {
            for (Path file : stream) {
                allRule.add(loadRecipesFromFile(file));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return allRule;
    }

    private static Entry loadRecipesFromFile(Path file) {
        try (Reader reader = Files.newBufferedReader(file)) {
            return GSON.fromJson(reader, new TypeToken<Entry>() {
            }.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return new Entry();
        }
    }
}
