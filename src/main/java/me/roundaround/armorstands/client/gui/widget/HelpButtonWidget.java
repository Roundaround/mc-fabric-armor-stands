package me.roundaround.armorstands.client.gui.widget;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class HelpButtonWidget extends IconButtonWidget<AbstractArmorStandScreen> {
  public HelpButtonWidget(
      MinecraftClient client,
      AbstractArmorStandScreen parent,
      int x,
      int y) {
    super(
        client,
        parent,
        x,
        y,
        16,
        wrapLines(Text.translatable("armorstands.help"), Math.max(200, parent.width / 2)),
        (button) -> {
          FabricLoader.getInstance().getModContainer(ArmorStandsMod.MOD_ID).ifPresent((mod) -> {
            mod.getMetadata().getContact().get("homepage").ifPresent((url) -> {
              // Clear currentScreen before changing to avoid closing the
              // screen handler

              client.currentScreen = null;
              client.setScreen(new ConfirmChatLinkScreen((confirmed) -> {
                if (confirmed) {
                  Util.getOperatingSystem().open(url);
                }
                client.currentScreen = null;
                client.setScreen(parent);
              }, url, false));
            });
          });

        });
  }

  @Override
  public void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
    matrixStack.push();
    matrixStack.translate(0, 0, 100);
    this.parent.renderOrderedTooltip(
        matrixStack,
        getTooltip(),
        this.x,
        this.y + IconButtonWidget.HEIGHT);
    matrixStack.pop();
  }
}
