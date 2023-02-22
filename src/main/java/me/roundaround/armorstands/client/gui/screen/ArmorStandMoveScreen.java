package me.roundaround.armorstands.client.gui.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.gui.HasArmorStandOverlay;
import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.client.gui.widget.MiniButtonWidget;
import me.roundaround.armorstands.client.gui.widget.MoveButtonWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.client.util.LastUsedScreen.ScreenType;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
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

public class ArmorStandMoveScreen
    extends AbstractArmorStandScreen
    implements HasArmorStandOverlay {
  public static final Text TITLE = Text.translatable("armorstands.screen.move");
  public static final int U_INDEX = 1;

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
  private LabelWidget playerFacingLabel;
  private LabelWidget standPosLabel;
  private LabelWidget standBlockPosLabel;

  public ArmorStandMoveScreen(ArmorStandScreenHandler handler, ArmorStandEntity armorStand) {
    super(handler, TITLE, armorStand);
    this.supportsUndoRedo = true;
  }

  @Override
  public ScreenType getScreenType() {
    return ScreenType.MOVE;
  }

  @Override
  public ScreenConstructor<?> getNextScreen() {
    return ArmorStandRotateScreen::new;
  }

  @Override
  public ScreenConstructor<?> getPreviousScreen() {
    return ArmorStandUtilitiesScreen::new;
  }

  @Override
  public void init() {
    super.init();

    addDrawable(LabelWidget.builder(
        Text.translatable("armorstands.current.player"),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build());

    playerPosLabel = LabelWidget.builder(
        getCurrentPosText(client.player),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build();
    addDrawable(playerPosLabel);

    playerBlockPosLabel = LabelWidget.builder(
        getCurrentBlockPosText(client.player),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + 2 * LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build();
    addDrawable(playerBlockPosLabel);

    playerFacingLabel = LabelWidget.builder(
        getCurrentFacingText(client.player),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + 3 * LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build();
    addDrawable(playerFacingLabel);

    addDrawable(LabelWidget.builder(
        Text.translatable("armorstands.snap.label"),
        SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD - 2 * (BUTTON_HEIGHT + BETWEEN_PAD))
        .shiftForPadding()
        .justifiedLeft()
        .alignedBottom()
        .build());
    addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD - 2 * BUTTON_HEIGHT - BETWEEN_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.snap.corner"),
        (button) -> {
          ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_CORNER);
        }));
    addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD + BUTTON_WIDTH + BETWEEN_PAD,
        this.height - SCREEN_EDGE_PAD - 2 * BUTTON_HEIGHT - BETWEEN_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.snap.center"),
        (button) -> {
          ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_CENTER);
        }));
    addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.snap.standing"),
        (button) -> {
          ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_STANDING);
        }));
    addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD + BUTTON_WIDTH + BETWEEN_PAD,
        this.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.snap.sitting"),
        (button) -> {
          ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_SITTING);
        }));
    addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD + 2 * (BUTTON_WIDTH + BETWEEN_PAD),
        this.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.snap.player"),
        (button) -> {
          ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_PLAYER);
        }));

    initNavigationButtons(List.of(
        ScreenFactory.create(
            ArmorStandUtilitiesScreen.TITLE,
            ArmorStandUtilitiesScreen.U_INDEX,
            ArmorStandUtilitiesScreen::new),
        ScreenFactory.create(
            ArmorStandMoveScreen.TITLE,
            ArmorStandMoveScreen.U_INDEX),
        ScreenFactory.create(
            ArmorStandRotateScreen.TITLE,
            ArmorStandRotateScreen.U_INDEX,
            ArmorStandRotateScreen::new),
        ScreenFactory.create(
            ArmorStandPresetsScreen.TITLE,
            ArmorStandPresetsScreen.U_INDEX,
            ArmorStandPresetsScreen::new),
        ScreenFactory.create(
            ArmorStandPoseScreen.TITLE,
            ArmorStandPoseScreen.U_INDEX,
            ArmorStandPoseScreen::new),
        ScreenFactory.create(
            ArmorStandInventoryScreen.TITLE,
            ArmorStandInventoryScreen.U_INDEX,
            ArmorStandInventoryScreen::new)));

    addDrawable(LabelWidget.builder(
        Text.translatable("armorstands.current.stand"),
        this.width - SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD)
        .alignedTop()
        .justifiedRight()
        .shiftForPadding()
        .build());

    standPosLabel = LabelWidget.builder(
        getCurrentPosText(this.armorStand),
        this.width - SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedRight()
        .shiftForPadding()
        .build();
    addDrawable(standPosLabel);

    standBlockPosLabel = LabelWidget.builder(
        getCurrentBlockPosText(this.armorStand),
        this.width - SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + 2 * LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedRight()
        .shiftForPadding()
        .build();
    addDrawable(standBlockPosLabel);

    moveButtons.clear();

    addRowOfButtons(Text.translatable("armorstands.move.up"), Direction.UP, 5);
    addRowOfButtons(Text.translatable("armorstands.move.down"), Direction.DOWN, 4);
    addRowOfButtons(Text.translatable("armorstands.move.south"), Direction.SOUTH, 3);
    addRowOfButtons(Text.translatable("armorstands.move.north"), Direction.NORTH, 2);
    addRowOfButtons(Text.translatable("armorstands.move.east"), Direction.EAST, 1);
    addRowOfButtons(Text.translatable("armorstands.move.west"), Direction.WEST, 0);
  }

  @Override
  public void handledScreenTick() {
    super.handledScreenTick();

    playerPosLabel.setText(getCurrentPosText(client.player));
    playerBlockPosLabel.setText(getCurrentBlockPosText(client.player));
    playerFacingLabel.setText(getCurrentFacingText(client.player));
    standPosLabel.setText(getCurrentPosText(this.armorStand));
    standBlockPosLabel.setText(getCurrentBlockPosText(this.armorStand));
  }

  @Override
  public void renderArmorStandOverlay(
      ArmorStandEntity armorStand,
      float tickDelta,
      MatrixStack matrixStack,
      VertexConsumerProvider vertexConsumerProvider,
      int light) {
    if (armorStand != this.armorStand) {
      return;
    }

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

  private Text getCurrentPosText(Entity entity) {
    String xStr = String.format("%.3f", entity.getX());
    String yStr = String.format("%.3f", entity.getY());
    String zStr = String.format("%.3f", entity.getZ());
    return Text.translatable("armorstands.current.position", xStr, yStr, zStr);
  }

  private Text getCurrentBlockPosText(Entity entity) {
    BlockPos pos = entity.getBlockPos();
    return Text.translatable("armorstands.current.block", pos.getX(), pos.getY(), pos.getZ());
  }

  private Text getCurrentFacingText(Entity entity) {
    float currentRotation = entity.getYaw();
    Direction currentFacing = Direction.fromRotation(currentRotation);
    String towardsI18n = switch (currentFacing) {
      case NORTH -> "negZ";
      case SOUTH -> "posZ";
      case WEST -> "negX";
      case EAST -> "posX";
      default -> "posX";
    };
    Text towards = Text.translatable("armorstands.current.facing." + towardsI18n);
    return Text.translatable("armorstands.current.facing", currentFacing, towards.getString());
  }

  private void addRowOfButtons(Text label, Direction direction, int index) {
    int refX = this.width - SCREEN_EDGE_PAD - MINI_BUTTON_WIDTH;
    int refY = this.height - SCREEN_EDGE_PAD - MINI_BUTTON_HEIGHT - index * (BETWEEN_PAD + MINI_BUTTON_HEIGHT);

    addDrawable(LabelWidget.builder(
        label,
        refX - 2 * (BETWEEN_PAD + MINI_BUTTON_WIDTH) - 4,
        refY + MINI_BUTTON_HEIGHT / 2)
        .justifiedRight()
        .alignedMiddle()
        .build());

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

    addDrawableChild(one);
    addDrawableChild(three);
    addDrawableChild(eight);

    if (direction.getAxis().isHorizontal()) {
      moveButtons.add(one);
      moveButtons.add(three);
      moveButtons.add(eight);
    }
  }
}
