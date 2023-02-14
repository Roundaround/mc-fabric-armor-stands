package me.roundaround.armorstands.client.gui.screen;

import java.util.Arrays;
import java.util.Optional;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.client.gui.ArmorStandState;
import me.roundaround.armorstands.client.gui.HasArmorStandState;
import me.roundaround.armorstands.client.gui.MessageRenderer;
import me.roundaround.armorstands.client.gui.widget.NavigationButton;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.mixin.InGameHudAccessor;
import me.roundaround.armorstands.mixin.KeyBindingAccessor;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
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
        ClientNetworking.sendUndoPacket(this.state.getArmorStand(), Screen.hasShiftDown());
        return true;
      case GLFW.GLFW_KEY_C:
        if (!supportsUndoRedo() || !Screen.hasControlDown()) {
          break;
        }
        playClickSound();
        messageRenderer.addMessage(MessageRenderer.TEXT_COPY);
        ClientNetworking.sendUtilityActionPacket(this.state.getArmorStand(), UtilityAction.COPY);
        return true;
      case GLFW.GLFW_KEY_V:
        if (!supportsUndoRedo() || !Screen.hasControlDown()) {
          break;
        }
        playClickSound();
        messageRenderer.addMessage(MessageRenderer.TEXT_PASTE);
        ClientNetworking.sendUtilityActionPacket(this.state.getArmorStand(), UtilityAction.PASTE);
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
    int index = 0;
    int totalWidth = 5 * NavigationButton.WIDTH + 4 * ICON_BUTTON_SPACING;

    int x = (width - totalWidth) / 2 - 2 * ICON_BUTTON_SPACING;
    int y = height - PADDING - NavigationButton.HEIGHT;

    if (this instanceof ArmorStandUtilitiesScreen) {
      addDrawable(new NavigationButton<>(
          this.client,
          this,
          x,
          y,
          ArmorStandUtilitiesScreen.TITLE,
          index));
    } else {
      addDrawableChild(new NavigationButton<>(
          this.client,
          this,
          x,
          y,
          ArmorStandUtilitiesScreen.TITLE,
          ArmorStandUtilitiesScreen::new,
          index));
    }

    index++;
    x += ICON_BUTTON_SPACING + NavigationButton.WIDTH;

    if (this instanceof ArmorStandMoveScreen) {
      addDrawable(new NavigationButton<>(
          this.client,
          this,
          x,
          y,
          ArmorStandMoveScreen.TITLE,
          index));
    } else {
      addDrawableChild(new NavigationButton<>(
          this.client,
          this,
          x,
          y,
          ArmorStandMoveScreen.TITLE,
          ArmorStandMoveScreen::new,
          index));
    }

    index++;
    x += ICON_BUTTON_SPACING + NavigationButton.WIDTH;

    if (this instanceof ArmorStandRotateScreen) {
      addDrawable(new NavigationButton<>(
          this.client,
          this,
          x,
          y,
          ArmorStandRotateScreen.TITLE,
          index));
    } else {
      addDrawableChild(new NavigationButton<>(
          this.client,
          this,
          x,
          y,
          ArmorStandRotateScreen.TITLE,
          ArmorStandRotateScreen::new,
          index));
    }

    index++;
    x += ICON_BUTTON_SPACING + NavigationButton.WIDTH;

    if (this instanceof ArmorStandPoseScreen) {
      addDrawable(new NavigationButton<>(
          this.client,
          this,
          x,
          y,
          ArmorStandPoseScreen.TITLE,
          index));
    } else {
      addDrawableChild(new NavigationButton<>(
          this.client,
          this,
          x,
          y,
          ArmorStandPoseScreen.TITLE,
          ArmorStandPoseScreen::new,
          index));
    }

    index++;
    x += ICON_BUTTON_SPACING + NavigationButton.WIDTH;

    addDrawableChild(new NavigationButton<>(
        this.client,
        this,
        x,
        y,
        ArmorStandInventoryScreen.TITLE,
        (button, state) -> {
          int syncId = state.getSyncId();
          PlayerInventory playerInventory = this.client.player.getInventory();
          ArmorStandScreenHandler screenHandler = new ArmorStandScreenHandler(
              syncId,
              playerInventory,
              state.getArmorStand());

          this.client.player.currentScreenHandler = screenHandler;
          this.client.setScreen(new ArmorStandInventoryScreen(
              screenHandler,
              playerInventory,
              state));
        },
        index));
  }

  protected void playClickSound() {
    this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f));
  }
}
