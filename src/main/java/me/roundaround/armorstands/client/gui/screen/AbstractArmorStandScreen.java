package me.roundaround.armorstands.client.gui.screen;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.client.gui.ArmorStandState;
import me.roundaround.armorstands.client.gui.HasArmorStandState;
import me.roundaround.armorstands.client.gui.MessageRenderer;
import me.roundaround.armorstands.client.gui.page.ArmorStandPage;
import me.roundaround.armorstands.client.gui.widget.PageSelectButtonWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.mixin.InGameHudAccessor;
import me.roundaround.armorstands.mixin.KeyBindingAccessor;
import me.roundaround.armorstands.network.UtilityAction;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public abstract class AbstractArmorStandScreen
    extends Screen
    implements HasArmorStandState {
  protected static final int PADDING = 1;
  protected static final int ICON_BUTTON_SPACING = 0;

  protected final ArmorStandState state;
  protected final MessageRenderer messageRenderer;

  protected AbstractArmorStandScreen(Text title, ArmorStandState state) {
    super(title);
    this.state = state;
    messageRenderer = new MessageRenderer(this);
  }

  protected abstract boolean supportsUndoRedo();

  @Override
  public ArmorStandState getState() {
    return this.state;
  }

  @Override
  public boolean shouldPause() {
    return false;
  }

  @Override
  public void tick() {
    // Block any inputs bound to shift so that this screen gets exclusive use
    Arrays.stream(this.client.options.allKeys)
        .filter((key) -> {
          return key.matchesKey(GLFW.GLFW_KEY_LEFT_SHIFT, 0) || key.matchesKey(GLFW.GLFW_KEY_RIGHT_SHIFT, 0);
        })
        .map((key) -> (KeyBindingAccessor) key)
        .forEach(KeyBindingAccessor::invokeReset);

    ((InGameHudAccessor) this.client.inGameHud).invokeUpdateVignetteDarkness(this.client.getCameraEntity());

    messageRenderer.tick();
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    int adjustedMouseX = this.state.isCursorLocked() ? -1 : mouseX;
    int adjustedMouseY = this.state.isCursorLocked() ? -1 : mouseY;

    RenderSystem.enableBlend();
    ((InGameHudAccessor) this.client.inGameHud).invokeRenderVignetteOverlay(this.client.getCameraEntity());
    super.render(matrixStack, adjustedMouseX, adjustedMouseY, delta);

    this.messageRenderer.render(matrixStack);
  }

  @Override
  public void mouseMoved(double mouseX, double mouseY) {
    if (this.state.isCursorLocked()) {
      return;
    }
    super.mouseMoved(mouseX, mouseY);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (this.state.isCursorLocked()) {
      return false;
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    if (this.state.isCursorLocked()) {
      return false;
    }
    return super.mouseReleased(mouseX, mouseY, button);
  }

  @Override
  public Optional<Element> hoveredElement(double mouseX, double mouseY) {
    if (this.state.isCursorLocked()) {
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

    if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
      close();
      return true;
    }

    switch (keyCode) {
      case GLFW.GLFW_KEY_LEFT_ALT:
      case GLFW.GLFW_KEY_RIGHT_ALT:
        this.state.lockCursor();
        return true;
      case GLFW.GLFW_KEY_Z:
        if (!supportsUndoRedo() || !Screen.hasControlDown()) {
          break;
        }
        playClickSound();
        messageRenderer.addMessage(Screen.hasShiftDown() ? MessageRenderer.TEXT_REDO : MessageRenderer.TEXT_UNDO);
        ClientNetworking.sendUndoPacket(Screen.hasShiftDown());
        return true;
      case GLFW.GLFW_KEY_C:
        if (!supportsUndoRedo() || !Screen.hasControlDown()) {
          break;
        }
        playClickSound();
        messageRenderer.addMessage(MessageRenderer.TEXT_COPY);
        ClientNetworking.sendUtilityActionPacket(UtilityAction.COPY);
        return true;
      case GLFW.GLFW_KEY_V:
        if (!supportsUndoRedo() || !Screen.hasControlDown()) {
          break;
        }
        playClickSound();
        messageRenderer.addMessage(MessageRenderer.TEXT_PASTE);
        ClientNetworking.sendUtilityActionPacket(UtilityAction.PASTE);
        return true;
    }

    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
    if (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT) {
      this.state.unlockCursor();
      return true;
    }

    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  protected void initNavigationButtons() {
    // TODO: Remove reference to pages when the button component is converted
    List<ArmorStandPage> pages = List.of(null, null, null, null, null);

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
    }
  }

  protected void playClickSound() {
    this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f));
  }
}
