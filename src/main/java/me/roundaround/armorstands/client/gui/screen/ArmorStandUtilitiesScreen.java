package me.roundaround.armorstands.client.gui.screen;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import me.roundaround.armorstands.client.gui.widget.ArmorStandFlagToggleWidget;
import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.client.gui.widget.SimpleTooltipButtonWidget;
import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.network.packet.c2s.UtilityActionPacket;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.text.Text;

public class ArmorStandUtilitiesScreen extends AbstractArmorStandScreen {
  private static final int BUTTON_WIDTH = 60;
  private static final int BUTTON_HEIGHT = 16;

  private final HashMap<ArmorStandFlag, Boolean> currentValues = new HashMap<>();
  private final HashMap<ArmorStandFlag, Consumer<Boolean>> listeners = new HashMap<>();

  public ArmorStandUtilitiesScreen(ArmorStandScreenHandler handler) {
    super(handler, ScreenType.UTILITIES.getDisplayName());
    this.supportsUndoRedo = true;
  }

  @Override
  public ScreenType getScreenType() {
    return ScreenType.UTILITIES;
  }

  @Override
  protected void initStart() {
    super.initStart();

    listeners.clear();
    refreshFlags();
  }

  @Override
  protected void initLeft() {
    super.initLeft();

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
  }

  @Override
  protected void initRight() {
    super.initRight();

    List<ArmorStandFlag> flags = ArmorStandFlag.getFlags();
    for (int i = flags.size() - 1; i >= 0; i--) {
      addFlagToggleWidget(flags.get(flags.size() - i - 1), i);
    }
  }

  @Override
  public void handledScreenTick() {
    super.handledScreenTick();

    refreshFlags();
  }

  private void refreshFlags() {
    Arrays.stream(ArmorStandFlag.values()).forEach((flag) -> {
      Consumer<Boolean> listener = listeners.getOrDefault(flag, (value) -> {
      });

      boolean curr = flag.getValue(this.armorStand);
      boolean prev = currentValues.getOrDefault(flag, !curr);

      if (curr != prev) {
        currentValues.put(flag, curr);
        listener.accept(curr);
      }
    });
  }

  private void addFlagToggleWidget(ArmorStandFlag flag, int index) {
    int xPos = this.width - SCREEN_EDGE_PAD;
    int yPos = this.height - (index + 1) * (SCREEN_EDGE_PAD + ArmorStandFlagToggleWidget.WIDGET_HEIGHT);

    ArmorStandFlagToggleWidget widget = new ArmorStandFlagToggleWidget(
        this.textRenderer,
        flag,
        this.currentValues.get(flag),
        xPos,
        yPos);

    addDrawableChild(widget);
    listeners.put(flag, t -> widget.setValue(t));
  }
}
