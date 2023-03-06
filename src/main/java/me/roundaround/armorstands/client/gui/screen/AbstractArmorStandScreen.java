package me.roundaround.armorstands.client.gui.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.client.ArmorStandsClientMod;
import me.roundaround.armorstands.client.gui.MessageRenderer;
import me.roundaround.armorstands.client.gui.MessageRenderer.HasMessageRenderer;
import me.roundaround.armorstands.client.gui.widget.HelpButtonWidget;
import me.roundaround.armorstands.client.gui.widget.IconButtonWidget;
import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.client.gui.widget.NavigationButtonWidget;
import me.roundaround.armorstands.client.util.LastUsedScreen;
import me.roundaround.armorstands.client.util.LastUsedScreen.ScreenType;
import me.roundaround.armorstands.mixin.InGameHudAccessor;
import me.roundaround.armorstands.mixin.KeyBindingAccessor;
import me.roundaround.armorstands.mixin.MouseAccessor;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.network.packet.c2s.InitSlotsPacket;
import me.roundaround.armorstands.network.packet.c2s.UndoPacket;
import me.roundaround.armorstands.network.packet.c2s.UtilityActionPacket;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.HasArmorStand;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class AbstractArmorStandScreen
    extends HandledScreen<ArmorStandScreenHandler>
    implements HasArmorStand, HasMessageRenderer {
  protected static final int SCREEN_EDGE_PAD = 4;
  protected static final int BETWEEN_PAD = 2;
  protected static final int NAV_BUTTON_BOTTOM_PADDING = 1;
  protected static final int NAV_BUTTON_SPACING = 0;
  protected static final Identifier WIDGETS_TEXTURE = new Identifier(
      Identifier.DEFAULT_NAMESPACE,
      "textures/gui/widgets.png");

  protected final ArmorStandEntity armorStand;
  protected final MessageRenderer messageRenderer;
  protected final ArrayList<LabelWidget> labels = new ArrayList<>();

  protected NavigationButtonWidget<?, ?> activeButton;
  protected boolean supportsUndoRedo = false;
  protected boolean utilizesInventory = false;

  private boolean cursorLocked = false;

  protected AbstractArmorStandScreen(
      ArmorStandScreenHandler handler,
      Text title,
      ArmorStandEntity armorStand) {
    super(handler, handler.getPlayerInventory(), title);

    this.armorStand = armorStand;
    this.messageRenderer = new MessageRenderer(this);

    this.passEvents = true;
  }

  public abstract ScreenType getScreenType();

  public abstract ScreenConstructor<?> getPreviousScreen();

  public abstract ScreenConstructor<?> getNextScreen();

  protected LabelWidget addLabel(LabelWidget label) {
    this.labels.add(label);
    return label;
  }

  @Override
  public ArmorStandEntity getArmorStand() {
    return this.armorStand;
  }

  @Override
  public MessageRenderer getMessageRenderer() {
    return this.messageRenderer;
  }

  @Override
  public boolean shouldPause() {
    return false;
  }

  @Override
  public void init() {
    this.handler.initSlots(this.utilizesInventory);
    InitSlotsPacket.sendToServer(this.utilizesInventory);

    this.labels.clear();

    super.init();
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

    // Render labels before all other widgets so they are rendered on bottom
    for (LabelWidget label : this.labels) {
      label.render(matrixStack, adjustedMouseX, adjustedMouseY, delta);
    }

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
    if (this.cursorLocked) {
      return;
    }
    super.mouseMoved(mouseX, mouseY);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (this.cursorLocked) {
      return false;
    }
    Element focused = getFocused();
    boolean result = super.mouseClicked(mouseX, mouseY, button);

    if (this.utilizesInventory) {
      setFocused(focused);
    }

    return result;
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    if (this.cursorLocked) {
      return false;
    }
    if (this.utilizesInventory) {
      return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    if (getFocused() != null && isDragging() && button == 0) {
      return getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    return false;
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    if (this.cursorLocked) {
      return false;
    }
    if (this.utilizesInventory) {
      return super.mouseReleased(mouseX, mouseY, button);
    }

    setDragging(false);
    return hoveredElement(mouseX, mouseY).filter((element) -> {
      return element.mouseReleased(mouseX, mouseY, button);
    }).isPresent();
  }

  @Override
  public Optional<Element> hoveredElement(double mouseX, double mouseY) {
    if (this.cursorLocked) {
      return Optional.empty();
    }
    return super.hoveredElement(mouseX, mouseY);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    // Allow jump to pass through without pressing buttons
    if (this.client.options.jumpKey.matchesKey(keyCode, scanCode)) {
      return false;
    }

    if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
      close();
      return true;
    }

    switch (keyCode) {
      case GLFW.GLFW_KEY_ESCAPE:
        close();
        return true;
      case GLFW.GLFW_KEY_LEFT_ALT:
      case GLFW.GLFW_KEY_RIGHT_ALT:
        if (!this.passEvents) {
          break;
        }
        lockCursor();
        return true;
      case GLFW.GLFW_KEY_TAB:
        boolean forward = !Screen.hasShiftDown();
        if (!changeFocus(forward)) {
          changeFocus(forward);
        }
        return false;
      case GLFW.GLFW_KEY_LEFT:
        if (this.client.options.leftKey.matchesKey(keyCode, scanCode) && !Screen.hasControlDown()) {
          break;
        }
        playClickSound();
        client.currentScreen = null;
        AbstractArmorStandScreen nextScreen = getPreviousScreen().accept(this.handler, this.armorStand);
        LastUsedScreen.set(nextScreen);
        client.setScreen(nextScreen);
        return true;
      case GLFW.GLFW_KEY_RIGHT:
        if (this.client.options.rightKey.matchesKey(keyCode, scanCode) && !Screen.hasControlDown()) {
          break;
        }
        playClickSound();
        client.currentScreen = null;
        nextScreen = getNextScreen().accept(this.handler, this.armorStand);
        LastUsedScreen.set(nextScreen);
        client.setScreen(nextScreen);
        return true;
      case GLFW.GLFW_KEY_Z:
        if (!this.supportsUndoRedo || !Screen.hasControlDown()) {
          break;
        }
        playClickSound();
        UndoPacket.sendToServer(Screen.hasShiftDown());
        return true;
      case GLFW.GLFW_KEY_C:
        if (!this.supportsUndoRedo || !Screen.hasControlDown()) {
          break;
        }
        playClickSound();
        UtilityActionPacket.sendToServer(UtilityAction.COPY);
        return true;
      case GLFW.GLFW_KEY_V:
        if (!this.supportsUndoRedo || !Screen.hasControlDown()) {
          break;
        }
        playClickSound();
        UtilityActionPacket.sendToServer(UtilityAction.PASTE);
        return true;
    }

    return getFocused() != null
        && getFocused().keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
    if (this.passEvents && (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT)) {
      unlockCursor();
      return true;
    }

    return getFocused() != null
        && getFocused().keyReleased(keyCode, scanCode, modifiers);
  }

  public boolean shouldHighlight(Entity entity) {
    return ArmorStandsClientMod.highlightArmorStandKeyBinding.isPressed()
        && entity == this.armorStand;
  }

  public boolean isCursorLocked() {
    return this.cursorLocked;
  }

  protected void initUtilityButtons() {
    addDrawableChild(new HelpButtonWidget(
        this.client,
        this,
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD));
    addDrawableChild(new IconButtonWidget<>(
        this.client,
        this,
        SCREEN_EDGE_PAD + IconButtonWidget.WIDTH + BETWEEN_PAD,
        SCREEN_EDGE_PAD,
        14,
        Text.translatable("armorstands.utility.copy"),
        (button) -> {
          UtilityActionPacket.sendToServer(UtilityAction.COPY);
        }));
    addDrawableChild(new IconButtonWidget<>(
        this.client,
        this,
        SCREEN_EDGE_PAD + 2 * (IconButtonWidget.WIDTH + BETWEEN_PAD),
        SCREEN_EDGE_PAD,
        15,
        Text.translatable("armorstands.utility.paste"),
        (button) -> {
          UtilityActionPacket.sendToServer(UtilityAction.PASTE);
        }));
  }

  protected void initNavigationButtons(Collection<ScreenFactory<?>> screenFactories) {
    int totalWidth = screenFactories.size() * NavigationButtonWidget.WIDTH
        + (screenFactories.size() - 1) * NAV_BUTTON_SPACING;

    int x = (width - totalWidth) / 2 - 2 * NAV_BUTTON_SPACING;
    int y = height - NAV_BUTTON_BOTTOM_PADDING - NavigationButtonWidget.HEIGHT;

    for (ScreenFactory<?> screenFactory : screenFactories) {
      NavigationButtonWidget<?, ?> button = NavigationButtonWidget.create(
          this.client,
          this,
          x,
          y,
          screenFactory);

      if (screenFactory.constructor == null) {
        this.activeButton = button;
        addDrawable(button);
      } else {
        addDrawableChild(button);
      }

      x += NAV_BUTTON_SPACING + NavigationButtonWidget.WIDTH;
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
        this.activeButton.y - 2,
        0,
        22,
        13,
        13);
    drawTexture(matrixStack,
        this.activeButton.x + NavigationButtonWidget.WIDTH / 2 + 1,
        this.activeButton.y - 2,
        12,
        22,
        12,
        13);
    drawTexture(matrixStack,
        this.activeButton.x - 2,
        this.activeButton.y + NavigationButtonWidget.HEIGHT / 2 + 1,
        0,
        34,
        13,
        12);
    drawTexture(matrixStack,
        this.activeButton.x + NavigationButtonWidget.WIDTH / 2 + 1,
        this.activeButton.y + NavigationButtonWidget.HEIGHT / 2 + 1,
        12,
        34,
        12,
        12);
    matrixStack.pop();
  }

  protected void playClickSound() {
    this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f));
  }

  protected void lockCursor() {
    this.cursorLocked = true;
    int x = this.client.getWindow().getWidth() / 2;
    int y = this.client.getWindow().getHeight() / 2;
    ((MouseAccessor) this.client.mouse).setX(x);
    ((MouseAccessor) this.client.mouse).setY(y);
    InputUtil.setCursorParameters(this.client.getWindow().getHandle(), InputUtil.GLFW_CURSOR_DISABLED, x, y);
  }

  protected void unlockCursor() {
    this.cursorLocked = false;
    int x = this.client.getWindow().getWidth() / 2;
    int y = this.client.getWindow().getHeight() / 2;
    ((MouseAccessor) this.client.mouse).setX(x);
    ((MouseAccessor) this.client.mouse).setY(y);
    InputUtil.setCursorParameters(this.client.getWindow().getHandle(), InputUtil.GLFW_CURSOR_NORMAL, x, y);
  }

  public static class ScreenFactory<T extends AbstractArmorStandScreen> {
    public final Text tooltip;
    public final int uIndex;
    public final ScreenConstructor<T> constructor;

    private ScreenFactory(Text tooltip, int uIndex, ScreenConstructor<T> constructor) {
      this.tooltip = tooltip;
      this.uIndex = uIndex;
      this.constructor = constructor;
    }

    public static <T extends AbstractArmorStandScreen> ScreenFactory<T> create(
        Text tooltip,
        int uIndex,
        ScreenConstructor<T> constructor) {
      return new ScreenFactory<>(tooltip, uIndex, constructor);
    }

    public static <T extends AbstractArmorStandScreen> ScreenFactory<T> create(
        Text tooltip,
        int uIndex) {
      return new ScreenFactory<>(tooltip, uIndex, null);
    }
  }

  @FunctionalInterface
  public static interface ScreenConstructor<T extends AbstractArmorStandScreen> {
    public T accept(
        ArmorStandScreenHandler handler,
        ArmorStandEntity armorStand);
  }
}
