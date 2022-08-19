package me.roundaround.armorstands.client.gui.screen;

import java.util.ArrayList;
import java.util.Optional;

import org.lwjgl.glfw.GLFW;

import me.roundaround.armorstands.client.gui.widget.PageChangeButtonWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.mixin.MouseAccessor;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class AbstractArmorStandScreen extends Screen {
  protected static final Identifier RESOURCE_PACKS_TEXTURE = new Identifier(
      Identifier.DEFAULT_NAMESPACE,
      "textures/gui/resource_packs.png");
  protected static final int BUTTON_WIDTH_MEDIUM = 100;
  protected static final int BUTTON_HEIGHT = 20;
  protected static final int PADDING = 4;

  private static final ArrayList<AbstractArmorStandScreen> SCREENS = new ArrayList<>();

  protected final ArmorStandEntity armorStand;
  protected final int index;
  protected boolean cursorLocked = false;

  protected AbstractArmorStandScreen(ArmorStandEntity armorStand, int index, Text title) {
    super(title);
    this.armorStand = armorStand;
    this.index = index;

    passEvents = true;
  }

  public static Screen initAndGetStartingScreen(ArmorStandEntity armorStand) {
    SCREENS.clear();

    SCREENS.add(new ArmorStandCoreScreen(armorStand, SCREENS.size()));
    SCREENS.add(new ArmorStandInventoryScreen(armorStand, SCREENS.size()));

    return SCREENS.get(0);
  }

  @Override
  public boolean shouldPause() {
    return false;
  }

  @Override
  public void removed() {
    ClientNetworking.sendCancelIdentifyPacket(armorStand);
    super.removed();
  }

  @Override
  protected void init() {
    addDrawableChild(new PageChangeButtonWidget(
        this,
        width / 2 - 40 - PageChangeButtonWidget.WIDTH,
        height - 4 - PageChangeButtonWidget.HEIGHT,
        false));
    addDrawableChild(new PageChangeButtonWidget(
        this,
        width / 2 + 40,
        height - 4 - PageChangeButtonWidget.HEIGHT,
        true));
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    int adjustedMouseX = cursorLocked ? -1 : mouseX;
    int adjustedMouseY = cursorLocked ? -1 : mouseY;

    renderBackground(matrixStack, adjustedMouseX, adjustedMouseY, delta);
    super.render(matrixStack, adjustedMouseX, adjustedMouseY, delta);
    renderContent(matrixStack, adjustedMouseX, adjustedMouseY, delta);
  }

  protected void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
  }

  protected void renderContent(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    Text text = Text.literal("Page " + (index + 1) + " of " + SCREENS.size());
    int textWidth = textRenderer.getWidth(text);

    fill(
        matrixStack,
        (width - textWidth) / 2 - 2,
        height - 4 - 2 - (PageChangeButtonWidget.HEIGHT + 10) / 2,
        (width + textWidth) / 2 + 3,
        height - 4 - (PageChangeButtonWidget.HEIGHT - 10) / 2,
        client.options.getTextBackgroundColor(0.8f));

    drawCenteredText(
        matrixStack,
        textRenderer,
        text,
        width / 2,
        height - 4 - (PageChangeButtonWidget.HEIGHT + 10) / 2,
        0xFFFFFFFF);
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

    // Allow jump to pass through
    if (client.options.jumpKey.matchesKey(keyCode, scanCode)) {
      return false;
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

  public void previousPage() {
    client.setScreen(SCREENS.get((index + SCREENS.size() - 1) % SCREENS.size()));
  }

  public void nextPage() {
    client.setScreen(SCREENS.get((index + 1) % SCREENS.size()));
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
