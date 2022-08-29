package me.roundaround.armorstands.client.gui.page;

import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
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
    int refX = screen.width - SCREEN_EDGE_PAD - BUTTON_WIDTH;
    int refY = screen.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT;

    screen.addDrawableChild(new MiniButtonWidget(
        refX,
        refY - 3 * (BETWEEN_PAD + BUTTON_HEIGHT),
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("+8"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(screen.getArmorStand(), Direction.UP, 8);
        }));

    screen.addDrawableChild(new MiniButtonWidget(
        refX,
        refY - 2 * (BETWEEN_PAD + BUTTON_HEIGHT),
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("+8"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(screen.getArmorStand(), Direction.SOUTH, 8);
        }));

    screen.addDrawableChild(new MiniButtonWidget(
        refX - 2 * (BETWEEN_PAD + BUTTON_WIDTH),
        refY - 1 * (BETWEEN_PAD + BUTTON_HEIGHT),
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("+1"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(screen.getArmorStand(), Direction.WEST, 1);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        refX - 1 * (BETWEEN_PAD + BUTTON_WIDTH),
        refY - 1 * (BETWEEN_PAD + BUTTON_HEIGHT),
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("+3"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(screen.getArmorStand(), Direction.WEST, 3);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        refX,
        refY - 1 * (BETWEEN_PAD + BUTTON_HEIGHT),
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("+8"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(screen.getArmorStand(), Direction.WEST, 8);
        }));

    screen.addDrawableChild(new MiniButtonWidget(
        refX - 2 * (BETWEEN_PAD + BUTTON_WIDTH),
        refY,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("+1"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(screen.getArmorStand(), Direction.EAST, 1);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        refX - 1 * (BETWEEN_PAD + BUTTON_WIDTH),
        refY,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("+3"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(screen.getArmorStand(), Direction.EAST, 3);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        refX,
        refY,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("+8"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(screen.getArmorStand(), Direction.EAST, 8);
        }));
  }

  @Override
  public void drawBackground(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    int refX = screen.width - SCREEN_EDGE_PAD - BUTTON_WIDTH - 2 * (BETWEEN_PAD + BUTTON_WIDTH);
    int refY = screen.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT;

    TextRenderer textRenderer = client.textRenderer;

    Text text = Text.literal("East (+X)");
    int width = textRenderer.getWidth(text);
    fill(
        matrixStack,
        refX - SCREEN_EDGE_PAD - width - 2,
        refY + Math.round((BUTTON_HEIGHT - textRenderer.fontHeight) / 2f) - 1,
        refX - SCREEN_EDGE_PAD + 2,
        refY + Math.round((BUTTON_HEIGHT - textRenderer.fontHeight) / 2f) + textRenderer.fontHeight + 1,
        0x40000000);
    textRenderer.draw(
        matrixStack,
        text,
        refX - SCREEN_EDGE_PAD - width,
        refY + (BUTTON_HEIGHT - textRenderer.fontHeight) / 2f,
        0xFFFFFFFF);

    text = Text.literal("West (-X)");
    width = textRenderer.getWidth(text);
    fill(
        matrixStack,
        refX - SCREEN_EDGE_PAD - width - 2,
        refY - 1 * (BETWEEN_PAD + BUTTON_HEIGHT) + Math.round((BUTTON_HEIGHT - textRenderer.fontHeight) / 2f) - 1,
        refX - SCREEN_EDGE_PAD + 2,
        refY - 1 * (BETWEEN_PAD + BUTTON_HEIGHT) + Math.round((BUTTON_HEIGHT - textRenderer.fontHeight) / 2f)
            + textRenderer.fontHeight + 1,
        0x40000000);
    textRenderer.draw(
        matrixStack,
        text,
        refX - SCREEN_EDGE_PAD - width,
        refY - 1 * (BETWEEN_PAD + BUTTON_HEIGHT) + (BUTTON_HEIGHT - (float) textRenderer.fontHeight) / 2,
        0xFFFFFFFF);
  }
}
