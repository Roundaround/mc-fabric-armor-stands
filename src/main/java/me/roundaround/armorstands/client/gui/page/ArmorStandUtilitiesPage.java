package me.roundaround.armorstands.client.gui.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import me.roundaround.armorstands.client.gui.widget.ArmorStandFlagToggleWidget;
import me.roundaround.armorstands.client.gui.widget.MiniButtonWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.network.UtilityAction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ArmorStandUtilitiesPage extends AbstractArmorStandPage {
  private static final int BUTTON_WIDTH = 100;
  protected static final int BUTTON_HEIGHT = 12;
  private static final int SCREEN_EDGE_PAD = 4;
  private static final int BETWEEN_PAD = 2;

  private final HashMap<ArmorStandFlag, Boolean> currentValues = new HashMap<>();
  private final HashMap<ArmorStandFlag, ArrayList<Consumer<Boolean>>> listeners = new HashMap<>();

  public ArmorStandUtilitiesPage(MinecraftClient client, ArmorStandScreen screen) {
    super(client, screen, Text.translatable("armorstands.page.utilities"), 0);
  }

  @Override
  public void preInit() {
    screen.addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD,
        screen.height - SCREEN_EDGE_PAD - 2 * 16 - BETWEEN_PAD,
        60,
        16,
        Text.translatable("armorstands.utility.copy"),
        (button) -> {
          ClientNetworking.sendUtilityActionPacket(UtilityAction.COPY);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD + 60 + BETWEEN_PAD,
        screen.height - SCREEN_EDGE_PAD - 2 * 16 - BETWEEN_PAD,
        60,
        16,
        Text.translatable("armorstands.utility.paste"),
        (button) -> {
          ClientNetworking.sendUtilityActionPacket(UtilityAction.PASTE);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD,
        screen.height - SCREEN_EDGE_PAD - 16,
        60,
        16,
        Text.translatable("armorstands.utility.prepare"),
        (button) -> {
          ClientNetworking.sendUtilityActionPacket(UtilityAction.PREPARE);
        }));
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
    int xPos = screen.width - SCREEN_EDGE_PAD - BUTTON_WIDTH;
    int yPos = screen.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT - index * (BETWEEN_PAD + BUTTON_HEIGHT);

    ArmorStandFlagToggleWidget widget = new ArmorStandFlagToggleWidget(
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
