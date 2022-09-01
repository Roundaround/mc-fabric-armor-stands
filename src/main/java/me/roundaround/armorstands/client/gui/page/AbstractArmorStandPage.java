package me.roundaround.armorstands.client.gui.page;

import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.text.Text;

public abstract class AbstractArmorStandPage extends DrawableHelper implements ArmorStandPage {
  protected final MinecraftClient client;
  protected final ArmorStandScreen screen;
  protected final Text title;
  protected final int textureU;

  protected AbstractArmorStandPage(MinecraftClient client, ArmorStandScreen screen, Text title, int textureU) {
    this.client = client;
    this.screen = screen;
    this.title = title;
    this.textureU = textureU;
  }

  @Override
  public Text getTitle() {
    return title;
  }

  @Override
  public int getTextureU() {
    return textureU;
  }
}
