package me.roundaround.armorstands.client.gui.widget;

import me.roundaround.armorstands.client.network.ClientNetworking;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

public class MoveButtonWidget extends ButtonWidget {
  public final Direction direction;
  public final int amount;

  private Mode mode = Mode.RELATIVE;

  public MoveButtonWidget(
      int x,
      int y,
      int width,
      int height,
      Direction direction,
      int amount,
      Mode mode) {
    super(
        x,
        y,
        width,
        height,
        getText(amount, Mode.RELATIVE),
        (button) -> {
          ((MoveButtonWidget) button).mode.apply(direction, amount);
        });

    this.direction = direction;
    this.amount = amount;
    this.mode = mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
    setMessage(getText(this.amount, this.mode));
  }

  public static Text getText(int amount, Mode mode) {
    return mode.getButtonText(amount);
  }

  public static Text getDirectionText(Direction direction, Mode mode) {
    return mode.getDirectionText(direction);
  }

  public static enum Mode {
    RELATIVE("relative"),
    LOCAL_TO_STAND("stand"),
    LOCAL_TO_PLAYER("player");

    private final String id;

    private Mode(String id) {
      this.id = id;
    }

    public void apply(Direction direction, int amount) {
      switch (this) {
        case RELATIVE:
          ClientNetworking.sendAdjustPosPacket(direction, amount);
          break;
        case LOCAL_TO_STAND:
          ClientNetworking.sendAdjustPosPacket(direction, amount, false);
          break;
        case LOCAL_TO_PLAYER:
          ClientNetworking.sendAdjustPosPacket(direction, amount, true);
          break;
      }
    }

    public Text getOptionValueText() {
      return Text.translatable("armorstands.move.mode." + this.id);
    }

    public Text getButtonText(int amount) {
      return Text.translatable("armorstands.move." + this.id + "." + amount);
    }

    public Text getDirectionText(Direction direction) {
      if (this.equals(RELATIVE)) {
        return Text.translatable("armorstands.move." + direction.getName());
      } else {
        return Text.translatable("armorstands.move.local." + direction.getName());
      }
    }

    public Text getUnitsText() {
      return Text.translatable("armorstands.move.units." + this.id);
    }

    public static Text getOptionLabelText() {
      return Text.translatable("armorstands.move.mode");
    }
  }
}
