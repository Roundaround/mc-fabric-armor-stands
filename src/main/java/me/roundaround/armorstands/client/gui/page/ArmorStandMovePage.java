package me.roundaround.armorstands.client.gui.page;

import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import me.roundaround.armorstands.client.network.ClientNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

public class ArmorStandMovePage extends AbstractArmorStandPage {
  public ArmorStandMovePage(MinecraftClient client, ArmorStandScreen screen) {
    super(client, screen);
  }

  @Override
  public void init() {
    screen.addDrawableChild(new ButtonWidget(
        4,
        4,
        100,
        20,
        Text.literal("+8X"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(screen.getArmorStand(), Direction.EAST, 8);
        }));

    screen.addDrawableChild(new ButtonWidget(
        4,
        26,
        100,
        20,
        Text.literal("-8X"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(screen.getArmorStand(), Direction.WEST, 8);
        }));

    screen.addDrawableChild(new ButtonWidget(
        4,
        48,
        100,
        20,
        Text.literal("+3Y"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(screen.getArmorStand(), Direction.UP, 3);
        }));

    screen.addDrawableChild(new ButtonWidget(
        4,
        70,
        100,
        20,
        Text.literal("-3Y"),
        (button) -> {
          ClientNetworking.sendAdjustPosPacket(screen.getArmorStand(), Direction.DOWN, 3);
        }));

    screen.addDrawableChild(new ButtonWidget(
        4,
        92,
        100,
        20,
        Text.literal("Rotate 15deg"),
        (button) -> {
          ClientNetworking.sendAdjustYawPacket(screen.getArmorStand(), 15);
        }));

    screen.addDrawableChild(new ButtonWidget(
        4,
        114,
        100,
        20,
        Text.literal("Rotate 90deg"),
        (button) -> {
          ClientNetworking.sendAdjustYawPacket(screen.getArmorStand(), 90);
        }));
  }
}
