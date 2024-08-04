package me.roundaround.armorstands.server.config;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.manage.ModConfigImpl;
import me.roundaround.roundalib.config.manage.store.WorldScopedFileStore;
import me.roundaround.roundalib.config.option.BooleanConfigOption;

public class ServerSideConfig extends ModConfigImpl implements WorldScopedFileStore {
  private static ServerSideConfig instance = null;

  public static ServerSideConfig getInstance() {
    if (instance == null) {
      instance = new ServerSideConfig();
    }
    return instance;
  }

  public BooleanConfigOption enforcePermissions;

  private ServerSideConfig() {
    super(ArmorStandsMod.MOD_ID, "server");
  }

  @Override
  protected void registerOptions() {
    this.enforcePermissions = this.buildRegistration(
            BooleanConfigOption.yesNoBuilder(ConfigPath.of("enforcePermissions")).setDefaultValue(true).build())
        .serverOnly()
        .commit();
  }
}
