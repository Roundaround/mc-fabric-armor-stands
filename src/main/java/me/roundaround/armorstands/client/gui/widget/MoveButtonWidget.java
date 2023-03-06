package me.roundaround.armorstands.client.gui.widget;

import me.roundaround.armorstands.network.packet.c2s.AdjustPosPacket;
import me.roundaround.armorstands.util.MoveMode;
import me.roundaround.armorstands.util.MoveUnits;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

public class MoveButtonWidget extends ButtonWidget {
  public final Direction direction;
  public final int amount;

  private MoveMode mode = MoveMode.RELATIVE;
  private MoveUnits units = MoveUnits.PIXELS;

  public MoveButtonWidget(
      int x,
      int y,
      int width,
      int height,
      Direction direction,
      int amount,
      MoveMode mode,
      MoveUnits units) {
    super(
        x,
        y,
        width,
        height,
        getText(amount, units),
        (rawButton) -> {
          MoveButtonWidget button = (MoveButtonWidget) rawButton;
          AdjustPosPacket.sendToServer(
              button.direction,
              button.amount,
              button.mode,
              button.units);
        });

    this.direction = direction;
    this.amount = amount;
    this.mode = mode;
    this.units = units;
  }

  public void setMode(MoveMode mode) {
    this.mode = mode;
    setUnits(this.mode.getDefaultUnits());
  }

  public void setUnits(MoveUnits units) {
    this.units = units;
    setMessage(getText(this.amount, this.units));
  }

  public static Text getText(int amount, MoveUnits units) {
    return units.getButtonText(amount);
  }

  public static Text getDirectionText(Direction direction, MoveMode mode) {
    return mode.getDirectionText(direction);
  }
}
