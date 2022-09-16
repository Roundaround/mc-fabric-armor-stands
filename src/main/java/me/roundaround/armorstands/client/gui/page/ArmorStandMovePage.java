package me.roundaround.armorstands.client.gui.page;

import java.util.ArrayList;
import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.client.gui.widget.MiniButtonWidget;
import me.roundaround.armorstands.client.gui.widget.MoveButtonWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.SnapPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class ArmorStandMovePage extends AbstractArmorStandPage {
  private static final int MINI_BUTTON_WIDTH = 16;
  private static final int MINI_BUTTON_HEIGHT = 16;
  private static final int BUTTON_WIDTH = 46;
  private static final int BUTTON_HEIGHT = 16;
  private static final int SCREEN_EDGE_PAD = 4;
  private static final int BETWEEN_PAD = 2;
  private static final Identifier INDICATORS_TEXTURE = new Identifier(
      ArmorStandsMod.MOD_ID,
      "textures/indicators.png");

  private final ArrayList<MoveButtonWidget> moveButtons = new ArrayList<>();

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
        Text.translatable("armorstands.snap.label"),
        SCREEN_EDGE_PAD,
        screen.height - SCREEN_EDGE_PAD - 2 * (BUTTON_HEIGHT + BETWEEN_PAD))
        .shiftForPadding()
        .justifiedLeft()
        .alignedBottom());
    screen.addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD,
        screen.height - SCREEN_EDGE_PAD - 2 * BUTTON_HEIGHT - BETWEEN_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.snap.corner"),
        (button) -> {
          ClientNetworking.sendSnapPosPacket(SnapPosition.CORNER);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD + BUTTON_WIDTH + BETWEEN_PAD,
        screen.height - SCREEN_EDGE_PAD - 2 * BUTTON_HEIGHT - BETWEEN_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.snap.center"),
        (button) -> {
          ClientNetworking.sendSnapPosPacket(SnapPosition.CENTER);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD,
        screen.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.snap.standing"),
        (button) -> {
          ClientNetworking.sendSnapPosPacket(SnapPosition.STANDING);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD + BUTTON_WIDTH + BETWEEN_PAD,
        screen.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.snap.sitting"),
        (button) -> {
          ClientNetworking.sendSnapPosPacket(SnapPosition.SITTING);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD + 2 * (BUTTON_WIDTH + BETWEEN_PAD),
        screen.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.snap.player"),
        (button) -> {
          ClientNetworking.sendSnapPosPacket(SnapPosition.PLAYER);
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

    moveButtons.clear();

    addRowOfButtons(Text.translatable("armorstands.move.up"), Direction.UP, 5);
    addRowOfButtons(Text.translatable("armorstands.move.down"), Direction.DOWN, 4);
    addRowOfButtons(Text.translatable("armorstands.move.south"), Direction.SOUTH, 3);
    addRowOfButtons(Text.translatable("armorstands.move.north"), Direction.NORTH, 2);
    addRowOfButtons(Text.translatable("armorstands.move.east"), Direction.EAST, 1);
    addRowOfButtons(Text.translatable("armorstands.move.west"), Direction.WEST, 0);
  }

  @Override
  public void renderArmorStandOverlay(
      ArmorStandEntity armorStand,
      float tickDelta,
      MatrixStack matrixStack,
      VertexConsumerProvider vertexConsumerProvider,
      int light) {
    int mouseX = (int) Math.round(client.mouse.getX()
        * client.getWindow().getScaledWidth()
        / client.getWindow().getWidth());
    int mouseY = (int) Math.round(client.mouse.getY()
        * client.getWindow().getScaledHeight()
        / client.getWindow().getHeight());

    Optional<MoveButtonWidget> hoveredButton = moveButtons.stream()
        .filter((button) -> button.isMouseOver(mouseX, mouseY))
        .findFirst();

    if (hoveredButton.isEmpty()) {
      return;
    }

    Direction direction = hoveredButton.get().direction;

    if (direction.getAxis().isVertical()) {
      return;
    }

    matrixStack.push();

    matrixStack.multiply(Direction.UP.getRotationQuaternion());
    matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180 - direction.asRotation()));
    matrixStack.translate(-0.5f, 0.01f, 0.125f - 1.5f);

    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, INDICATORS_TEXTURE);
    RenderSystem.enableBlend();

    Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder buffer = tessellator.getBuffer();
    buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

    float x0 = (16 - 13) / 32f;
    float x1 = (16 + 13) / 32f;
    float z0 = 0;
    float z1 = 1;

    float u0 = 0;
    float v0 = 0;
    float u1 = 13f / 256f;
    float v1 = 16f / 256f;

    buffer.vertex(matrix4f, x0, 0, z1)
        .texture(u0, v1)
        .next();
    buffer.vertex(matrix4f, x1, 0, z1)
        .texture(u1, v1)
        .next();
    buffer.vertex(matrix4f, x1, 0, z0)
        .texture(u1, v0)
        .next();
    buffer.vertex(matrix4f, x0, 0, z0)
        .texture(u0, v0)
        .next();
    tessellator.draw();

    matrixStack.pop();
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

    screen.addDrawable(LabelWidget.builder(
        label,
        refX - 2 * (BETWEEN_PAD + MINI_BUTTON_WIDTH) - 4,
        refY + MINI_BUTTON_HEIGHT / 2)
        .justifiedRight()
        .alignedMiddle());

    MoveButtonWidget one = new MoveButtonWidget(
        refX - 2 * (BETWEEN_PAD + MINI_BUTTON_WIDTH),
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        direction,
        1);

    MoveButtonWidget three = new MoveButtonWidget(
        refX - 1 * (BETWEEN_PAD + MINI_BUTTON_WIDTH),
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        direction,
        3);

    MoveButtonWidget eight = new MoveButtonWidget(
        refX,
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        direction,
        8);

    screen.addDrawableChild(one);
    screen.addDrawableChild(three);
    screen.addDrawableChild(eight);

    if (direction.getAxis().isHorizontal()) {
      moveButtons.add(one);
      moveButtons.add(three);
      moveButtons.add(eight);
    }
  }
}
