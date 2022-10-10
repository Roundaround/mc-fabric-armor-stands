package me.roundaround.armorstands.client.gui.screen;

import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class MessageRenderer {
  public static final Text TEXT_COPY = Text.literal("Stand copied");
  public static final Text TEXT_PASTE = Text.literal("Stand pasted");
  public static final Text TEXT_UNDO = Text.literal("Action undone");
  public static final Text TEXT_REDO = Text.literal("Action redone");

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
    public static final int SHOW_DURATION = 80;
    public static final int ANIM_DURATION = 3;
    public static final int ANIM_IN_FINISH_TIME = SHOW_DURATION - ANIM_DURATION;

    private final Text text;
    private int timeRemaining;
    private long lastTick;

    public Message(Text text) {
      this.text = text;

      timeRemaining = SHOW_DURATION;
    }

    public void tick() {
      timeRemaining--;
      lastTick = Util.getMeasuringTimeMs();
    }

    public void render(Screen screen, MatrixStack matrixStack) {
      MinecraftClient client = MinecraftClient.getInstance();
      TextRenderer textRenderer = client.textRenderer;
      int x = (screen.width - textRenderer.getWidth(text)) / 2 - 12;
      int y = (screen.height - textRenderer.fontHeight) / 2 + 12;

      RenderSystem.setShaderColor(1f, 1f, 1f, getOpacity());
      RenderSystem.enableBlend();
      screen.renderTooltip(matrixStack, text, x, y);
    }

    public boolean isExpired() {
      return timeRemaining <= 0;
    }

    private float getOpacity() {
      long renderTime = Util.getMeasuringTimeMs();

      // 50ms per tick
      float partialTick = MathHelper.clamp((renderTime - lastTick) / 50f, 0, 1);
      float partialTimeRemaining = timeRemaining - partialTick;

      if (timeRemaining > ANIM_IN_FINISH_TIME) {
        // Animating in
        float animTime = Math.max(0f, partialTimeRemaining) - ANIM_IN_FINISH_TIME;
        float basePercent = MathHelper.clamp(animTime / ANIM_DURATION, 0, 1);
        return 1f - basePercent * basePercent;
      } else if (partialTimeRemaining < ANIM_DURATION) {
        // Animating out
        float animTime = Math.max(0f, partialTimeRemaining);
        float basePercent = MathHelper.clamp(animTime / ANIM_DURATION, 0, 1);
        return basePercent * basePercent;
      } else {
        // Fully showing
        return 1f;
      }
    }
  }
}
