package me.roundaround.armorstands.client.gui.screen;

import java.util.ArrayList;
import java.util.Optional;

import org.lwjgl.glfw.GLFW;

import me.roundaround.armorstands.client.ArmorStandsClientMod;
import me.roundaround.armorstands.client.gui.page.AbstractArmorStandPage;
import me.roundaround.armorstands.client.gui.page.ArmorStandFlagsPage;
import me.roundaround.armorstands.client.gui.page.ArmorStandInventoryPage;
import me.roundaround.armorstands.client.gui.widget.PageChangeButtonWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.mixin.MouseAccessor;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ArmorStandScreen extends HandledScreen<ArmorStandScreenHandler> {
  protected static final Identifier RESOURCE_PACKS_TEXTURE = new Identifier(
      Identifier.DEFAULT_NAMESPACE,
      "textures/gui/resource_packs.png");
  protected static final int BUTTON_WIDTH_MEDIUM = 100;
  protected static final int BUTTON_HEIGHT = 20;
  protected static final int PADDING = 4;

  protected final ArmorStandEntity armorStand;
  protected final ArrayList<AbstractArmorStandPage> pages = new ArrayList<>();

  protected AbstractArmorStandPage page;
  protected int pageNum = 0;
  protected boolean cursorLocked = false;

  public ArmorStandScreen(
      ArmorStandScreenHandler screenHandler,
      PlayerInventory playerInventory,
      ArmorStandEntity armorStand) {
    super(screenHandler, playerInventory, Text.literal(""));
    passEvents = true;

    this.armorStand = armorStand;

    pages.add(new ArmorStandFlagsPage(this));
    pages.add(new ArmorStandInventoryPage(this));

    setPage(0, false);
  }

  @Override
  public boolean shouldPause() {
    return false;
  }

  @Override
  protected void init() {
    super.init();

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

    page.init();
  }

  @Override
  public <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
    return super.addDrawableChild(drawableElement);
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    int adjustedMouseX = cursorLocked ? -1 : mouseX;
    int adjustedMouseY = cursorLocked ? -1 : mouseY;

    super.render(matrixStack, adjustedMouseX, adjustedMouseY, delta);
  }

  @Override
  protected void drawBackground(MatrixStack matrixStack, float delta, int mouseX, int mouseY) {
    page.drawBackground(matrixStack, mouseX, mouseY, delta);

    Text text = Text.literal("Page " + (pageNum + 1) + " of " + pages.size());
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
  protected void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
  }

  @Override
  protected void handledScreenTick() {
    page.tick();
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
    // Allow jump to pass through
    if (client.options.jumpKey.matchesKey(keyCode, scanCode)) {
      return false;
    }

    if (keyCode == GLFW.GLFW_KEY_ESCAPE && shouldCloseOnEsc()) {
      close();
      return true;
    }

    switch (keyCode) {
      case GLFW.GLFW_KEY_LEFT_ALT:
      case GLFW.GLFW_KEY_RIGHT_ALT:
        lockCursor();
        return true;
      case GLFW.GLFW_KEY_LEFT:
        previousPage();
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f));
        return true;
      case GLFW.GLFW_KEY_RIGHT:
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f));
        nextPage();
        return true;
      case GLFW.GLFW_KEY_TAB:
        boolean forward = !Screen.hasShiftDown();
        if (!changeFocus(forward)) {
          changeFocus(forward);
        }
        return false;
    }

    return getFocused() != null
        && getFocused().keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
    if (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT) {
      unlockCursor();
      return true;
    }

    return getFocused() != null
        && getFocused().keyReleased(keyCode, scanCode, modifiers);
  }

  public void setPage(int pageNum) {
    setPage(pageNum, true);
  }

  public void setPage(int pageNum, boolean rerunInit) {
    this.pageNum = (pageNum + pages.size()) % pages.size();
    page = pages.get(this.pageNum);
    handler.populateSlots(page.usesSlots());
    ClientNetworking.sendPopulateSlotsPacket(page.usesSlots());
    clearAndInit();
  }

  public boolean isCursorLocked() {
    return cursorLocked;
  }

  public void previousPage() {
    setPage(pageNum - 1);
  }

  public void nextPage() {
    setPage(pageNum + 1);
  }

  public boolean shouldHighlight(Entity entity) {
    return ArmorStandsClientMod.highlightArmorStandKeyBinding.isPressed() && entity == armorStand;
  }

  public ArmorStandEntity getArmorStand() {
    return armorStand;
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
