package me.roundaround.armorstands.client.gui.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import me.roundaround.armorstands.client.gui.widget.ArmorStandFlagToggleWidget;
import me.roundaround.armorstands.network.ArmorStandFlag;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ArmorStandFlagsPage extends AbstractArmorStandPage {
  private static final int BUTTON_WIDTH = 100;
  protected static final int BUTTON_HEIGHT = 20;
  protected static final int PADDING = 4;

  private final HashMap<ArmorStandFlag, Boolean> currentValues = new HashMap<>();
  private final HashMap<ArmorStandFlag, ArrayList<Consumer<Boolean>>> listeners = new HashMap<>();

  public ArmorStandFlagsPage(MinecraftClient client, ArmorStandScreen screen) {
    super(client, screen, Text.translatable("armorstands.page.flags"));
  }

  @Override
  public void init() {
    listeners.values().forEach(ArrayList::clear);

    refreshFlags();

    addFlagToggleWidget(
        ArmorStandFlag.BASE,
        screen.width - PADDING - BUTTON_WIDTH,
        screen.height - 7 * (PADDING + ArmorStandFlagToggleWidget.WIDGET_HEIGHT),
        true,
        Text.literal("Base plate"));

    addFlagToggleWidget(
        ArmorStandFlag.ARMS,
        screen.width - PADDING - BUTTON_WIDTH,
        screen.height - 6 * (PADDING + ArmorStandFlagToggleWidget.WIDGET_HEIGHT),
        false,
        Text.literal("Arms"));

    addFlagToggleWidget(
        ArmorStandFlag.SMALL,
        screen.width - PADDING - BUTTON_WIDTH,
        screen.height - 5 * (PADDING + ArmorStandFlagToggleWidget.WIDGET_HEIGHT),
        false,
        Text.literal("Small"));

    addFlagToggleWidget(
        ArmorStandFlag.GRAVITY,
        screen.width - PADDING - BUTTON_WIDTH,
        screen.height - 4 * (PADDING + ArmorStandFlagToggleWidget.WIDGET_HEIGHT),
        true,
        Text.literal("Gravity"));

    addFlagToggleWidget(
        ArmorStandFlag.VISIBLE,
        screen.width - PADDING - BUTTON_WIDTH,
        screen.height - 3 * (PADDING + ArmorStandFlagToggleWidget.WIDGET_HEIGHT),
        false,
        Text.literal("Invisible"));

    addFlagToggleWidget(
        ArmorStandFlag.NAME,
        screen.width - PADDING - BUTTON_WIDTH,
        screen.height - 2 * (PADDING + ArmorStandFlagToggleWidget.WIDGET_HEIGHT),
        false,
        Text.literal("Show name"));

    addFlagToggleWidget(
        ArmorStandFlag.INVULNERABLE,
        screen.width - PADDING - BUTTON_WIDTH,
        screen.height - 1 * (PADDING + ArmorStandFlagToggleWidget.WIDGET_HEIGHT),
        false,
        Text.literal("Invulnerable"));
  }

  @Override
  public void tick() {
    refreshFlags();
  }

  private void refreshFlags() {
    Arrays.stream(ArmorStandFlag.values()).forEach((flag) -> {
      if (!listeners.containsKey(flag)) {
        listeners.put(flag, new ArrayList<>());
      }

      boolean curr = flag.getValue(screen.getArmorStand());
      boolean prev = currentValues.getOrDefault(flag, !curr);

      if (curr != prev) {
        currentValues.put(flag, curr);
        listeners.get(flag).forEach((listener) -> listener.accept(curr));
      }
    });
  }

  private void addFlagToggleWidget(ArmorStandFlag flag, int xPos, int yPos, boolean inverted, Text label) {
    ArmorStandFlagToggleWidget widget = new ArmorStandFlagToggleWidget(
        client,
        screen.getArmorStand(),
        flag,
        inverted,
        currentValues.get(flag),
        xPos,
        yPos,
        BUTTON_WIDTH,
        label);
    screen.addDrawableChild(widget);
    listeners.get(flag).add(widget);
  }
}
