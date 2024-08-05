package me.roundaround.armorstands.server.config;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.manage.ModConfigImpl;
import me.roundaround.roundalib.config.manage.store.WorldScopedFileStore;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.config.option.StringListConfigOption;

public class ServerSideConfig extends ModConfigImpl implements WorldScopedFileStore {
  private static ServerSideConfig instance = null;

  public static ServerSideConfig getInstance() {
    if (instance == null) {
      instance = new ServerSideConfig();
    }
    return instance;
  }

  public BooleanConfigOption enforcePermissions;
  public BooleanConfigOption opsHavePermissions;
  public StringListConfigOption allowedUsers;

  private ServerSideConfig() {
    super(ArmorStandsMod.MOD_ID, "server");
  }

  @Override
  protected void registerOptions() {
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
}
