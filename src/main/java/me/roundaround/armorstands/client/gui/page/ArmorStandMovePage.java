package me.roundaround.armorstands.client.gui.page;

import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.client.gui.widget.MiniButtonWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

public class ArmorStandMovePage extends AbstractArmorStandPage {
  private static final int MINI_BUTTON_WIDTH = 16;
  private static final int MINI_BUTTON_HEIGHT = 16;
  private static final int BUTTON_WIDTH = 40;
  private static final int BUTTON_HEIGHT = 16;
  private static final int SCREEN_EDGE_PAD = 4;
  private static final int BETWEEN_PAD = 2;

  public ArmorStandMovePage(MinecraftClient client, ArmorStandScreen screen) {
    super(client, screen, Text.translatable("armorstands.page.move"));
  }

  @Override
  public void init() {
    screen.addDrawable(LabelWidget.builder(
        Text.literal("Snap to:"),
        screen.width - SCREEN_EDGE_PAD - 2 - 3 * (BUTTON_WIDTH + BETWEEN_PAD),
        SCREEN_EDGE_PAD + BUTTON_HEIGHT / 2)
        .alignedRight()
        .alignedMiddle());
    screen.addDrawableChild(new MiniButtonWidget(
        screen.width - SCREEN_EDGE_PAD - 3 * BUTTON_WIDTH - 2 * BETWEEN_PAD,
        SCREEN_EDGE_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("Edge"),
        (button) -> {

        }));
    screen.addDrawableChild(new MiniButtonWidget(
        screen.width - SCREEN_EDGE_PAD - 2 * BUTTON_WIDTH - 1 * BETWEEN_PAD,
        SCREEN_EDGE_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("Center"),
        (button) -> {

        }));
    screen.addDrawableChild(new MiniButtonWidget(
        screen.width - SCREEN_EDGE_PAD - BUTTON_WIDTH,
        SCREEN_EDGE_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("Pixel"),
        (button) -> {

        }));

    addRowOfButtons(Text.translatable("armorstands.move.up"), Direction.UP, 5);
    addRowOfButtons(Text.translatable("armorstands.move.down"), Direction.DOWN, 4);
    addRowOfButtons(Text.translatable("armorstands.move.south"), Direction.SOUTH, 3);
    addRowOfButtons(Text.translatable("armorstands.move.north"), Direction.NORTH, 2);
    addRowOfButtons(Text.translatable("armorstands.move.east"), Direction.EAST, 1);
    addRowOfButtons(Text.translatable("armorstands.move.west"), Direction.WEST, 0);
  }

  private void addRowOfButtons(Text label, Direction direction, int index) {
    int refX = screen.width - SCREEN_EDGE_PAD - MINI_BUTTON_WIDTH;
    int refY = screen.height - SCREEN_EDGE_PAD - MINI_BUTTON_HEIGHT - index * (BETWEEN_PAD + MINI_BUTTON_HEIGHT);

    screen.addDrawable(LabelWidget.builder(
        label,
        refX - 2 * (BETWEEN_PAD + MINI_BUTTON_WIDTH) - 4,
        refY + MINI_BUTTON_HEIGHT / 2)
        .alignedRight()
        .alignedMiddle());
    screen.addDrawableChild(new MiniButtonWidget(
        refX - 2 * (BETWEEN_PAD + MINI_BUTTON_WIDTH),
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        Text.literal("1"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(screen.getArmorStand(), direction, 1);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        refX - 1 * (BETWEEN_PAD + MINI_BUTTON_WIDTH),
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        Text.literal("3"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(screen.getArmorStand(), direction, 3);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        refX,
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        Text.literal("8"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(screen.getArmorStand(), direction, 8);
        }));
  }
}
