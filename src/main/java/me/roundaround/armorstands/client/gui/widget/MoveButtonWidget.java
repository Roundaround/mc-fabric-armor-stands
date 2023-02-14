package me.roundaround.armorstands.client.gui.widget;

import me.roundaround.armorstands.client.gui.ArmorStandState;
import me.roundaround.armorstands.client.network.ClientNetworking;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.AxisDirection;

public class MoveButtonWidget extends MiniButtonWidget {
  public final Direction direction;

  public MoveButtonWidget(
      ArmorStandState state,
      int x,
      int y,
      int width,
      int height,
      Direction direction,
      int amount) {
    super(
        x,
        y,
        width,
        height,
        Text.literal(getModifier(direction) + amount),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(state.getArmorStand(), direction, amount);
        });

    this.direction = direction;
  }

  private static String getModifier(Direction direction) {
    return direction.getDirection().equals(AxisDirection.POSITIVE) ? "+" : "-";
  }
}
