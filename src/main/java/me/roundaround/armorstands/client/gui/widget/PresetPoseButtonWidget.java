package me.roundaround.armorstands.client.gui.widget;

import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.util.PosePreset;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.MutableComponent;

public class PresetPoseButtonWidget extends Button.Plain {
  private PosePreset pose = PosePreset.DEFAULT;

  public PresetPoseButtonWidget(int width, int height) {
    super(
        0,
        0,
        width,
        height,
        net.minecraft.network.chat.Component.empty(),
        (button) -> ClientNetworking.sendSetPosePresetPacket(((PresetPoseButtonWidget) button).getPose()),
        Button.DEFAULT_NARRATION
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
    MutableComponent category = net.minecraft.network.chat.Component.translatable(
        "armorstands.presets.category",
        pose.getCategory().getDisplayName()
    );
    MutableComponent source = net.minecraft.network.chat.Component.translatable(
        "armorstands.presets.source",
        pose.getSource().getDisplayName()
    );
    setTooltip(Tooltip.create(category.append(net.minecraft.network.chat.Component.literal("\n")).append(source)));
    setMessage(this.pose.getDisplayName());
  }
}
