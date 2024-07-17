package me.roundaround.armorstands.client.gui.screen;

import me.roundaround.armorstands.client.gui.widget.FlagToggleWidget;
import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.roundalib.client.gui.GuiUtil;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

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

//  @Override
//  protected void initStart() {
//    super.initStart();
//
//    listeners.clear();
//    refreshFlags();
//  }
//
//  @Override
//  protected void initLeft() {
//    super.initLeft();
//
////    addLabel(LabelWidget.builder(Text.translatable("armorstands.utility.setup"), GuiUtil.PADDING,
////        this.height - GuiUtil.PADDING - 3 * BUTTON_HEIGHT - 3 * (GuiUtil.PADDING / 2)
////    ).alignedBottom().justifiedLeft().shiftForPadding().build());
//    addDrawableChild(ButtonWidget.builder(Text.translatable("armorstands.utility.prepare"),
//            (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.PREPARE)
//        )
//        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
//        .position(GuiUtil.PADDING, this.height - GuiUtil.PADDING - 3 * BUTTON_HEIGHT - 2 * (GuiUtil.PADDING / 2))
//        .tooltip(Tooltip.of(Text.translatable("armorstands.utility.prepare.tooltip")))
//        .build());
//    addDrawableChild(ButtonWidget.builder(Text.translatable("armorstands.utility.toolRack"),
//            (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.TOOL_RACK)
//        )
//        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
//        .position(GuiUtil.PADDING + BUTTON_WIDTH + (GuiUtil.PADDING / 2),
//            this.height - GuiUtil.PADDING - 3 * BUTTON_HEIGHT - 2 * (GuiUtil.PADDING / 2)
//        )
//        .tooltip(Tooltip.of(Text.translatable("armorstands.utility.toolRack.tooltip")))
//        .build());
//    addDrawableChild(ButtonWidget.builder(Text.translatable("armorstands.utility.uprightItem"),
//            (button) -> ClientNetworking.sendUtilityActionPacket(
//                UtilityAction.UPRIGHT_ITEM.forSmall(ArmorStandFlag.SMALL.getValue(armorStand)))
//        )
//        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
//        .position(GuiUtil.PADDING, this.height - GuiUtil.PADDING - 2 * BUTTON_HEIGHT - (GuiUtil.PADDING / 2))
//        .tooltip(Tooltip.of(Text.translatable("armorstands.utility.uprightItem.tooltip")))
//        .build());
//    addDrawableChild(ButtonWidget.builder(Text.translatable("armorstands.utility.flatItem"),
//            (button) -> ClientNetworking.sendUtilityActionPacket(
//                UtilityAction.FLAT_ITEM.forSmall(ArmorStandFlag.SMALL.getValue(armorStand)))
//        )
//        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
//        .position(GuiUtil.PADDING + BUTTON_WIDTH + (GuiUtil.PADDING / 2),
//            this.height - GuiUtil.PADDING - 2 * BUTTON_HEIGHT - (GuiUtil.PADDING / 2)
//        )
//        .tooltip(Tooltip.of(Text.translatable("armorstands.utility.flatItem.tooltip")))
//        .build());
//    addDrawableChild(ButtonWidget.builder(Text.translatable("armorstands.utility.block"),
//            (button) -> ClientNetworking.sendUtilityActionPacket(
//                UtilityAction.BLOCK.forSmall(ArmorStandFlag.SMALL.getValue(armorStand)))
//        )
//        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
//        .position(GuiUtil.PADDING, this.height - GuiUtil.PADDING - BUTTON_HEIGHT)
//        .tooltip(Tooltip.of(Text.translatable("armorstands.utility.block.tooltip")))
//        .build());
//    addDrawableChild(ButtonWidget.builder(Text.translatable("armorstands.utility.tool"),
//            (button) -> ClientNetworking.sendUtilityActionPacket(
//                UtilityAction.TOOL.forSmall(ArmorStandFlag.SMALL.getValue(armorStand)))
//        )
//        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
//        .position(GuiUtil.PADDING + BUTTON_WIDTH + (GuiUtil.PADDING / 2), this.height - GuiUtil.PADDING - BUTTON_HEIGHT)
//        .tooltip(Tooltip.of(Text.translatable("armorstands.utility.tool.tooltip")))
//        .build());
//  }
//
//  @Override
//  protected void initRight() {
//    super.initRight();
//
//    List<ArmorStandFlag> flags = ArmorStandFlag.getFlags();
//    for (int i = flags.size() - 1; i >= 0; i--) {
//      addFlagToggleWidget(flags.get(flags.size() - i - 1), i);
//    }
//  }
//
//  @Override
//  public void handledScreenTick() {
//    super.handledScreenTick();
//
//    refreshFlags();
//  }
//
//  private void refreshFlags() {
//    Arrays.stream(ArmorStandFlag.values()).forEach((flag) -> {
//      Consumer<Boolean> listener = listeners.getOrDefault(flag, (value) -> {
//      });
//
//      boolean curr = flag.getValue(this.armorStand);
//      boolean prev = currentValues.getOrDefault(flag, !curr);
//
//      if (curr != prev) {
//        currentValues.put(flag, curr);
//        listener.accept(curr);
//      }
//    });
//  }
//
//  private void addFlagToggleWidget(ArmorStandFlag flag, int index) {
//    int xPos = this.width - GuiUtil.PADDING;
//    int yPos = this.height - (index + 1) * (GuiUtil.PADDING + FlagToggleWidget.WIDGET_HEIGHT);
//
//    FlagToggleWidget widget = new FlagToggleWidget(this.textRenderer, flag, this.currentValues.get(flag), xPos, yPos);
//
//    addDrawableChild(widget);
//    listeners.put(flag, widget::setValue);
//  }
}
