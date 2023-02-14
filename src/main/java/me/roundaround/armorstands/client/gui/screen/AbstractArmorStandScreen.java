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
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class AbstractArmorStandScreen
    extends Screen
    implements HasArmorStandState {
  protected static final int PADDING = 1;
  protected static final int ICON_BUTTON_SPACING = 0;
  protected static final Identifier WIDGETS_TEXTURE = new Identifier(
      Identifier.DEFAULT_NAMESPACE,
      "textures/gui/widgets.png");

  protected final ArmorStandState state;
  protected final MessageRenderer messageRenderer;

  protected NavigationButton<?> activeButton;

  private Text currentTooltip;

  protected AbstractArmorStandScreen(Text title, ArmorStandState state) {
    super(title);
    this.passEvents = true;
    this.state = state;
    this.messageRenderer = new MessageRenderer(this);
  }

  protected boolean supportsUndoRedo() {
    return false;
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

    renderActivePageButtonHighlight(matrixStack);

    if (this.currentTooltip != null) {
      super.renderTooltip(matrixStack, this.currentTooltip, adjustedMouseX, adjustedMouseY);
      this.currentTooltip = null;
    }

    this.messageRenderer.render(matrixStack);
  }

  @Override
  public void renderTooltip(MatrixStack matrixStack, Text text, int mouseX, int mouseY) {
    this.currentTooltip = text;
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
