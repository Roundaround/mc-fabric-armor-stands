package me.roundaround.armorstands.client.gui.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

import me.roundaround.armorstands.client.gui.widget.ArmorStandFlagToggleWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.ArmorStandFlag;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class ArmorStandFlagsScreen extends AbstractArmorStandScreen {
  private final HashMap<ArmorStandFlag, Boolean> currentValues = new HashMap<>();
  private final HashMap<ArmorStandFlag, ArrayList<Consumer<Boolean>>> listeners = new HashMap<>();

  public ArmorStandFlagsScreen(ArmorStandEntity armorStand, int index) {
    super(armorStand, index, Text.literal(""));
    refreshFlags();
  }

  @Override
  protected void init() {
    listeners.values().forEach(ArrayList::clear);

    refreshFlags();

    addDrawableChild(new ButtonWidget(
        PADDING,
        PADDING,
        BUTTON_WIDTH_MEDIUM,
        BUTTON_HEIGHT,
        Text.literal("Highlight"),
        (button) -> {
          ClientNetworking.sendIdentifyStandPacket(armorStand);
        }));

    super.init();

    addFlagToggleWidget(
        ArmorStandFlag.BASE,
        width - PADDING - BUTTON_WIDTH_MEDIUM,
        height - 7 * (PADDING + ArmorStandFlagToggleWidget.WIDGET_HEIGHT),
        true,
        Text.literal("Base plate"));

    addFlagToggleWidget(
        ArmorStandFlag.ARMS,
        width - PADDING - BUTTON_WIDTH_MEDIUM,
        height - 6 * (PADDING + ArmorStandFlagToggleWidget.WIDGET_HEIGHT),
        false,
        Text.literal("Arms"));

    addFlagToggleWidget(
        ArmorStandFlag.SMALL,
        width - PADDING - BUTTON_WIDTH_MEDIUM,
        height - 5 * (PADDING + ArmorStandFlagToggleWidget.WIDGET_HEIGHT),
        false,
        Text.literal("Small"));

    addFlagToggleWidget(
        ArmorStandFlag.GRAVITY,
        width - PADDING - BUTTON_WIDTH_MEDIUM,
        height - 4 * (PADDING + ArmorStandFlagToggleWidget.WIDGET_HEIGHT),
        true,
        Text.literal("Gravity"));

    addFlagToggleWidget(
        ArmorStandFlag.VISIBLE,
        width - PADDING - BUTTON_WIDTH_MEDIUM,
        height - 3 * (PADDING + ArmorStandFlagToggleWidget.WIDGET_HEIGHT),
        false,
        Text.literal("Invisible"));

    addFlagToggleWidget(
        ArmorStandFlag.NAME,
        width - PADDING - BUTTON_WIDTH_MEDIUM,
        height - 2 * (PADDING + ArmorStandFlagToggleWidget.WIDGET_HEIGHT),
        false,
        Text.literal("Show name"));

    addFlagToggleWidget(
        ArmorStandFlag.INVULNERABLE,
        width - PADDING - BUTTON_WIDTH_MEDIUM,
        height - 1 * (PADDING + ArmorStandFlagToggleWidget.WIDGET_HEIGHT),
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

      boolean curr = flag.getValue(armorStand);
      boolean prev = currentValues.getOrDefault(flag, !curr);

      if (curr != prev) {
        currentValues.put(flag, curr);
        listeners.get(flag).forEach((listener) -> listener.accept(curr));
      }
    });
  }

  private void addFlagToggleWidget(ArmorStandFlag flag, int xPos, int yPos, boolean inverted, Text label) {
    ArmorStandFlagToggleWidget widget = new ArmorStandFlagToggleWidget(
        armorStand,
        flag,
        inverted,
        currentValues.get(flag),
        xPos,
        yPos,
        BUTTON_WIDTH_MEDIUM,
        label);
    addDrawableChild(widget);
    listeners.get(flag).add(widget);
  }
}
