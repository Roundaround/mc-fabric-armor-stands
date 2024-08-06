package me.roundaround.armorstands.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.ClientSideConfig;
import me.roundaround.roundalib.client.gui.screen.ConfigScreen;

public class ModMenuImpl implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return (parent) -> {
      ClientSideConfig config = ClientSideConfig.getInstance();
      return config.isApplicable() ? new ConfigScreen(parent, ArmorStandsMod.MOD_ID, config) : null;
    };
  }
}
