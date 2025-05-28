package me.roundaround.armorstands.client;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.roundalib.config.ConfigPath;
import me.roundaround.armorstands.roundalib.config.manage.ModConfigImpl;
import me.roundaround.armorstands.roundalib.config.manage.store.GameScopedFileStore;
import me.roundaround.armorstands.roundalib.config.option.BooleanConfigOption;
import me.roundaround.armorstands.roundalib.config.option.IntConfigOption;
import net.minecraft.client.resource.language.I18n;

public class ClientSideConfig extends ModConfigImpl implements GameScopedFileStore {
  private static ClientSideConfig instance = null;

  public static ClientSideConfig getInstance() {
    if (instance == null) {
      instance = new ClientSideConfig();
    }
    return instance;
  }

  public BooleanConfigOption requireSneakingToEdit;
  public IntConfigOption nameRenderDistance;
  public BooleanConfigOption directOnlyNameRender;

  private ClientSideConfig() {
    super(ArmorStandsMod.MOD_ID, "client");
  }

  @Override
  protected void registerOptions() {
    this.requireSneakingToEdit = this.buildRegistration(BooleanConfigOption.yesNoBuilder(ConfigPath.of(
            "requireSneakingToEdit"))
        .setDefaultValue(false)
        .setComment("Require sneaking in order to open the mod's edit GUI.")
        .build()).singlePlayerOnly().commit();
    this.nameRenderDistance = this.buildRegistration(IntConfigOption.sliderBuilder(ConfigPath.of("nameRenderDistance"))
        .setDefaultValue(0)
        .setMinValue(0)
        .setMaxValue(64)
        .setStep(4)
        .setToStringFunction((val) -> {
          if (val == 0) {
            return I18n.translate("armorstands.nameRenderDistance.value.default");
          }
          return I18n.translate("armorstands.nameRenderDistance.value", val);
        })
        .setComment("How far away armor stand names should be visible.", "Set to 0 to fall back to default.")
        .build()).clientOnly().commit();
    this.directOnlyNameRender = this.buildRegistration(BooleanConfigOption.yesNoBuilder(ConfigPath.of(
            "directOnlyNameRender"))
        .setDefaultValue(true)
        .setComment("Only render armor stand names when targeting them.")
        .build()).clientOnly().commit();
  }

  public boolean isApplicable() {
    return this.isReady() && (this.isActive(this.requireSneakingToEdit) || this.isActive(this.directOnlyNameRender));
  }
}
