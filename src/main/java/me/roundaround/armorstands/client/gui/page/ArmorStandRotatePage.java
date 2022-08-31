package me.roundaround.armorstands.client.gui.page;

import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ArmorStandRotatePage extends AbstractArmorStandPage {
  public ArmorStandRotatePage(MinecraftClient client, ArmorStandScreen screen) {
    super(client, screen, Text.translatable("armorstands.page.rotate"), 2);
  }
}
