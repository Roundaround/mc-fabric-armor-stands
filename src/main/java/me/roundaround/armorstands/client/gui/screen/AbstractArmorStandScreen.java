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
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class AbstractArmorStandScreen
    extends HandledScreen<ArmorStandScreenHandler>
    implements HasArmorStandState {
  protected static final int PADDING = 1;
  protected static final int ICON_BUTTON_SPACING = 0;
  protected static final Identifier WIDGETS_TEXTURE = new Identifier(
      Identifier.DEFAULT_NAMESPACE,
      "textures/gui/widgets.png");

  protected final ArmorStandState state;
  protected final MessageRenderer messageRenderer;

  protected NavigationButton<?> activeButton;
  protected boolean cursorLocked = false;
  protected boolean supportsUndoRedo = false;
  protected boolean renderInventories = false;

  protected AbstractArmorStandScreen(
      ArmorStandScreenHandler handler,
      boolean utilizesInventory,
      Text title,
      ArmorStandState state) {
    super(handler, handler.getPlayerInventory(), title);

    this.state = state;
    this.messageRenderer = new MessageRenderer(this);

    this.passEvents = true;

    handler.clearSlots();
    if (utilizesInventory) {
      handler.populateSlots();
    }

    ClientNetworking.sendPopulateSlotsPacket(utilizesInventory);
  }

  @Override
  public ArmorStandState getState() {
    return this.state;
  }

  @Override
  public boolean shouldPause() {
    return false;
  }

  @Override
  protected void handledScreenTick() {
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
    int adjustedMouseX = cursorLocked ? -1 : mouseX;
    int adjustedMouseY = cursorLocked ? -1 : mouseY;

    RenderSystem.enableBlend();
    ((InGameHudAccessor) this.client.inGameHud).invokeRenderVignetteOverlay(this.client.getCameraEntity());
    super.render(matrixStack, adjustedMouseX, adjustedMouseY, delta);

    renderActivePageButtonHighlight(matrixStack);

    this.messageRenderer.render(matrixStack);
  }

  @Override
  protected void drawBackground(MatrixStack matrixStack, float delta, int mouseX, int mouseY) {
  }

  @Override
  protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
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

    if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
      close();
      return true;
    }

    switch (keyCode) {
      case GLFW.GLFW_KEY_LEFT_ALT:
      case GLFW.GLFW_KEY_RIGHT_ALT:
        this.state.lockCursor();
        return true;
      case GLFW.GLFW_KEY_TAB:
        boolean forward = !Screen.hasShiftDown();
        if (!changeFocus(forward)) {
          changeFocus(forward);
        }
        return false;
      case GLFW.GLFW_KEY_Z:
        if (!this.supportsUndoRedo || !Screen.hasControlDown()) {
          break;
        }
        playClickSound();
        messageRenderer.addMessage(Screen.hasShiftDown() ? MessageRenderer.TEXT_REDO : MessageRenderer.TEXT_UNDO);
        ClientNetworking.sendUndoPacket(Screen.hasShiftDown());
        return true;
      case GLFW.GLFW_KEY_C:
        if (!this.supportsUndoRedo || !Screen.hasControlDown()) {
          break;
        }
        playClickSound();
        messageRenderer.addMessage(MessageRenderer.TEXT_COPY);
        ClientNetworking.sendUtilityActionPacket(UtilityAction.COPY);
        return true;
      case GLFW.GLFW_KEY_V:
        if (!this.supportsUndoRedo || !Screen.hasControlDown()) {
          break;
        }
        playClickSound();
        messageRenderer.addMessage(MessageRenderer.TEXT_PASTE);
        ClientNetworking.sendUtilityActionPacket(UtilityAction.PASTE);
        return true;
    }

    return getFocused() != null
        && getFocused().keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
    if (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT) {
      this.state.unlockCursor();
      return true;
    }

    return getFocused() != null
        && getFocused().keyReleased(keyCode, scanCode, modifiers);
  }

  protected void initNavigationButtons() {
    int index = 0;
    int totalWidth = 5 * NavigationButton.WIDTH + 4 * ICON_BUTTON_SPACING;

    int x = (width - totalWidth) / 2 - 2 * ICON_BUTTON_SPACING;
    int y = height - PADDING - NavigationButton.HEIGHT;

    if (this instanceof ArmorStandUtilitiesScreen) {
      this.activeButton = new NavigationButton<>(
          this.client,
          this,
          x,
          y,
          ArmorStandUtilitiesScreen.TITLE,
          index);
      addDrawable(this.activeButton);
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
      this.activeButton = new NavigationButton<>(
          this.client,
          this,
          x,
          y,
          ArmorStandMoveScreen.TITLE,
          index);
      addDrawable(this.activeButton);
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
      this.activeButton = new NavigationButton<>(
          this.client,
          this,
          x,
          y,
          ArmorStandRotateScreen.TITLE,
          index);
      addDrawable(this.activeButton);
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
      this.activeButton = new NavigationButton<>(
          this.client,
          this,
          x,
          y,
          ArmorStandPoseScreen.TITLE,
          index);
      addDrawable(this.activeButton);
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

    if (this instanceof ArmorStandInventoryScreen) {
      this.activeButton = new NavigationButton<>(
          this.client,
          this,
          x,
          y,
          ArmorStandInventoryScreen.TITLE,
          index);
      addDrawable(this.activeButton);
    } else {
      addDrawableChild(new NavigationButton<>(
          this.client,
          this,
          x,
          y,
          ArmorStandInventoryScreen.TITLE,
          ArmorStandInventoryScreen::new,
          index));
    }
  }

  protected void renderActivePageButtonHighlight(MatrixStack matrixStack) {
    if (this.activeButton == null) {
      return;
    }

    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();

    matrixStack.push();
    matrixStack.translate(0, 0, 100);
    drawTexture(
        matrixStack,
        this.activeButton.x - 2,
        activeButton.y - 2,
        0,
        22,
        13,
        13);
    drawTexture(matrixStack,
        this.activeButton.x + NavigationButton.WIDTH / 2 + 1,
        activeButton.y - 2,
        12,
        22,
        12,
        13);
    drawTexture(matrixStack,
        this.activeButton.x - 2,
        activeButton.y + NavigationButton.HEIGHT / 2 + 1,
        0,
        34,
        13,
        12);
    drawTexture(matrixStack,
        this.activeButton.x + NavigationButton.WIDTH / 2 + 1,
        this.activeButton.y + NavigationButton.HEIGHT / 2 + 1,
        12,
        34,
        12,
        12);
    matrixStack.pop();
  }

  protected void playClickSound() {
    this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f));
  }
}
