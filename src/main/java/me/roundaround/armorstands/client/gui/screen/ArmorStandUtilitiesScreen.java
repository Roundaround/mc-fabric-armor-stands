package me.roundaround.armorstands.client.gui.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import me.roundaround.armorstands.client.gui.widget.ArmorStandFlagToggleWidget;
import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.client.gui.widget.SimpleTooltipButtonWidget;
import me.roundaround.armorstands.client.util.LastUsedScreen.ScreenType;
import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.network.packet.c2s.UtilityActionPacket;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class ArmorStandUtilitiesScreen
    extends AbstractArmorStandScreen {
  public static final Text TITLE = Text.translatable("armorstands.screen.utilities");
  public static final int U_INDEX = 0;

  private static final int BUTTON_WIDTH = 60;
  private static final int BUTTON_HEIGHT = 16;

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

    initUtilityButtons();

    addLabel(LabelWidget.builder(
        Text.translatable("armorstands.utility.setup"),
        SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD
            - 3 * BUTTON_HEIGHT - 3 * BETWEEN_PAD)
        .alignedBottom()
        .justifiedLeft()
        .shiftForPadding()
        .build());
    addDrawableChild(new SimpleTooltipButtonWidget(
        this,
        SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD
            - 3 * BUTTON_HEIGHT - 2 * BETWEEN_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.utility.prepare"),
        Text.translatable("armorstands.utility.prepare.tooltip"),
        (button) -> {
          UtilityActionPacket.sendToServer(UtilityAction.PREPARE);
        }));
    addDrawableChild(new SimpleTooltipButtonWidget(
        this,
        SCREEN_EDGE_PAD + BUTTON_WIDTH + BETWEEN_PAD,
        this.height - SCREEN_EDGE_PAD
            - 3 * BUTTON_HEIGHT - 2 * BETWEEN_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.utility.toolRack"),
        Text.translatable("armorstands.utility.toolRack.tooltip"),
        (button) -> {
          UtilityActionPacket.sendToServer(UtilityAction.TOOL_RACK);
        }));
    addDrawableChild(new SimpleTooltipButtonWidget(
        this,
        SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD
            - 2 * BUTTON_HEIGHT - BETWEEN_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.utility.uprightItem"),
        Text.translatable("armorstands.utility.uprightItem.tooltip"),
        (button) -> {
          UtilityActionPacket.sendToServer(
              UtilityAction.UPRIGHT_ITEM.forSmall(
                  ArmorStandFlag.SMALL.getValue(armorStand)));
        }));
    addDrawableChild(new SimpleTooltipButtonWidget(
        this,
        SCREEN_EDGE_PAD + BUTTON_WIDTH + BETWEEN_PAD,
        this.height - SCREEN_EDGE_PAD
            - 2 * BUTTON_HEIGHT - BETWEEN_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.utility.flatItem"),
        Text.translatable("armorstands.utility.flatItem.tooltip"),
        (button) -> {
          UtilityActionPacket.sendToServer(
              UtilityAction.FLAT_ITEM.forSmall(
                  ArmorStandFlag.SMALL.getValue(armorStand)));
        }));
    addDrawableChild(new SimpleTooltipButtonWidget(
        this,
        SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD
            - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.utility.block"),
        Text.translatable("armorstands.utility.block.tooltip"),
        (button) -> {
          UtilityActionPacket.sendToServer(
              UtilityAction.BLOCK.forSmall(
                  ArmorStandFlag.SMALL.getValue(armorStand)));
        }));
    addDrawableChild(new SimpleTooltipButtonWidget(
        this,
        SCREEN_EDGE_PAD + BUTTON_WIDTH + BETWEEN_PAD,
        this.height - SCREEN_EDGE_PAD
            - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.utility.tool"),
        Text.translatable("armorstands.utility.tool.tooltip"),
        (button) -> {
          UtilityActionPacket.sendToServer(
              UtilityAction.TOOL.forSmall(
                  ArmorStandFlag.SMALL.getValue(armorStand)));
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
            ArmorStandPoseScreen.TITLE,
            ArmorStandPoseScreen.U_INDEX,
            ArmorStandPoseScreen::new),
        ScreenFactory.create(
            ArmorStandPresetsScreen.TITLE,
            ArmorStandPresetsScreen.U_INDEX,
            ArmorStandPresetsScreen::new),
        ScreenFactory.create(
            ArmorStandInventoryScreen.TITLE,
            ArmorStandInventoryScreen.U_INDEX,
            ArmorStandInventoryScreen::new)));

    addFlagToggleWidget(
        Text.translatable("armorstands.flags.base"),
        ArmorStandFlag.HIDE_BASE_PLATE,
        6,
        true);

    addFlagToggleWidget(
        Text.translatable("armorstands.flags.arms"),
        ArmorStandFlag.SHOW_ARMS,
        5,
        false);

    addFlagToggleWidget(
        Text.translatable("armorstands.flags.small"),
        ArmorStandFlag.SMALL,
        4,
        false);

    addFlagToggleWidget(
        Text.translatable("armorstands.flags.gravity"),
        ArmorStandFlag.NO_GRAVITY,
        3,
        true);

    addFlagToggleWidget(
        Text.translatable("armorstands.flags.visible"),
        ArmorStandFlag.INVISIBLE,
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
    int xPos = this.width - SCREEN_EDGE_PAD;
    int yPos = this.height - (index + 1) * (SCREEN_EDGE_PAD + ArmorStandFlagToggleWidget.WIDGET_HEIGHT);

    ArmorStandFlagToggleWidget widget = new ArmorStandFlagToggleWidget(
        this.textRenderer,
        flag,
        inverted,
        this.currentValues.get(flag),
        xPos,
        yPos,
        label);

    addDrawableChild(widget);
    listeners.get(flag).add(widget);
  }
}
