package me.roundaround.armorstands.client.gui.widget;

import me.roundaround.armorstands.client.network.ClientNetworking;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.AxisDirection;

public class MoveButtonWidget extends MiniButtonWidget {
  public final Direction direction;
  
  public MoveButtonWidget(int x, int y, int width, int height, Direction direction, int amount) {
    super(x, y, width, height, Text.literal(getModifier(direction) + amount), (button) -> {
      ClientNetworking.sendAdjustPosPacket(direction, amount);
    });

    this.direction = direction;
  }

  private static String getModifier(Direction direction) {
    return direction.getDirection().equals(AxisDirection.POSITIVE) ? "+" : "-";
  }
}
