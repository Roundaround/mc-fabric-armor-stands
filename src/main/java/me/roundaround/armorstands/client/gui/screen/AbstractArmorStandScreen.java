package me.roundaround.armorstands.client.gui.screen;

import java.util.Optional;

import org.lwjgl.glfw.GLFW;

import me.roundaround.armorstands.mixin.MouseAccessor;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public abstract class AbstractArmorStandScreen extends Screen {
  protected ArmorStandEntity armorStand;
  protected boolean cursorLocked = false;

  protected AbstractArmorStandScreen(ArmorStandEntity armorStand, Text title) {
    super(title);
    this.armorStand = armorStand;
    passEvents = true;
  }

  @Override
  public boolean shouldPause() {
    return false;
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    int adjustedMouseX = cursorLocked ? -1 : mouseX;
    int adjustedMouseY = cursorLocked ? -1 : mouseY;
    super.render(matrixStack, adjustedMouseX, adjustedMouseY, delta);
  }

  @Override
  public void mouseMoved(double mouseX, double mouseY) {
    if (cursorLocked) {
      return;
    }
    super.mouseMoved(mouseX, mouseY);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (cursorLocked) {
      return false;
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    if (cursorLocked) {
      return false;
    }
    return super.mouseReleased(mouseX, mouseY, button);
  }

  @Override
  public Optional<Element> hoveredElement(double mouseX, double mouseY) {
    if (cursorLocked) {
      return Optional.empty();
    }
    return super.hoveredElement(mouseX, mouseY);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT) {
      lockCursor();
      return true;
    }

    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
    if (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT) {
      unlockCursor();
      return true;
    }

    return super.keyReleased(keyCode, scanCode, modifiers);
  }

  public boolean isCursorLocked() {
    return cursorLocked;
  }

  protected void lockCursor() {
    // TODO: Determine if and how to do mouse release event
    // mouseReleased(client.mouse.getX(), client.mouse.getY(),
    // GLFW.GLFW_MOUSE_BUTTON_LAST);

    cursorLocked = true;
    int x = client.getWindow().getWidth() / 2;
    int y = client.getWindow().getHeight() / 2;
    ((MouseAccessor) client.mouse).setX(x);
    ((MouseAccessor) client.mouse).setY(y);
    InputUtil.setCursorParameters(client.getWindow().getHandle(), InputUtil.GLFW_CURSOR_DISABLED, x, y);
  }

  protected void unlockCursor() {
    cursorLocked = false;
    int x = client.getWindow().getWidth() / 2;
    int y = client.getWindow().getHeight() / 2;
    ((MouseAccessor) client.mouse).setX(x);
    ((MouseAccessor) client.mouse).setY(y);
    InputUtil.setCursorParameters(client.getWindow().getHandle(), InputUtil.GLFW_CURSOR_NORMAL, x, y);
  }
}
