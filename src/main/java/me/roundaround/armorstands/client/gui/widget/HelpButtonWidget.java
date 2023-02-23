package me.roundaround.armorstands.client.gui.widget;

import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

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
          // TODO: Link out to mod page
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
