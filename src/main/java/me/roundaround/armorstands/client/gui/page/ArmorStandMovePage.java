package me.roundaround.armorstands.client.gui.page;

import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.client.gui.widget.MiniButtonWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.AlignPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.AxisDirection;

public class ArmorStandMovePage extends AbstractArmorStandPage {
  private static final int MINI_BUTTON_WIDTH = 16;
  private static final int MINI_BUTTON_HEIGHT = 16;
  private static final int BUTTON_WIDTH = 40;
  private static final int BUTTON_HEIGHT = 16;
  private static final int SCREEN_EDGE_PAD = 4;
  private static final int BETWEEN_PAD = 2;

  private LabelWidget playerPosLabel;
  private LabelWidget playerBlockPosLabel;
  private LabelWidget standPosLabel;
  private LabelWidget standBlockPosLabel;

  public ArmorStandMovePage(MinecraftClient client, ArmorStandScreen screen) {
    super(client, screen, Text.translatable("armorstands.page.move"), 1);
  }

  @Override
  public void tick() {
    playerPosLabel.setText(getCurrectPosText(client.player));
    playerBlockPosLabel.setText(getCurrectBlockPosText(client.player));
    standPosLabel.setText(getCurrectPosText(screen.getArmorStand()));
    standBlockPosLabel.setText(getCurrectBlockPosText(screen.getArmorStand()));
  }

  @Override
  public void preInit() {
    screen.addDrawable(LabelWidget.builder(
        Text.translatable("armorstands.current.player"),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding());

    playerPosLabel = LabelWidget.builder(
      getCurrectPosText(client.player),
      SCREEN_EDGE_PAD,
      SCREEN_EDGE_PAD + LabelWidget.HEIGHT_WITH_PADDING)
      .alignedTop()
      .justifiedLeft()
      .shiftForPadding()
      .build();
    screen.addDrawable(playerPosLabel);
  
    playerBlockPosLabel = LabelWidget.builder(
        getCurrectBlockPosText(client.player),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + 2 * LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build();
    screen.addDrawable(playerBlockPosLabel);

    screen.addDrawable(LabelWidget.builder(
        Text.literal("Align horizontally"),
        SCREEN_EDGE_PAD + 2,
        screen.height - SCREEN_EDGE_PAD - 2 * (BUTTON_HEIGHT + BETWEEN_PAD))
        .justifiedLeft()
        .alignedBottom());
    screen.addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD,
        screen.height - SCREEN_EDGE_PAD - 2 * BUTTON_HEIGHT - BETWEEN_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("Edge"),
        (button) -> {
          ClientNetworking.sendAlignPosPacket(AlignPosition.EDGE);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD + BUTTON_WIDTH + BETWEEN_PAD,
        screen.height - SCREEN_EDGE_PAD - 2 * BUTTON_HEIGHT - BETWEEN_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("Center"),
        (button) -> {
          ClientNetworking.sendAlignPosPacket(AlignPosition.CENTER);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD,
        screen.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("Standing"),
        (button) -> {
          ClientNetworking.sendAlignPosPacket(AlignPosition.STANDING);
        }));
  }

  @Override
  public void postInit() {
    screen.addDrawable(LabelWidget.builder(
        Text.translatable("armorstands.current.stand"),
        screen.width - SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD)
        .alignedTop()
        .justifiedRight()
        .shiftForPadding());

    standPosLabel = LabelWidget.builder(
        getCurrectPosText(screen.getArmorStand()),
        screen.width - SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedRight()
        .shiftForPadding()
        .build();
    screen.addDrawable(standPosLabel);

    standBlockPosLabel = LabelWidget.builder(
        getCurrectBlockPosText(screen.getArmorStand()),
        screen.width - SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + 2 * LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedRight()
        .shiftForPadding()
        .build();
    screen.addDrawable(standBlockPosLabel);

    addRowOfButtons(Text.translatable("armorstands.move.up"), Direction.UP, 5);
    addRowOfButtons(Text.translatable("armorstands.move.down"), Direction.DOWN, 4);
    addRowOfButtons(Text.translatable("armorstands.move.south"), Direction.SOUTH, 3);
    addRowOfButtons(Text.translatable("armorstands.move.north"), Direction.NORTH, 2);
    addRowOfButtons(Text.translatable("armorstands.move.east"), Direction.EAST, 1);
    addRowOfButtons(Text.translatable("armorstands.move.west"), Direction.WEST, 0);
  }

  private Text getCurrectPosText(Entity entity) {
    String xStr = String.format("%.3f", entity.getX());
    String yStr = String.format("%.3f", entity.getY());
    String zStr = String.format("%.3f", entity.getZ());
    return Text.translatable("armorstands.current.position", xStr, yStr, zStr);
  }

  private Text getCurrectBlockPosText(Entity entity) {
    BlockPos pos = entity.getBlockPos();
    return Text.translatable("armorstands.current.block", pos.getX(), pos.getY(), pos.getZ());
  }

  private void addRowOfButtons(Text label, Direction direction, int index) {
    int refX = screen.width - SCREEN_EDGE_PAD - MINI_BUTTON_WIDTH;
    int refY = screen.height - SCREEN_EDGE_PAD - MINI_BUTTON_HEIGHT - index * (BETWEEN_PAD + MINI_BUTTON_HEIGHT);
    String modifier = direction.getDirection().equals(AxisDirection.POSITIVE) ? "+" : "-";

    screen.addDrawable(LabelWidget.builder(
        label,
        refX - 2 * (BETWEEN_PAD + MINI_BUTTON_WIDTH) - 4,
        refY + MINI_BUTTON_HEIGHT / 2)
        .justifiedRight()
        .alignedMiddle());
    screen.addDrawableChild(new MiniButtonWidget(
        refX - 2 * (BETWEEN_PAD + MINI_BUTTON_WIDTH),
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        Text.literal(modifier + "1"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(direction, 1);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        refX - 1 * (BETWEEN_PAD + MINI_BUTTON_WIDTH),
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        Text.literal(modifier + "3"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(direction, 3);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        refX,
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        Text.literal(modifier + "8"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(direction, 8);
        }));
  }
}
