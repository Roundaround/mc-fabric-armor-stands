package me.roundaround.armorstands.client.gui.widget;

import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.util.PosePreset;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class PresetPoseButtonWidget extends ButtonWidget {
  private PosePreset pose = PosePreset.DEFAULT;

  private final Screen parent;

  public PresetPoseButtonWidget(
      Screen parent,
      int x,
      int y,
      int width,
      int height) {
    super(x, y, width, height, Text.empty(), (button) -> {
      ClientNetworking.sendSetPosePacket(((PresetPoseButtonWidget) button).getPose());
    });

    this.parent = parent;

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

  @Override
  public void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
    this.parent.renderTooltip(
        matrixStack,
        Text.translatable("armorstands.presets.source", pose.getSource().getDisplayName()),
        this.hovered ? mouseX : this.x,
        this.hovered ? mouseY : this.y);
  }
}
