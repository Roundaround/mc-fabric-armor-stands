package me.roundaround.armorstands.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.ArmorStandsClientMod;
import me.roundaround.armorstands.client.gui.ArmorStandState;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class ArmorStandInventoryScreen
    extends AbstractArmorStandScreen {
  public static final Text TITLE = Text.translatable("armorstands.page.inventory");

  private static final int BACKGROUND_WIDTH = 176;
  private static final int BACKGROUND_HEIGHT = 166;
  private static final Identifier CUSTOM_TEXTURE = new Identifier(
      ArmorStandsMod.MOD_ID,
      "textures/gui/container/inventory.png");
  private static final Identifier CUSTOM_TEXTURE_DARK = new Identifier(
      ArmorStandsMod.MOD_ID,
      "textures/gui/container/inventory_dark.png");

  public ArmorStandInventoryScreen(
      ArmorStandScreenHandler handler,
      ArmorStandState state) {
    super(handler, true, TITLE, state);
    this.renderInventories = true;
  }

  @Override
  public void init() {
    super.init();
    initNavigationButtons();
  }

  @Override
  protected void drawBackground(MatrixStack matrixStack, float delta, int mouseX, int mouseY) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

    int x = (this.width - BACKGROUND_WIDTH) / 2;
    int y = (this.height - BACKGROUND_HEIGHT) / 2;

    RenderSystem.setShaderTexture(0,
        ArmorStandsClientMod.darkModeDetected
            ? CUSTOM_TEXTURE_DARK
            : CUSTOM_TEXTURE);
    drawTexture(
        matrixStack,
        x,
        y,
        0,
        0,
        BACKGROUND_WIDTH,
        BACKGROUND_HEIGHT);

    drawEntity(x + 88, y + 75, 30, 0f, 0f);
  }

  protected void drawEntity(int x, int y, int size, float mouseX, float mouseY) {
    EntityRenderDispatcher entityRenderDispatcher = client.getEntityRenderDispatcher();
    ArmorStandEntity armorStand = this.state.getArmorStand();

    MatrixStack matrixStack = RenderSystem.getModelViewStack();
    matrixStack.push();
    matrixStack.translate(x, y, 1050f);
    matrixStack.scale(1f, 1f, -1f);
    RenderSystem.applyModelViewMatrix();

    MatrixStack matrixStack2 = new MatrixStack();
    matrixStack2.translate(0f, 0f, 1000f);
    matrixStack2.scale(size, size, size);
    Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180f);
    Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(0f);
    quaternion.hamiltonProduct(quaternion2);
    matrixStack2.multiply(quaternion);

    float bodyYaw = armorStand.bodyYaw;
    float yaw = armorStand.getYaw();
    boolean name = armorStand.isCustomNameVisible();
    armorStand.bodyYaw = 180f;
    armorStand.setYaw(180f);
    armorStand.setCustomNameVisible(false);

    DiffuseLighting.method_34742();
    quaternion2.conjugate();
    entityRenderDispatcher.setRotation(quaternion2);
    entityRenderDispatcher.setRenderShadows(false);
    VertexConsumerProvider.Immediate immediate = client
        .getBufferBuilders()
        .getEntityVertexConsumers();
    runInFancyGraphicsMode(() -> {
      entityRenderDispatcher.render(armorStand, 0, 0, 0, 0f, 1f, matrixStack2, immediate, 0xF000F0);
    });
    immediate.draw();
    entityRenderDispatcher.setRenderShadows(true);

    armorStand.bodyYaw = bodyYaw;
    armorStand.setYaw(yaw);
    armorStand.setCustomNameVisible(name);

    matrixStack.pop();
    RenderSystem.applyModelViewMatrix();
    DiffuseLighting.enableGuiDepthLighting();
  }

  private void runInFancyGraphicsMode(Runnable runnable) {
    if (!MinecraftClient.isFabulousGraphicsOrBetter()) {
      runnable.run();
      return;
    }

    SimpleOption<GraphicsMode> option = client.options.getGraphicsMode();
    GraphicsMode originalGraphicsMode = option.getValue();
    option.setValue(GraphicsMode.FANCY);
    runnable.run();
    option.setValue(originalGraphicsMode);
  }
}
