package me.roundaround.armorstands.client;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.manage.ModConfigImpl;
import me.roundaround.roundalib.config.manage.store.GameScopedFileStore;
import me.roundaround.roundalib.config.option.BooleanConfigOption;

public class ClientSideConfig extends ModConfigImpl implements GameScopedFileStore {
  private static ClientSideConfig instance = null;

  public static ClientSideConfig getInstance() {
    if (instance == null) {
      instance = new ClientSideConfig();
    }
    return instance;
  }

  public BooleanConfigOption requireSneakingToEdit;

  private ClientSideConfig() {
    super(ArmorStandsMod.MOD_ID, "client");
  }

  @Override
  protected void registerOptions() {
    this.requireSneakingToEdit = this.buildRegistration(
        BooleanConfigOption.yesNoBuilder(ConfigPath.of("requireSneakingToEdit"))
            .setDefaultValue(false)
            .setComment("Require sneaking in order to open the mod's edit GUI.")
            .build()).singlePlayerOnly().commit();
  }

  public boolean isApplicable() {
    return this.isReady() && this.isActive(this.requireSneakingToEdit);
  }
}
