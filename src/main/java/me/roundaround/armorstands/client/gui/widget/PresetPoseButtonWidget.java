package me.roundaround.armorstands.client.gui.widget;

import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.util.PosePreset;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class PresetPoseButtonWidget extends ButtonWidget {
  private PosePreset pose = PosePreset.DEFAULT;

  public PresetPoseButtonWidget(
      int x,
      int y,
      int width,
      int height) {
    super(x, y, width, height, Text.empty(), (button) -> {
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
    setMessage(this.pose.getLabel());
  }
}
