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

public class ArmorStandSettingsPage extends AbstractArmorStandPage {
  private static final int BUTTON_WIDTH = 100;
  protected static final int BUTTON_HEIGHT = 20;
  protected static final int PADDING = 4;

  private final HashMap<ArmorStandFlag, Boolean> currentValues = new HashMap<>();
  private final HashMap<ArmorStandFlag, ArrayList<Consumer<Boolean>>> listeners = new HashMap<>();

  public ArmorStandSettingsPage(MinecraftClient client, ArmorStandScreen screen) {
    super(client, screen, Text.translatable("armorstands.page.settings"), 0);
  }

  @Override
  public void postInit() {
    listeners.values().forEach(ArrayList::clear);

    refreshFlags();

    addFlagToggleWidget(
        Text.translatable("armorstands.flags.base"),
        ArmorStandFlag.BASE,
        6,
        true);

    addFlagToggleWidget(
        Text.translatable("armorstands.flags.arms"),
        ArmorStandFlag.ARMS,
        5,
        false);

    addFlagToggleWidget(
        Text.translatable("armorstands.flags.small"),
        ArmorStandFlag.SMALL,
        4,
        false);

    addFlagToggleWidget(
        Text.translatable("armorstands.flags.gravity"),
        ArmorStandFlag.GRAVITY,
        3,
        true);

    addFlagToggleWidget(
        Text.translatable("armorstands.flags.visible"),
        ArmorStandFlag.VISIBLE,
        2,
        false);

    addFlagToggleWidget(
        Text.translatable("armorstands.flags.name"),
        ArmorStandFlag.NAME,
        1,
        false);

    addFlagToggleWidget(
        Text.translatable("armorstands.flags.invulnerable"),
        ArmorStandFlag.INVULNERABLE,
        0,
        false);
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

  private void addFlagToggleWidget(
      Text label,
      ArmorStandFlag flag,
      int index,
      boolean inverted) {
    int xPos = screen.width - PADDING - BUTTON_WIDTH;
    int yPos = screen.height - (index + 1) * (PADDING + ArmorStandFlagToggleWidget.WIDGET_HEIGHT);

    ArmorStandFlagToggleWidget widget = new ArmorStandFlagToggleWidget(
        client,
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
