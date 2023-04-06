package me.roundaround.armorstands.client.gui.widget;

import me.roundaround.armorstands.client.ArmorStandsClientMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class HelpButtonWidget extends IconButtonWidget {
  private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

  public HelpButtonWidget(int x, int y) {
    super(x, y, 16, buildTooltipText(), (button) -> {
    });
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    return false;
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    return false;
  }

  private static Text buildTooltipText() {
    String alt = Text.translatable("armorstands.help.alt").getString();
    String inventory = CLIENT.options.inventoryKey.getBoundKeyLocalizedText().getString();
    String left = InputUtil.fromKeyCode(GLFW.GLFW_KEY_LEFT, 0).getLocalizedText().getString();
    String right = InputUtil.fromKeyCode(GLFW.GLFW_KEY_RIGHT, 0).getLocalizedText().getString();
    String highlight =
        ArmorStandsClientMod.highlightArmorStandKeyBinding.getBoundKeyLocalizedText().getString();
    String control =
        Text.translatable("armorstands.help." + (MinecraftClient.IS_SYSTEM_MAC ? "cmd" : "ctrl"))
            .getString();
    String z = InputUtil.fromKeyCode(GLFW.GLFW_KEY_Z, 0).getLocalizedText().getString();
    String shift = Text.translatable("armorstands.help.shift").getString();
    String c = InputUtil.fromKeyCode(GLFW.GLFW_KEY_C, 0).getLocalizedText().getString();
    String v = InputUtil.fromKeyCode(GLFW.GLFW_KEY_V, 0).getLocalizedText().getString();

    return Text.translatable("armorstands.help",
        alt,
        inventory,
        left,
        right,
        highlight,
        control,
        z,
        control,
        shift,
        z,
        control,
        c,
        control,
        v);
  }
}
