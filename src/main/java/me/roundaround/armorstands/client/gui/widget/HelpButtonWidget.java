package me.roundaround.armorstands.client.gui.widget;

import org.lwjgl.glfw.GLFW;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.ArmorStandsClientMod;
import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.util.InputUtil;
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
        wrapLines(getTooltip(client), Math.max(200, 3 * parent.width / 4)),
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
        this.x + IconButtonWidget.WIDTH,
        this.y + IconButtonWidget.HEIGHT);
    matrixStack.pop();
  }

  private static Text getTooltip(MinecraftClient client) {
    String alt = Text.translatable("armorstands.help.alt").getString();
    String inventory = client.options.inventoryKey
        .getBoundKeyLocalizedText()
        .getString();
    String left = InputUtil.fromKeyCode(GLFW.GLFW_KEY_LEFT, 0)
        .getLocalizedText()
        .getString();
    String right = InputUtil.fromKeyCode(GLFW.GLFW_KEY_RIGHT, 0)
        .getLocalizedText()
        .getString();
    String highlight = ArmorStandsClientMod.highlightArmorStandKeyBinding
        .getBoundKeyLocalizedText()
        .getString();
    String control = Text.translatable("armorstands.help."
        + (MinecraftClient.IS_SYSTEM_MAC ? "cmd" : "ctrl"))
        .getString();
    String z = InputUtil.fromKeyCode(GLFW.GLFW_KEY_Z, 0)
        .getLocalizedText()
        .getString();
    String shift = Text.translatable("armorstands.help.shift")
        .getString();
    String c = InputUtil.fromKeyCode(GLFW.GLFW_KEY_C, 0)
        .getLocalizedText()
        .getString();
    String v = InputUtil.fromKeyCode(GLFW.GLFW_KEY_V, 0)
        .getLocalizedText()
        .getString();

    return Text.translatable(
        "armorstands.help",
        alt,
        inventory,
        left,
        right,
        highlight,
        control, z,
        control, shift, z,
        control, c,
        control, v);
  }
}
