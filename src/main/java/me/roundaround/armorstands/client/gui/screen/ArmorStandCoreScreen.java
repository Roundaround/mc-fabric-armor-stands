package me.roundaround.armorstands.client.gui.screen;

import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.ArmorStandFlag;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class ArmorStandCoreScreen extends AbstractArmorStandScreen {
  public ArmorStandCoreScreen(ArmorStandEntity armorStand) {
    super(armorStand, Text.literal(""));
  }

  @Override
  protected void init() {
    addDrawableChild(new ButtonWidget(
        PADDING,
        PADDING,
        BUTTON_WIDTH_MEDIUM,
        BUTTON_HEIGHT,
        Text.literal("Highlight"),
        (button) -> {
          ClientNetworking.sendIdentifyStandPacket(armorStand);
        }));

    int xPos = width - PADDING - BUTTON_WIDTH_MEDIUM;
    int yPos = height;

    addDrawableChild(new ButtonWidget(
        xPos,
        (yPos -= PADDING + BUTTON_HEIGHT),
        BUTTON_WIDTH_MEDIUM,
        BUTTON_HEIGHT,
        Text.literal("Toggle show name"),
        (button) -> {
          ClientNetworking.sendToggleFlagPacket(armorStand, ArmorStandFlag.NAME);
        }));

    addDrawableChild(new ButtonWidget(
        xPos,
        (yPos -= PADDING + BUTTON_HEIGHT),
        BUTTON_WIDTH_MEDIUM,
        BUTTON_HEIGHT,
        Text.literal("Toggle visible"),
        (button) -> {
          ClientNetworking.sendToggleFlagPacket(armorStand, ArmorStandFlag.VISIBLE);
        }));

    addDrawableChild(new ButtonWidget(
        xPos,
        (yPos -= PADDING + BUTTON_HEIGHT),
        BUTTON_WIDTH_MEDIUM,
        BUTTON_HEIGHT,
        Text.literal("Toggle gravity"),
        (button) -> {
          ClientNetworking.sendToggleFlagPacket(armorStand, ArmorStandFlag.GRAVITY);
        }));

    addDrawableChild(new ButtonWidget(
        xPos,
        (yPos -= PADDING + BUTTON_HEIGHT),
        BUTTON_WIDTH_MEDIUM,
        BUTTON_HEIGHT,
        Text.literal("Toggle small"),
        (button) -> {
          ClientNetworking.sendToggleFlagPacket(armorStand, ArmorStandFlag.SMALL);
        }));

    addDrawableChild(new ButtonWidget(
        xPos,
        (yPos -= PADDING + BUTTON_HEIGHT),
        BUTTON_WIDTH_MEDIUM,
        BUTTON_HEIGHT,
        Text.literal("Toggle arms"),
        (button) -> {
          ClientNetworking.sendToggleFlagPacket(armorStand, ArmorStandFlag.ARMS);
        }));

    addDrawableChild(new ButtonWidget(
        xPos,
        (yPos -= PADDING + BUTTON_HEIGHT),
        BUTTON_WIDTH_MEDIUM,
        BUTTON_HEIGHT,
        Text.literal("Toggle base plate"),
        (button) -> {
          ClientNetworking.sendToggleFlagPacket(armorStand, ArmorStandFlag.BASE);
        }));
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    super.render(matrixStack, mouseX, mouseY, delta);

    int xPos = PADDING;
    int yPos = height - PADDING - textRenderer.fontHeight;

    textRenderer.drawWithShadow(
        matrixStack,
        Text.literal(armorStand.isCustomNameVisible() ? "Name shown" : "Name hidden"),
        xPos,
        (yPos -= textRenderer.fontHeight + PADDING / 2),
        0xFFFFFFFF);

    textRenderer.drawWithShadow(
        matrixStack,
        Text.literal(armorStand.isInvisible() ? "Invisible" : "Visible"),
        xPos,
        (yPos -= textRenderer.fontHeight + PADDING / 2),
        0xFFFFFFFF);

    textRenderer.drawWithShadow(
        matrixStack,
        Text.literal(armorStand.hasNoGravity() ? "Gravity disabled" : "Gravity enabled"),
        xPos,
        (yPos -= textRenderer.fontHeight + PADDING / 2),
        0xFFFFFFFF);

    textRenderer.drawWithShadow(
        matrixStack,
        Text.literal(armorStand.isSmall() ? "Small size" : "Normal size"),
        xPos,
        (yPos -= textRenderer.fontHeight + PADDING / 2),
        0xFFFFFFFF);

    textRenderer.drawWithShadow(
        matrixStack,
        Text.literal(armorStand.shouldShowArms() ? "Arms shown" : "Arms hidden"),
        xPos,
        (yPos -= textRenderer.fontHeight + PADDING / 2),
        0xFFFFFFFF);

    textRenderer.drawWithShadow(
        matrixStack,
        Text.literal(armorStand.shouldHideBasePlate() ? "Base plate hidden" : "Base plate shown"),
        xPos,
        (yPos -= textRenderer.fontHeight + PADDING / 2),
        0xFFFFFFFF);
  }
}
