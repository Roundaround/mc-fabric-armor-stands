package me.roundaround.armorstands.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.roundaround.armorstands.util.Pose;
import me.roundaround.armorstands.util.SavedPose;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.JsonHelper;

public class PoseStorage {
  private static final File FILE = FabricLoader.getInstance().getGameDir().resolve("armorstandsposes.json").toFile();
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  private static HashMap<UUID, SavedPose> map = new HashMap<>();

  public static void add(String name, ArmorStandEntity armorStand) {
    add(name, new Pose(armorStand));
  }

  public static void add(String name, Pose pose) {
    UUID uuid = UUID.randomUUID();
    try {
      map.put(uuid, new SavedPose(name, pose));
      save();
    } catch (IOException e) {
      map.remove(uuid);
      e.printStackTrace();
    }
  }

  public static void rename(UUID uuid, String name) {
    try {
      SavedPose savedPose = map.get(uuid);
      if (savedPose == null) {
        return;
      }
      map.put(uuid, new SavedPose(name, savedPose.toPose()));
      save();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void remove(UUID uuid) {
    try {
      map.remove(uuid);
      save();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void reload() {
    try {
      load();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Map<UUID, SavedPose> getPoses() {
    return Map.copyOf(map);
  }

  private static void save() throws IOException {
    JsonArray jsonArray = new JsonArray();
    map.values().stream().map(SavedPose::toJson).forEach(jsonArray::add);
    try (BufferedWriter bufferedWriter = Files.newWriter(FILE, StandardCharsets.UTF_8);) {
      GSON.toJson(jsonArray, bufferedWriter);
    }
  }

  private static void load() throws IOException {
    if (!FILE.exists()) {
      return;
    }
    try (BufferedReader bufferedReader = Files.newReader(FILE, StandardCharsets.UTF_8)) {
      map.clear();

      JsonArray jsonArray = GSON.fromJson(bufferedReader, JsonArray.class);
      for (JsonElement jsonElement : jsonArray) {
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entry");
        map.put(UUID.randomUUID(), SavedPose.fromJson(jsonObject));
      }
    }
  }
}
