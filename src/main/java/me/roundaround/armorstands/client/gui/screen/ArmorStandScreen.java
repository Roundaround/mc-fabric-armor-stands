package me.roundaround.armorstands.client.gui.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.client.ArmorStandsClientMod;
import me.roundaround.armorstands.client.gui.page.ArmorStandInventoryPage;
import me.roundaround.armorstands.client.gui.page.ArmorStandMovePage;
import me.roundaround.armorstands.client.gui.page.ArmorStandPage;
import me.roundaround.armorstands.client.gui.page.ArmorStandPosePage;
import me.roundaround.armorstands.client.gui.page.ArmorStandRotatePage;
import me.roundaround.armorstands.client.gui.page.ArmorStandUtilitiesPage;
import me.roundaround.armorstands.client.gui.widget.DrawableBuilder;
import me.roundaround.armorstands.client.gui.widget.PageSelectButtonWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.mixin.InGameHudAccessor;
import me.roundaround.armorstands.mixin.KeyBindingAccessor;
import me.roundaround.armorstands.mixin.MouseAccessor;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class ArmorStandScreen extends HandledScreen<ArmorStandScreenHandler> implements HasArmorStandOverlay {
  protected static final int BUTTON_WIDTH_MEDIUM = 100;
  protected static final int BUTTON_HEIGHT = 20;
  protected static final int PADDING = 4;
  protected static final int ICON_BUTTON_SPACING = 2;

  protected final ArmorStandEntity armorStand;
  protected final ArrayList<ArmorStandPage> pages = new ArrayList<>();
  protected final ArrayList<PageSelectButtonWidget> pageSelectButtons = new ArrayList<>();

  protected ArmorStandPage page;
  protected int pageNum = 0;
  protected boolean cursorLocked = false;

  public ArmorStandScreen(
      ArmorStandScreenHandler screenHandler,
      PlayerInventory playerInventory,
      ArmorStandEntity armorStand) {
    super(screenHandler, playerInventory, Text.literal(""));
    passEvents = true;
    this.armorStand = armorStand;
  }

  @Override
  public boolean shouldPause() {
    return false;
  }

  @Override
  protected void clearAndInit() {
    pageSelectButtons.clear();
    super.clearAndInit();
  }

  @Override
  protected void init() {
    setFocused(null);

    setUpPages();

    super.init();

    page.preInit();

    for (int i = 0; i < pages.size(); i++) {
      ArmorStandPage page = pages.get(i);
      int totalWidth = pages.size() * PageSelectButtonWidget.WIDTH
          + (pages.size() - 1) * ICON_BUTTON_SPACING;
      int x = (width - totalWidth) / 2
          + i * PageSelectButtonWidget.HEIGHT
          + (i - 1) * ICON_BUTTON_SPACING;
      int y = height - PADDING - PageSelectButtonWidget.HEIGHT;

      PageSelectButtonWidget button = new PageSelectButtonWidget(x, y, this, page, i);
      if (button.isActivePage()) {
        addDrawable(button);
      } else {
        addDrawableChild(button);
      }
      pageSelectButtons.add(button);
    }

    page.postInit();
  }

  @Override
  public <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
    return super.addDrawableChild(drawableElement);
  }

  @Override
  public <T extends Drawable> T addDrawable(T drawable) {
    return super.addDrawable(drawable);
  }

  public <T extends Drawable, S extends DrawableBuilder<T>> T addDrawable(S builder) {
    return addDrawable(builder.build());
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    int adjustedMouseX = cursorLocked ? -1 : mouseX;
    int adjustedMouseY = cursorLocked ? -1 : mouseY;

    RenderSystem.enableBlend();
    ((InGameHudAccessor) client.inGameHud).invokeRenderVignetteOverlay(client.getCameraEntity());
    super.render(matrixStack, adjustedMouseX, adjustedMouseY, delta);
  }

  @Override
  protected void drawBackground(MatrixStack matrixStack, float delta, int mouseX, int mouseY) {
    page.drawBackground(matrixStack, mouseX, mouseY, delta);
  }

  @Override
  protected void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
  }

  @Override
  public void renderArmorStandOverlay(
      ArmorStandEntity armorStand,
      float tickDelta,
      MatrixStack matrixStack,
      VertexConsumerProvider vertexConsumerProvider,
      int light) {
    if (armorStand != this.armorStand) {
      return;
    }

    page.renderArmorStandOverlay(armorStand, tickDelta, matrixStack, vertexConsumerProvider, light);
  }

  @Override
  protected void handledScreenTick() {
    // Block any inputs bound to shift so that this screen gets exclusive use
    Arrays.stream(client.options.allKeys)
        .filter((key) -> {
          return key.matchesKey(GLFW.GLFW_KEY_LEFT_SHIFT, 0) || key.matchesKey(GLFW.GLFW_KEY_RIGHT_SHIFT, 0);
        })
        .map((key) -> (KeyBindingAccessor) key)
        .forEach(KeyBindingAccessor::invokeReset);

    ((InGameHudAccessor) client.inGameHud).invokeUpdateVignetteDarkness(client.getCameraEntity());

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
    Element focused = getFocused();
    boolean result = super.mouseClicked(mouseX, mouseY, button);
    setFocused(focused);
    return result;
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
    // Allow jump to pass through without pressing buttons
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
        playClickSound();
        previousPage();
        return true;
      case GLFW.GLFW_KEY_RIGHT:
        playClickSound();
        nextPage();
        return true;
      case GLFW.GLFW_KEY_TAB:
        boolean forward = !Screen.hasShiftDown();
        if (!changeFocus(forward)) {
          changeFocus(forward);
        }
        return false;
      case GLFW.GLFW_KEY_Z:
        if (!Screen.hasControlDown()) {
          break;
        }
        playClickSound();
        ClientNetworking.sendUndoPacket(Screen.hasShiftDown());
        return true;
      case GLFW.GLFW_KEY_C:
        if (!Screen.hasControlDown()) {
          break;
        }
        playClickSound();
        ClientNetworking.sendUtilityActionPacket(UtilityAction.COPY);
        return true;
      case GLFW.GLFW_KEY_V:
        if (!Screen.hasControlDown()) {
          break;
        }
        playClickSound();
        ClientNetworking.sendUtilityActionPacket(UtilityAction.PASTE);
        return true;
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

  public void previousPage() {
    setPage(pageNum - 1);
  }

  public void nextPage() {
    setPage(pageNum + 1);
  }

  public void setPage(int pageNum) {
    setPage(pageNum, true);
  }

  public ArmorStandPage getCurrentPage() {
    return page;
  }

  public void setPage(int pageNum, boolean rerunInit) {
    this.pageNum = (pageNum + pages.size()) % pages.size();
    page = pages.get(this.pageNum);
    handler.populateSlots(page.usesSlots());
    ClientNetworking.sendPopulateSlotsPacket(page.usesSlots());

    if (rerunInit) {
      clearAndInit();
      setFocused(null);
    }
  }

  public boolean isCursorLocked() {
    return cursorLocked;
  }

  public boolean shouldHighlight(Entity entity) {
    return ArmorStandsClientMod.highlightArmorStandKeyBinding.isPressed() && entity == armorStand;
  }

  public ArmorStandEntity getArmorStand() {
    return armorStand;
  }

  protected void playClickSound() {
    client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f));
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

  protected void setUpPages() {
    if (!pages.isEmpty()) {
      return;
    }

    pages.add(new ArmorStandUtilitiesPage(client, this));
    pages.add(new ArmorStandMovePage(client, this));
    pages.add(new ArmorStandRotatePage(client, this));
    pages.add(new ArmorStandPosePage(client, this));
    pages.add(new ArmorStandInventoryPage(client, this));

    setPage(0, false);
  }

  protected void drawCenteredText(MatrixStack matrixStack, Text text, int centerX, int y, int color) {
    client.textRenderer.draw(matrixStack, text, centerX - textRenderer.getWidth(text) / 2f, y, color);
  }
}
