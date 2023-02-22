package me.roundaround.armorstands.client.gui.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import me.roundaround.armorstands.client.gui.widget.ArmorStandFlagToggleWidget;
import me.roundaround.armorstands.client.gui.widget.MiniButtonWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.client.util.LastUsedScreen.ScreenType;
import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class ArmorStandUtilitiesScreen
    extends AbstractArmorStandScreen {
  public static final Text TITLE = Text.translatable("armorstands.screen.utilities");
  public static final int U_INDEX = 0;

  private static final int BUTTON_WIDTH = 100;
  private static final int SCREEN_EDGE_PAD = 4;
  private static final int BETWEEN_PAD = 2;

  private final HashMap<ArmorStandFlag, Boolean> currentValues = new HashMap<>();
  private final HashMap<ArmorStandFlag, ArrayList<Consumer<Boolean>>> listeners = new HashMap<>();

  public ArmorStandUtilitiesScreen(
      ArmorStandScreenHandler handler,
      ArmorStandEntity armorStand) {
    super(handler, TITLE, armorStand);
    this.supportsUndoRedo = true;
  }

  @Override
  public ScreenType getScreenType() {
    return ScreenType.UTILITIES;
  }

  @Override
  public ScreenConstructor<?> getNextScreen() {
    return ArmorStandMoveScreen::new;
  }

  @Override
  public ScreenConstructor<?> getPreviousScreen() {
    return ArmorStandInventoryScreen::new;
  }

  @Override
  public void init() {
    super.init();

    listeners.values().forEach(ArrayList::clear);

    refreshFlags();

    addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD - 2 * 16 - BETWEEN_PAD,
        60,
        16,
        Text.translatable("armorstands.utility.copy"),
        (button) -> {
          ClientNetworking.sendUtilityActionPacket(UtilityAction.COPY);
        }));
    addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD + 60 + BETWEEN_PAD,
        this.height - SCREEN_EDGE_PAD - 2 * 16 - BETWEEN_PAD,
        60,
        16,
        Text.translatable("armorstands.utility.paste"),
        (button) -> {
          ClientNetworking.sendUtilityActionPacket(UtilityAction.PASTE);
        }));
    addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD - 16,
        60,
        16,
        Text.translatable("armorstands.utility.prepare"),
        (button) -> {
          ClientNetworking.sendUtilityActionPacket(UtilityAction.PREPARE);
        }));

    initNavigationButtons(List.of(
        ScreenFactory.create(
            ArmorStandUtilitiesScreen.TITLE,
            ArmorStandUtilitiesScreen.U_INDEX),
        ScreenFactory.create(
            ArmorStandMoveScreen.TITLE,
            ArmorStandMoveScreen.U_INDEX,
            ArmorStandMoveScreen::new),
        ScreenFactory.create(
            ArmorStandRotateScreen.TITLE,
            ArmorStandRotateScreen.U_INDEX,
            ArmorStandRotateScreen::new),
        ScreenFactory.create(
            ArmorStandPresetsScreen.TITLE,
            ArmorStandPresetsScreen.U_INDEX,
            ArmorStandPresetsScreen::new),
        ScreenFactory.create(
            ArmorStandPoseScreen.TITLE,
            ArmorStandPoseScreen.U_INDEX,
            ArmorStandPoseScreen::new),
        ScreenFactory.create(
            ArmorStandInventoryScreen.TITLE,
            ArmorStandInventoryScreen.U_INDEX,
            ArmorStandInventoryScreen::new)));

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
  public void handledScreenTick() {
    super.handledScreenTick();

    refreshFlags();
  }

  private void refreshFlags() {
    Arrays.stream(ArmorStandFlag.values()).forEach((flag) -> {
      if (!listeners.containsKey(flag)) {
        listeners.put(flag, new ArrayList<>());
      }

      boolean curr = flag.getValue(this.armorStand);
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
    int xPos = this.width - NAV_BUTTON_BOTTOM_PADDING - BUTTON_WIDTH;
    int yPos = this.height - (index + 1) * (NAV_BUTTON_BOTTOM_PADDING + ArmorStandFlagToggleWidget.WIDGET_HEIGHT);

    ArmorStandFlagToggleWidget widget = new ArmorStandFlagToggleWidget(
        flag,
        inverted,
        this.currentValues.get(flag),
        xPos,
        yPos,
        BUTTON_WIDTH,
        label);

    addDrawableChild(widget);
    listeners.get(flag).add(widget);
  }
}
