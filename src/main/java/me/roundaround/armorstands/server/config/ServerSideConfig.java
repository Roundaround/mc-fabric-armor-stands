package me.roundaround.armorstands.server.config;

import com.mojang.authlib.GameProfile;
import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.mixin.AbstractPropertiesHandlerAccessor;
import me.roundaround.armorstands.mixin.ServerConfigEntryAccessor;
import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.manage.ModConfigImpl;
import me.roundaround.roundalib.config.manage.store.WorldScopedFileStore;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.config.option.StringListConfigOption;
import me.roundaround.roundalib.nightconfig.core.Config;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerSideConfig extends ModConfigImpl implements WorldScopedFileStore, AutoCloseable {
  private static ServerSideConfig instance = null;

  public static ServerSideConfig getInstance() {
    if (instance == null || instance.server == null) {
      throw new RuntimeException(
          "Armor Stands mod server-side config not initialized properly! This is a bug with the mod. Please report it" +
              " to the mod's author.");
    }
    return instance;
  }

  public static ServerSideConfig create(MinecraftDedicatedServer server) {
    if (instance != null) {
      instance.close();
    }
    instance = new ServerSideConfig(server);
    return instance;
  }

  private static final String LEGACY_PROP_KEY = "enforce-armor-stand-permissions";
  private static final String LEGACY_USERS_FILE = "armorstandsusers.json";

  public BooleanConfigOption requireSneakingToEdit;
  public BooleanConfigOption enforcePermissions;
  public BooleanConfigOption opsHavePermissions;
  public StringListConfigOption allowedUsers;

  private MinecraftDedicatedServer server;

  private ServerSideConfig(MinecraftDedicatedServer server) {
    super(ArmorStandsMod.MOD_ID, "server");
    this.server = server;
  }

  @Override
  public void initializeStore() {
    super.initializeStore();
  }

  @Override
  protected void registerOptions() {
    this.requireSneakingToEdit = this.buildRegistration(
        BooleanConfigOption.yesNoBuilder(ConfigPath.of("requireSneakingToEdit"))
            .setDefaultValue(false)
            .setComment("Require users to sneak to use the mod.")
            .build()).serverOnly().commit();
    this.enforcePermissions = this.buildRegistration(
        BooleanConfigOption.yesNoBuilder(ConfigPath.of("enforcePermissions"))
            .setDefaultValue(true)
            .setComment("Only allow permitted users to use the mod.")
            .build()).serverOnly().commit();
    this.opsHavePermissions = this.buildRegistration(
        BooleanConfigOption.yesNoBuilder(ConfigPath.of("opsHavePermissions"))
            .setDefaultValue(true)
            .setComment("Whether server OPs are always permitted to use the mod.")
            .build()).serverOnly().commit();
    this.allowedUsers = this.buildRegistration(StringListConfigOption.builder(ConfigPath.of("allowedUsers"))
        .setComment("List of users by their UUID permitted to use the mod.")
        .build()).serverOnly().commit();
  }

  @Override
  public boolean performConfigUpdate(int versionSnapshot, Config inMemoryConfigSnapshot) {
    return this.performLegacyMigration(inMemoryConfigSnapshot);
  }

  private boolean performLegacyMigration(Config config) {
    AtomicBoolean modified = new AtomicBoolean(false);

    String enforcePermissionsPath = this.enforcePermissions.getPath().toString();
    if (!config.contains(enforcePermissionsPath)) {
      getLegacyPermissionsEnforced().ifPresent((enforced) -> {
        config.add(enforcePermissionsPath, enforced);
        modified.set(true);
      });
    }

    String allowedUsersPath = this.allowedUsers.getPath().toString();
    if (config.contains(allowedUsersPath)) {
      if (Files.isReadable(Paths.get(LEGACY_USERS_FILE))) {
        ArmorStandsMod.LOGGER.warn(
            "Users listed in config file, but legacy {} file still exists. Consider removing it.", LEGACY_USERS_FILE);
      }
    } else {
      config.set(allowedUsersPath, getLegacyAllowlist());
      modified.set(true);
    }

    return modified.get();
  }

  @Override
  public void close() {
    this.server = null;
  }

  private Optional<Boolean> getLegacyPermissionsEnforced() {
    if (this.server == null) {
      return Optional.empty();
    }

    ServerPropertiesHandler propertiesHandler = this.server.getProperties();
    Properties properties = ((AbstractPropertiesHandlerAccessor) propertiesHandler).getProperties();
    String legacyValueRaw = properties.getProperty(LEGACY_PROP_KEY);

    // Removing it from the Properties instance should be enough - the game will save the in-memory map
    // back to file automatically (at some point).
    properties.remove(LEGACY_PROP_KEY);

    if (legacyValueRaw == null || legacyValueRaw.isBlank()) {
      return Optional.empty();
    }

    return Optional.of(Boolean.parseBoolean(legacyValueRaw));
  }

  private List<String> getLegacyAllowlist() {
    Path path = Paths.get(LEGACY_USERS_FILE);

    if (!Files.isReadable(path)) {
      return List.of();
    }

    Whitelist legacyAllowlist = new Whitelist(path.toFile());

    try {
      legacyAllowlist.load();
    } catch (IOException e) {
      ArmorStandsMod.LOGGER.warn(
          "Failed to parse legacy {} file during automatic config migration. Skipping.", LEGACY_USERS_FILE, e);
      return List.of();
    }

    try {
      Files.delete(path);
    } catch (IOException e) {
      ArmorStandsMod.LOGGER.warn(
          "Failed to remove legacy {} file during automatic config migration.", LEGACY_USERS_FILE, e);
    }

    return legacyAllowlist.values().stream().map(this::extractUuid).toList();
  }

  @SuppressWarnings("unchecked")
  private String extractUuid(WhitelistEntry entry) {
    return ((ServerConfigEntryAccessor<GameProfile>) entry).invokeGetKey().getId().toString();
  }
}
