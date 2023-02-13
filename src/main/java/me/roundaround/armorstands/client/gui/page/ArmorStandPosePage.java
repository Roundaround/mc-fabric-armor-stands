package me.roundaround.armorstands.client.gui.page;

import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import me.roundaround.armorstands.client.gui.widget.PoseListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ArmorStandPosePage extends AbstractArmorStandPage {
  private static final int SCREEN_EDGE_PAD = 4;

  public ArmorStandPosePage(MinecraftClient client, ArmorStandScreen screen) {
    super(client, screen, Text.translatable("armorstands.page.pose"), 4);
  }

  @Override
  public void postInit() {
    int refX = screen.width - SCREEN_EDGE_PAD;
    int refY = screen.height - SCREEN_EDGE_PAD;

    PoseListWidget list = new PoseListWidget(
        client,
        refX,
        refY,
        160,
        refY - 20);
    screen.addDrawableChild(list);
  }
}
