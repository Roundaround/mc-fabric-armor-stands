package me.roundaround.armorstands.client.gui.widget;

import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.util.PosePreset;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class PresetPoseButtonWidget extends SimpleTooltipButtonWidget {
  private PosePreset pose = PosePreset.DEFAULT;

  public PresetPoseButtonWidget(
      Screen parent,
      int x,
      int y,
      int width,
      int height) {
    super(
        parent,
        x,
        y,
        width,
        height,
        Text.empty(),
        Text.empty(),
        (button) -> {
          ClientNetworking.sendSetPosePacket(((PresetPoseButtonWidget) button).getPose());
        });

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
    setTooltip(Text.translatable("armorstands.presets.source", pose.getSource().getDisplayName()));
    setMessage(this.pose.getLabel());
  }
}
