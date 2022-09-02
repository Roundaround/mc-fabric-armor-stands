package me.roundaround.armorstands.client.gui.page;

import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import me.roundaround.armorstands.client.gui.widget.MiniButtonWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.PosePreset;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ArmorStandPosePage extends AbstractArmorStandPage {
  private static final int MINI_BUTTON_WIDTH = 16;
  private static final int MINI_BUTTON_HEIGHT = 16;
  private static final int SCREEN_EDGE_PAD = 4;
  private static final int BETWEEN_PAD = 2;

  public ArmorStandPosePage(MinecraftClient client, ArmorStandScreen screen) {
    super(client, screen, Text.translatable("armorstands.page.pose"), 4);
  }

  @Override
  public void postInit() {
    int refX = screen.width - SCREEN_EDGE_PAD - MINI_BUTTON_WIDTH;
    int refY = screen.height - SCREEN_EDGE_PAD - MINI_BUTTON_HEIGHT;

    screen.addDrawableChild(new MiniButtonWidget(
        refX - 3 * (BETWEEN_PAD + MINI_BUTTON_WIDTH),
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        Text.literal("A"),
        (button) -> {
          ClientNetworking.sendSetPosePacket(PosePreset.ATTENTION);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        refX - 2 * (BETWEEN_PAD + MINI_BUTTON_WIDTH),
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        Text.literal("B"),
        (button) -> {
          ClientNetworking.sendSetPosePacket(PosePreset.WALKING);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        refX - 1 * (BETWEEN_PAD + MINI_BUTTON_WIDTH),
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        Text.literal("C"),
        (button) -> {
          ClientNetworking.sendSetPosePacket(PosePreset.RUNNING);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        refX,
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        Text.literal("D"),
        (button) -> {
          ClientNetworking.sendSetPosePacket(PosePreset.SITTING);
        }));
  }
}
