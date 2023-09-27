package me.roundaround.armorstands.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.ArmorStandsClientMod;
import me.roundaround.armorstands.client.gui.MessageRenderer;
import me.roundaround.armorstands.client.gui.MessageRenderer.HasMessageRenderer;
import me.roundaround.armorstands.client.gui.widget.HelpButtonWidget;
import me.roundaround.armorstands.client.gui.widget.IconButtonWidget;
import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.client.gui.widget.NavigationButtonWidget;
import me.roundaround.armorstands.mixin.ArmorStandEntityAccessor;
import me.roundaround.armorstands.mixin.InGameHudAccessor;
import me.roundaround.armorstands.mixin.KeyBindingAccessor;
import me.roundaround.armorstands.mixin.MouseAccessor;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.network.packet.c2s.PingPacket;
import me.roundaround.armorstands.network.packet.c2s.RequestScreenPacket;
import me.roundaround.armorstands.network.packet.c2s.UndoPacket;
import me.roundaround.armorstands.network.packet.c2s.UtilityActionPacket;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.HasArmorStand;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public abstract class AbstractArmorStandScreen extends HandledScreen<ArmorStandScreenHandler>
    implements HasArmorStand, HasMessageRenderer, PassesEventsThrough {
  protected static final Identifier SELECTION_TEXTURE =
      new Identifier(ArmorStandsMod.MOD_ID, "textures/gui/selection_frame.png");

  protected static final int SCREEN_EDGE_PAD = 4;
  protected static final int BETWEEN_PAD = 2;
  protected static final int NAV_BUTTON_BOTTOM_PADDING = 1;
  protected static final int NAV_BUTTON_SPACING = 0;

  protected final ArmorStandEntity armorStand;
  protected final MessageRenderer messageRenderer;
  protected final ArrayList<LabelWidget> labels = new ArrayList<>();
  protected final ArrayList<NavigationButtonWidget> navigationButtons = new ArrayList<>();

  protected NavigationButtonWidget activeButton;
  protected boolean supportsUndoRedo = false;
  protected boolean utilizesInventory = false;
  protected boolean passEvents = true;
  protected long currentSyncDelay = 0;

  private boolean cursorLocked = false;
  private long lastPing = 0;

  protected AbstractArmorStandScreen(ArmorStandScreenHandler handler, Text title) {
    super(handler, handler.getPlayerInventory(), title);
    this.armorStand = handler.getArmorStand();

    this.messageRenderer = new MessageRenderer(this);
  }

  public abstract ScreenType getScreenType();

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
  public boolean shouldPassEvents() {
    return this.passEvents;
  }

  @Override
  public boolean shouldPause() {
    return false;
  }

  @Override
  public void init() {
    initStart();

    super.init();

    initLeft();
    initNavigationButtons();
    initRight();

    initEnd();
  }

  @Override
  protected void handledScreenTick() {
    // Block any inputs bound to shift so that this screen gets exclusive use
    Arrays.stream(this.client.options.allKeys).filter((key) -> {
      return key.matchesKey(GLFW.GLFW_KEY_LEFT_SHIFT, 0) ||
          key.matchesKey(GLFW.GLFW_KEY_RIGHT_SHIFT, 0);
    }).map((key) -> (KeyBindingAccessor) key).forEach(KeyBindingAccessor::invokeReset);

    ((InGameHudAccessor) this.client.inGameHud).invokeUpdateVignetteDarkness(this.client.getCameraEntity());

    messageRenderer.tick();
  }

  @Override
  public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    int adjustedMouseX = cursorLocked ? -1 : mouseX;
    int adjustedMouseY = cursorLocked ? -1 : mouseY;

    RenderSystem.enableBlend();
    ((InGameHudAccessor) this.client.inGameHud).invokeRenderVignetteOverlay(drawContext,
        this.client.getCameraEntity());

    // Render labels before all other widgets so they are rendered on bottom
    for (LabelWidget label : this.labels) {
      label.render(drawContext, adjustedMouseX, adjustedMouseY, delta);
    }

    super.render(drawContext, adjustedMouseX, adjustedMouseY, delta);

    renderActivePageButtonHighlight(drawContext);

    this.messageRenderer.render(drawContext);
  }

  @Override
  public void renderInGameBackground(DrawContext context) {
    // No grayed out background
  }

  @Override
  protected void drawBackground(DrawContext drawContext, float delta, int mouseX, int mouseY) {
  }

  @Override
  protected void drawForeground(DrawContext drawContext, int mouseX, int mouseY) {
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
  public boolean mouseDragged(
      double mouseX, double mouseY, int button, double deltaX, double deltaY) {
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

    if (isDragging() && getFocused() != null && button == 0) {
      setDragging(false);
      return getFocused().mouseReleased(mouseX, mouseY, button);
    }

    setDragging(false);
    return hoveredElement(mouseX, mouseY).filter((element) -> {
      return element.mouseReleased(mouseX, mouseY, button);
    }).isPresent();
  }

  @Override
  public boolean mouseScrolled(
      double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    if (this.cursorLocked) {
      return false;
    }
    for (NavigationButtonWidget button : this.navigationButtons) {
      if (button.isMouseOverIgnoreState(mouseX, mouseY)) {
        if (verticalAmount > 0) {
          goToPreviousScreen();
        } else {
          goToNextScreen();
        }
        return true;
      }
    }
    return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
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
      case GLFW.GLFW_KEY_LEFT:
        if (!Screen.hasControlDown()) {
          break;
        }
        playClickSound();
        goToPreviousScreen();
        return true;
      case GLFW.GLFW_KEY_RIGHT:
        if (!Screen.hasControlDown()) {
          break;
        }
        playClickSound();
        goToNextScreen();
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

    for (int i = 0; i < this.navigationButtons.size(); i++) {
      NavigationButtonWidget button = this.navigationButtons.get(i);
      ScreenType screenType = button.getScreenType();

      if (screenType == this.getScreenType()) {
        continue;
      }

      if (this.client.options.hotbarKeys[i].matchesKey(keyCode, scanCode)) {
        playClickSound();
        button.onPress();
        return true;
      }
    }

    if (getFocused() != null && getFocused().keyPressed(keyCode, scanCode, modifiers)) {
      return true;
    }

    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
    if (this.passEvents &&
        (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT)) {
      unlockCursor();
      return true;
    }

    return getFocused() != null && getFocused().keyReleased(keyCode, scanCode, modifiers);
  }

  public boolean shouldHighlight(Entity entity) {
    return ArmorStandsClientMod.highlightArmorStandKeyBinding.isPressed() &&
        entity == this.armorStand;
  }

  public boolean isCursorLocked() {
    return this.cursorLocked;
  }

  public void sendPing() {
    this.lastPing = System.currentTimeMillis();
    PingPacket.sendToServer(this.client.player);
  }

  public void onPong() {
    this.currentSyncDelay = System.currentTimeMillis() - this.lastPing;
  }

  public void updatePosOnClient(double x, double y, double z) {
    this.armorStand.setPos(x, y, z);
  }

  public void updateYawOnClient(float yaw) {
    this.armorStand.setYaw(yaw);
  }

  public void updatePitchOnClient(float pitch) {
    this.armorStand.setPitch(pitch);
  }

  public void updateInvulnerableOnClient(boolean invulnerable) {
    this.armorStand.setInvulnerable(invulnerable);
  }

  public void updateDisabledSlotsOnClient(int disabledSlots) {
    ((ArmorStandEntityAccessor) this.armorStand).setDisabledSlots(disabledSlots);
  }

  protected void initStart() {
    this.labels.clear();
    this.navigationButtons.clear();
  }

  protected void initUtilityButtons() {
    if (!this.supportsUndoRedo) {
      return;
    }

    addDrawableChild(new HelpButtonWidget(SCREEN_EDGE_PAD, SCREEN_EDGE_PAD));
    addDrawableChild(new IconButtonWidget(SCREEN_EDGE_PAD + IconButtonWidget.WIDTH + BETWEEN_PAD,
        SCREEN_EDGE_PAD,
        14,
        Text.translatable("armorstands.utility.copy"),
        (button) -> {
          UtilityActionPacket.sendToServer(UtilityAction.COPY);
        }));
    addDrawableChild(new IconButtonWidget(
        SCREEN_EDGE_PAD + 2 * (IconButtonWidget.WIDTH + BETWEEN_PAD),
        SCREEN_EDGE_PAD,
        15,
        Text.translatable("armorstands.utility.paste"),
        (button) -> {
          UtilityActionPacket.sendToServer(UtilityAction.PASTE);
        }));
    addDrawableChild(new IconButtonWidget(
        SCREEN_EDGE_PAD + 3 * (IconButtonWidget.WIDTH + BETWEEN_PAD),
        SCREEN_EDGE_PAD,
        17,
        Text.translatable("armorstands.utility.undo"),
        (button) -> {
          UndoPacket.sendToServer(false);
        }));
    addDrawableChild(new IconButtonWidget(
        SCREEN_EDGE_PAD + 4 * (IconButtonWidget.WIDTH + BETWEEN_PAD),
        SCREEN_EDGE_PAD,
        18,
        Text.translatable("armorstands.utility.redo"),
        (button) -> {
          UndoPacket.sendToServer(true);
        }));
  }

  protected void initLeft() {
    initUtilityButtons();
  }

  protected void initNavigationButtons() {
    ScreenType[] screenTypes = ScreenType.values();
    int totalWidth = screenTypes.length * NavigationButtonWidget.WIDTH +
        (screenTypes.length - 1) * NAV_BUTTON_SPACING;

    int x = (width - totalWidth) / 2 - 2 * NAV_BUTTON_SPACING;
    int y = height - NAV_BUTTON_BOTTOM_PADDING - NavigationButtonWidget.HEIGHT;

    for (ScreenType screenType : screenTypes) {
      NavigationButtonWidget button = new NavigationButtonWidget(this, x, y, screenType);

      if (getScreenType() == screenType) {
        this.activeButton = button;
        addDrawable(button);
      } else {
        addDrawableChild(button);
      }

      x += NAV_BUTTON_SPACING + NavigationButtonWidget.WIDTH;

      this.navigationButtons.add(button);
    }
  }

  protected void initRight() {
  }

  protected void initEnd() {
  }

  protected void renderActivePageButtonHighlight(DrawContext drawContext) {
    if (this.activeButton == null) {
      return;
    }

    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();

    MatrixStack matrixStack = drawContext.getMatrices();
    matrixStack.push();
    matrixStack.translate(0, 0, 100);
    drawContext.drawTexture(SELECTION_TEXTURE,
        this.activeButton.getX() - 2,
        this.activeButton.getY() - 2,
        0,
        0,
        24,
        24,
        24,
        24);
    matrixStack.pop();
  }

  protected void playClickSound() {
    this.client.getSoundManager()
        .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f));
  }

  protected void lockCursor() {
    this.cursorLocked = true;
    int x = this.client.getWindow().getWidth() / 2;
    int y = this.client.getWindow().getHeight() / 2;
    ((MouseAccessor) this.client.mouse).setX(x);
    ((MouseAccessor) this.client.mouse).setY(y);
    InputUtil.setCursorParameters(this.client.getWindow().getHandle(),
        InputUtil.GLFW_CURSOR_DISABLED,
        x,
        y);
  }

  protected void unlockCursor() {
    this.cursorLocked = false;
    int x = this.client.getWindow().getWidth() / 2;
    int y = this.client.getWindow().getHeight() / 2;
    ((MouseAccessor) this.client.mouse).setX(x);
    ((MouseAccessor) this.client.mouse).setY(y);
    InputUtil.setCursorParameters(this.client.getWindow().getHandle(),
        InputUtil.GLFW_CURSOR_NORMAL,
        x,
        y);
  }

  protected void goToPreviousScreen() {
    RequestScreenPacket.sendToServer(this.armorStand, getScreenType().previous());
  }

  protected void goToNextScreen() {
    RequestScreenPacket.sendToServer(this.armorStand, getScreenType().next());
  }
}
