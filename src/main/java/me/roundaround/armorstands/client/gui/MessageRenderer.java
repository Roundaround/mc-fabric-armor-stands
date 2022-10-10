package me.roundaround.armorstands.client.gui;

import java.util.Optional;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class MessageRenderer {
  public static final Text TEXT_COPY = Text.literal("Stand copied");
  public static final Text TEXT_PASTE = Text.literal("Stand pasted");
  public static final Text TEXT_UNDO = Text.literal("Action undone");
  public static final Text TEXT_REDO = Text.literal("Action redone");

  private static final MessageRenderer INSTANCE = new MessageRenderer();

  private Optional<Message> shownMessage = Optional.empty();

  public static void init() {
    ClientTickEvents.END_CLIENT_TICK.register(INSTANCE::tick);
    ScreenEvents.AFTER_INIT.register((MinecraftClient client, Screen initScreen, int scaledWidth, int scaledHeight) -> {
      ScreenEvents.afterRender(initScreen).register(INSTANCE::render);
    });
  }

  public static void addMessage(Text text) {
    INSTANCE.shownMessage = Optional.of(new Message(text));
  }

  private void tick(MinecraftClient client) {
    if (shownMessage.isEmpty()) {
      return;
    }

    Message current = shownMessage.get();
    current.tick();

    if (current.isExpired()) {
      shownMessage = Optional.empty();
    }
  }

  private void render(Screen screen, MatrixStack matrixStack, int mouseX, int mouseY, float tickDelta) {
    if (shownMessage.isEmpty()) {
      return;
    }

    screen.renderTooltip(matrixStack, shownMessage.get().getText(), screen.width / 2, screen.height / 2);
  }

  private static class Message extends DrawableHelper {
    public static final int SHOW_DURATION = 120;

    private final Text text;
    private int timeRemaining;

    public Message(Text text) {
      this.text = text;

      timeRemaining = SHOW_DURATION;
    }

    public void tick() {
      timeRemaining--;
    }

    public Text getText() {
      return text;
    }

    public boolean isExpired() {
      return timeRemaining <= 0;
    }
  }
}
