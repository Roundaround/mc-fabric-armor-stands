package me.roundaround.armorstands.client.gui.widget;

import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen.ScreenFactory;
import me.roundaround.armorstands.client.util.LastUsedScreen;
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
      ScreenFactory<?> factory) {
    return new NavigationButtonWidget<>(
        client,
        parent,
        x,
        y,
        factory.tooltip,
        (button, handler, armorStand) -> {
          if (factory.constructor == null) {
            return;
          }
          client.currentScreen = null;
          AbstractArmorStandScreen nextScreen = factory.constructor.accept(handler, armorStand);
          LastUsedScreen.set(nextScreen);
          client.setScreen(nextScreen);
        },
        factory.constructor != null,
        factory.uIndex);
  }

  @Override
  public boolean isHovered() {
    return this.clickable && super.isHovered();
  }

  @FunctionalInterface
  public static interface PressAction<P extends AbstractArmorStandScreen, T extends AbstractArmorStandScreen> {
    public void accept(
        NavigationButtonWidget<P, T> button,
        ArmorStandScreenHandler handler,
        ArmorStandEntity armorStand);
  }
}
