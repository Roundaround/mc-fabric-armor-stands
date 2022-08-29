package me.roundaround.armorstands.client.gui.page;

import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public abstract class AbstractArmorStandPage extends DrawableHelper {
  protected final MinecraftClient client;
  protected final ArmorStandScreen screen;
  protected final Text title;

  protected AbstractArmorStandPage(MinecraftClient client, ArmorStandScreen screen, Text title) {
    this.client = client;
    this.screen = screen;
    this.title = title;
  }

  public Text getTitle() {
    return title;
  }

  public boolean usesSlots() {
    return false;
  }

  public void init() {
  }

  public void drawBackground(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
  }

  public void renderEntityOverlay(
      MatrixStack matrixStack,
      VertexConsumerProvider vertexConsumerProvider,
      int light) {
  }

  public void tick() {
  }
}
