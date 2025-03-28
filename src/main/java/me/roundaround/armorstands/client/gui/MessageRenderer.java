package me.roundaround.armorstands.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;

public class MessageRenderer {
  public static final int BASE_COLOR = 0xFFFFFF;

  private final Screen screen;

  private Optional<Message> shownMessage = Optional.empty();

  public MessageRenderer(Screen screen) {
    this.screen = screen;
  }

  public void addMessage(Text text) {
    shownMessage = Optional.of(new Message(text));
  }

  public void addMessage(Text text, int color) {
    shownMessage = Optional.of(new Message(text, color));
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

  public void render(DrawContext drawContext) {
    shownMessage.ifPresent((message) -> {
      message.render(screen, drawContext);
    });
  }

  private static class Message {
    private static final int SHOW_DURATION = 40;

    private final Text text;
    private final int baseTextColor;
    private int timeRemaining;

    public Message(Text text) {
      this(text, BASE_COLOR);
    }

    public Message(Text text, int baseTextColor) {
      this.text = text;
      this.baseTextColor = baseTextColor;

      timeRemaining = SHOW_DURATION;
    }

    public void tick() {
      timeRemaining--;
    }

    public void render(Screen screen, DrawContext context) {
      MinecraftClient client = MinecraftClient.getInstance();
      TextRenderer textRenderer = client.textRenderer;
      int width = textRenderer.getWidth(text);
      int x = (screen.width - width) / 2;
      int y = screen.height - ButtonWidget.DEFAULT_HEIGHT - 1 - 6 - textRenderer.fontHeight;
      float opacity = MathHelper.clamp(timeRemaining / 10f, 0f, 1f);

      int backgroundAlpha = client.options.getTextBackgroundColor(0) >> 24 & 0xFF;

      int backgroundColor = (int) (opacity * backgroundAlpha) << 24 & 0xFF000000;
      int textColor = this.baseTextColor + ((int) (opacity * 255) << 24);

      context.fill(x - 2, y - 2, x + width + 2, y + textRenderer.fontHeight + 2, backgroundColor);
      context.drawTextWithShadow(textRenderer, text, x, y, textColor);
    }

    public boolean isExpired() {
      return timeRemaining <= 0;
    }
  }

  @FunctionalInterface
  public interface HasMessageRenderer {
    MessageRenderer getMessageRenderer();
  }
}
