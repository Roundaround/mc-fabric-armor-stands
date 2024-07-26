package me.roundaround.armorstands.client.gui.widget;

import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.util.PosePreset;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class PresetPoseButtonWidget extends ButtonWidget {
  private PosePreset pose = PosePreset.DEFAULT;

  public PresetPoseButtonWidget(
      int width, int height
  ) {
    super(0, 0, width, height, Text.empty(),
        (button) -> ClientNetworking.sendSetPosePresetPacket(((PresetPoseButtonWidget) button).getPose()),
        ButtonWidget.DEFAULT_NARRATION_SUPPLIER
    );

    updateMessage();
  }

  public PosePreset getPose() {
    return pose;
  }

  public void setPose(PosePreset pose) {
    this.pose = pose;
    updateMessage();
  }

  private void updateMessage() {
    MutableText category = Text.translatable("armorstands.presets.category", pose.getCategory().getDisplayName());
    MutableText source = Text.translatable("armorstands.presets.source", pose.getSource().getDisplayName());
    setTooltip(Tooltip.of(category.append(Text.literal("\n")).append(source)));
    setMessage(this.pose.getDisplayName());
  }
}
