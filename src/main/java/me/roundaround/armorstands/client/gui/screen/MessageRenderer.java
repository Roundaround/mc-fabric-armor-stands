package me.roundaround.armorstands.client.gui.screen;

import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.client.gui.widget.PageSelectButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class MessageRenderer {
  public static final Text TEXT_COPY = Text.translatable("armorstands.message.copy");
  public static final Text TEXT_PASTE = Text.translatable("armorstands.message.paste");
  public static final Text TEXT_UNDO = Text.translatable("armorstands.message.undo");
  public static final Text TEXT_REDO = Text.translatable("armorstands.message.redo");

  private final Screen screen;

  private Optional<Message> shownMessage = Optional.empty();

  public MessageRenderer(Screen screen) {
    this.screen = screen;
  }

  public void addMessage(Text text) {
    shownMessage = Optional.of(new Message(text));
  }

  public void tick() {
    if (shownMessage.isEmpty()) {
      return;
    }

    Message current = shownMessage.get();
    current.tick();

    if (current.isExpired()) {
      shownMessage = Optional.empty();
    }
  }

  public void render(MatrixStack matrixStack) {
    shownMessage.ifPresent((message) -> {
      message.render(screen, matrixStack);
    });
  }

  private static class Message extends DrawableHelper {
    private static final int SHOW_DURATION = 40;

    private final Text text;
    private int timeRemaining;

    public Message(Text text) {
      this.text = text;

      timeRemaining = SHOW_DURATION;
    }

    public void tick() {
      timeRemaining--;
    }

    public void render(Screen screen, MatrixStack matrixStack) {
      MinecraftClient client = MinecraftClient.getInstance();
      TextRenderer textRenderer = client.textRenderer;
      int width = textRenderer.getWidth(text);
      int x = (screen.width - width) / 2;
      int y = screen.height - PageSelectButtonWidget.HEIGHT - 1 - 6 - textRenderer.fontHeight;
      float opacity = MathHelper.clamp(timeRemaining / 10f, 0f, 1f);

      int backgroundAlpha = client.options.getTextBackgroundColor(0) >> 24 & 0xFF;

      int backgroundColor = (int) (opacity * backgroundAlpha) << 24 & 0xFF000000;
      int textColor = 0xFFFFFF + ((int) (opacity * 255) << 24);

      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      fill(matrixStack, x - 2, y - 2, x + width + 2, y + textRenderer.fontHeight + 2, backgroundColor);
      textRenderer.drawWithShadow(matrixStack, text, x, y, textColor);
      RenderSystem.disableBlend();
    }

    public boolean isExpired() {
      return timeRemaining <= 0;
    }
  }
}
