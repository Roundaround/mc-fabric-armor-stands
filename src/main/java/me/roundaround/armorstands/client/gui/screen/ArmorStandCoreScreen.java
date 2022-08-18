package me.roundaround.armorstands.client.gui.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.ArmorStandFlag;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class ArmorStandCoreScreen extends AbstractArmorStandScreen {
  private final HashMap<ArmorStandFlag, Boolean> currentValues = new HashMap<>();

  public ArmorStandCoreScreen(ArmorStandEntity armorStand) {
    this(armorStand, false);
  }

  public ArmorStandCoreScreen(ArmorStandEntity armorStand, boolean highlightOnOpen) {
    super(armorStand, highlightOnOpen, Text.literal(""));
    refreshFlags();
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
    ArrayList<ButtonWidget> toggleButtons = new ArrayList<>();

    toggleButtons.add(new ButtonWidget(
        xPos,
        (yPos -= PADDING + BUTTON_HEIGHT),
        BUTTON_WIDTH_MEDIUM,
        BUTTON_HEIGHT,
        Text.literal("Toggle show name"),
        (button) -> {
          toggleFlag(ArmorStandFlag.NAME);
        }));

    toggleButtons.add(new ButtonWidget(
        xPos,
        (yPos -= PADDING + BUTTON_HEIGHT),
        BUTTON_WIDTH_MEDIUM,
        BUTTON_HEIGHT,
        Text.literal("Toggle visible"),
        (button) -> {
          toggleFlag(ArmorStandFlag.VISIBLE);
        }));

    toggleButtons.add(new ButtonWidget(
        xPos,
        (yPos -= PADDING + BUTTON_HEIGHT),
        BUTTON_WIDTH_MEDIUM,
        BUTTON_HEIGHT,
        Text.literal("Toggle gravity"),
        (button) -> {
          toggleFlag(ArmorStandFlag.GRAVITY);
        }));

    toggleButtons.add(new ButtonWidget(
        xPos,
        (yPos -= PADDING + BUTTON_HEIGHT),
        BUTTON_WIDTH_MEDIUM,
        BUTTON_HEIGHT,
        Text.literal("Toggle small"),
        (button) -> {
          toggleFlag(ArmorStandFlag.SMALL);
        }));

    toggleButtons.add(new ButtonWidget(
        xPos,
        (yPos -= PADDING + BUTTON_HEIGHT),
        BUTTON_WIDTH_MEDIUM,
        BUTTON_HEIGHT,
        Text.literal("Toggle arms"),
        (button) -> {
          toggleFlag(ArmorStandFlag.ARMS);
        }));

    toggleButtons.add(new ButtonWidget(
        xPos,
        (yPos -= PADDING + BUTTON_HEIGHT),
        BUTTON_WIDTH_MEDIUM,
        BUTTON_HEIGHT,
        Text.literal("Toggle base plate"),
        (button) -> {
          toggleFlag(ArmorStandFlag.BASE);
        }));

    // Reverse add order to ensure tab order is top to bottom
    for (int i = toggleButtons.size() - 1; i >= 0; i--) {
      addDrawableChild(toggleButtons.get(i));
    }
  }

  @Override
  public void tick() {
    refreshFlags();
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    super.render(matrixStack, mouseX, mouseY, delta);

    int xPos = PADDING;
    int yPos = height - PADDING - textRenderer.fontHeight;

    textRenderer.drawWithShadow(
        matrixStack,
        Text.literal(currentValues.get(ArmorStandFlag.NAME) ? "Name shown" : "Name hidden"),
        xPos,
        (yPos -= textRenderer.fontHeight + PADDING / 2),
        0xFFFFFFFF);

    textRenderer.drawWithShadow(
        matrixStack,
        Text.literal(currentValues.get(ArmorStandFlag.VISIBLE) ? "Invisible" : "Visible"),
        xPos,
        (yPos -= textRenderer.fontHeight + PADDING / 2),
        0xFFFFFFFF);

    textRenderer.drawWithShadow(
        matrixStack,
        Text.literal(currentValues.get(ArmorStandFlag.GRAVITY) ? "Gravity disabled" : "Gravity enabled"),
        xPos,
        (yPos -= textRenderer.fontHeight + PADDING / 2),
        0xFFFFFFFF);

    textRenderer.drawWithShadow(
        matrixStack,
        Text.literal(currentValues.get(ArmorStandFlag.SMALL) ? "Small size" : "Normal size"),
        xPos,
        (yPos -= textRenderer.fontHeight + PADDING / 2),
        0xFFFFFFFF);

    textRenderer.drawWithShadow(
        matrixStack,
        Text.literal(currentValues.get(ArmorStandFlag.ARMS) ? "Arms shown" : "Arms hidden"),
        xPos,
        (yPos -= textRenderer.fontHeight + PADDING / 2),
        0xFFFFFFFF);

    textRenderer.drawWithShadow(
        matrixStack,
        Text.literal(currentValues.get(ArmorStandFlag.BASE) ? "Base plate hidden" : "Base plate shown"),
        xPos,
        (yPos -= textRenderer.fontHeight + PADDING / 2),
        0xFFFFFFFF);
  }

  private void refreshFlags() {
    Arrays.stream(ArmorStandFlag.values()).forEach((flag) -> {
      currentValues.put(flag, flag.getValue(armorStand));
    });
  }

  private void toggleFlag(ArmorStandFlag flag) {
    ClientNetworking.sendSetFlagPacket(armorStand, flag, !currentValues.get(flag));
  }
}
