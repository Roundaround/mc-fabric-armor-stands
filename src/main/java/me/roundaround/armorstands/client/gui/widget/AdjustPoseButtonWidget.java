package me.roundaround.armorstands.client.gui.widget;

import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.util.EulerAngleParameter;
import me.roundaround.armorstands.util.PosePart;
import net.minecraft.text.Text;

public class AdjustPoseButtonWidget extends MiniButtonWidget {
  public final PosePart part;
  public final EulerAngleParameter parameter;

  public AdjustPoseButtonWidget(
      int x,
      int y,
      int width,
      int height,
      PosePart part,
      EulerAngleParameter parameter,
      int amount) {
    super(
        x,
        y,
        width,
        height,
        Text.literal(getModifier(parameter) + amount),
        (button) -> {
          ClientNetworking.sendAdjustPosePacket(part, parameter, amount);
        });

    this.part = part;
    this.parameter = parameter;
  }

  private static String getModifier(EulerAngleParameter parameter) {
    return parameter.equals(EulerAngleParameter.YAW) ? "" : parameter.equals(EulerAngleParameter.PITCH) ? "+" : "-";
  }
}
