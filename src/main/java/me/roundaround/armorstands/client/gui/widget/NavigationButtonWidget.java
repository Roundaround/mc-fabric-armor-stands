package me.roundaround.armorstands.client.gui.widget;

import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.network.packet.c2s.RequestScreenPacket;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class NavigationButtonWidget<P extends AbstractArmorStandScreen, T extends AbstractArmorStandScreen>
    extends IconButtonWidget<P> {
  private boolean clickable;

  @SuppressWarnings("unchecked")
  private NavigationButtonWidget(
      MinecraftClient client,
      P parent,
      int x,
      int y,
      Text tooltip,
      PressAction<P, T> onPress,
      boolean clickable,
      int uIndex) {
    super(
        client,
        parent,
        x,
        y,
        uIndex,
        tooltip,
        (button) -> {
          onPress.accept((NavigationButtonWidget<P, T>) button, parent.getScreenHandler(), parent.getArmorStand());
        });

    this.clickable = clickable;
    this.active = clickable;
  }

  public static NavigationButtonWidget<?, ?> create(
      MinecraftClient client,
      AbstractArmorStandScreen parent,
      int x,
      int y,
      ScreenType screenType) {
    return new NavigationButtonWidget<>(
        client,
        parent,
        x,
        y,
        screenType.getDisplayName(),
        (button, handler, armorStand) -> {
          if (parent.getScreenType() == screenType) {
            return;
          }

          RequestScreenPacket.sendToServer(armorStand, screenType);
        },
        parent.getScreenType() != screenType,
        screenType.getUIndex());
  }

  @Override
  public boolean isHovered() {
    return this.clickable && super.isHovered();
  }

  public boolean isMouseOverIgnoreState(double mouseX, double mouseY) {
    return mouseX >= this.x && mouseY >= this.y
        && mouseX < (this.x + this.width) && mouseY < (this.y + this.height);
  }

  @FunctionalInterface
  public static interface PressAction<P extends AbstractArmorStandScreen, T extends AbstractArmorStandScreen> {
    public void accept(
        NavigationButtonWidget<P, T> button,
        ArmorStandScreenHandler handler,
        ArmorStandEntity armorStand);
  }
}
