package me.roundaround.armorstands.client.gui;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class MessageRenderer {
  public static final int BASE_COLOR = 0xFFFFFF;

  private final Screen screen;

  private Optional<Message> shownMessage = Optional.empty();

  public MessageRenderer(Screen screen) {
    this.screen = screen;
  }

  public void addMessage(Component text) {
    shownMessage = Optional.of(new Message(text));
  }

  public void addMessage(Component text, int color) {
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

  public void render(GuiGraphics drawContext) {
    shownMessage.ifPresent((message) -> {
      message.render(screen, drawContext);
    });
  }

  private static class Message {
    private static final int SHOW_DURATION = 40;

    private final Component text;
    private final int baseTextColor;
    private int timeRemaining;

    public Message(Component text) {
      this(text, BASE_COLOR);
    }

    public Message(Component text, int baseTextColor) {
      this.text = text;
      this.baseTextColor = baseTextColor;

      timeRemaining = SHOW_DURATION;
    }

    public void tick() {
      timeRemaining--;
    }

    public void render(Screen screen, GuiGraphics context) {
      Minecraft client = Minecraft.getInstance();
      Font textRenderer = client.font;
      int width = textRenderer.width(text);
      int x = (screen.width - width) / 2;
      int y = screen.height - Button.DEFAULT_HEIGHT - 1 - 6 - textRenderer.lineHeight;
      float opacity = Mth.clamp(timeRemaining / 10f, 0f, 1f);

      int backgroundAlpha = client.options.getBackgroundColor(0) >> 24 & 0xFF;

      int backgroundColor = (int) (opacity * backgroundAlpha) << 24 & 0xFF000000;
      int textColor = this.baseTextColor + ((int) (opacity * 255) << 24);

      context.fill(x - 2, y - 2, x + width + 2, y + textRenderer.lineHeight + 2, backgroundColor);
      context.drawString(textRenderer, text, x, y, textColor);
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
