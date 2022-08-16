package me.roundaround.armorstands.client.gui.screen;

import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.ArmorStandFlag;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class ArmorStandScreen extends Screen {
  private ArmorStandEntity armorStand;

  public ArmorStandScreen(ArmorStandEntity armorStand) {
    super(Text.translatable(""));
    this.armorStand = armorStand;
    passEvents = true;
  }

  @Override
  public boolean shouldPause() {
    return false;
  }

  @Override
  protected void init() {
    addDrawableChild(new ButtonWidget(
        (width - 100) / 2,
        (height - 20) / 2,
        100,
        20,
        Text.literal("Identify"),
        (button) -> {
          ClientNetworking.sendIdentifyStandPacket(armorStand);
        }));

    addDrawableChild(new ButtonWidget(
        (width - 100) / 2,
        (height - 20) / 2 + 24,
        100,
        20,
        Text.literal("Rotate 45 deg"),
        (button) -> {
          ClientNetworking.sendAdjustYawPacket(armorStand, 45);
        }));

    addDrawableChild(new ButtonWidget(
        (width - 100) / 2,
        (height - 20) / 2 + 48,
        100,
        20,
        Text.literal("Toggle base plate"),
        (button) -> {
          ClientNetworking.sendToggleFlagPacket(armorStand, ArmorStandFlag.BASE);
        }));
  }

  public boolean isCursorLocked() {
    return Screen.hasAltDown();
  }
}
