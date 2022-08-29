package me.roundaround.armorstands.client.gui.page;

import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.client.gui.widget.MiniButtonWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

public class ArmorStandMovePage extends AbstractArmorStandPage {
  private static final int BUTTON_WIDTH = 16;
  private static final int BUTTON_HEIGHT = 16;
  private static final int SCREEN_EDGE_PAD = 4;
  private static final int BETWEEN_PAD = 2;

  public ArmorStandMovePage(MinecraftClient client, ArmorStandScreen screen) {
    super(client, screen);
  }

  @Override
  public void init() {
    addRowOfButtons(Text.literal("Up (+Y)"), Direction.UP, 5);
    addRowOfButtons(Text.literal("Down (-Y)"), Direction.DOWN, 4);
    addRowOfButtons(Text.literal("South (+Z)"), Direction.SOUTH, 3);
    addRowOfButtons(Text.literal("North (-Z)"), Direction.NORTH, 2);
    addRowOfButtons(Text.literal("East (+X)"), Direction.EAST, 1);
    addRowOfButtons(Text.literal("West (-X)"), Direction.WEST, 0);
  }

  private void addRowOfButtons(Text label, Direction direction, int index) {
    int refX = screen.width - SCREEN_EDGE_PAD - BUTTON_WIDTH;
    int refY = screen.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT - index * (BETWEEN_PAD + BUTTON_HEIGHT);

    screen.addDrawable(LabelWidget.builder(
        label,
        refX - 2 * (BETWEEN_PAD + BUTTON_WIDTH) - 4,
        refY + BUTTON_HEIGHT / 2)
        .alignedRight()
        .alignedMiddle()
        .build());
    screen.addDrawableChild(new MiniButtonWidget(
        refX - 2 * (BETWEEN_PAD + BUTTON_WIDTH),
        refY,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("1"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(screen.getArmorStand(), direction, 1);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        refX - 1 * (BETWEEN_PAD + BUTTON_WIDTH),
        refY,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("3"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(screen.getArmorStand(), direction, 3);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        refX,
        refY,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("8"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(screen.getArmorStand(), direction, 8);
        }));
  }
}
