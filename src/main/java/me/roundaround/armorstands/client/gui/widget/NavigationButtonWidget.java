package me.roundaround.armorstands.client.gui.widget;

import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.ScreenType;

public class NavigationButtonWidget extends IconButtonWidget {
  private final ScreenType screenType;
  private final boolean clickable;

  public NavigationButtonWidget(
      AbstractArmorStandScreen parent, int x, int y, ScreenType screenType
  ) {
    super(x, y, screenType.getUIndex(), screenType.getDisplayName(), (button) -> {
      if (parent.getScreenType() == screenType) {
        return;
      }

      ClientNetworking.sendRequestScreenPacket(parent.getArmorStand(), screenType);
    });

    this.screenType = screenType;
    this.clickable = parent.getScreenType() != screenType;
    this.active = clickable;
  }

  public ScreenType getScreenType() {
    return this.screenType;
  }

  public boolean isMouseOverIgnoreState(double mouseX, double mouseY) {
    return mouseX >= this.getX() && mouseY >= this.getY() && mouseX < (this.getX() + this.width) &&
        mouseY < (this.getY() + this.height);
  }

  @Override
  public boolean isHovered() {
    return this.clickable && super.isHovered();
  }
}
