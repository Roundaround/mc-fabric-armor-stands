package me.roundaround.armorstands.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.ArmorStandsClientMod;
import me.roundaround.armorstands.client.gui.MessageRenderer;
import me.roundaround.armorstands.client.gui.widget.ArmorStandLayoutWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.mixin.ArmorStandEntityAccessor;
import me.roundaround.armorstands.mixin.InGameHudAccessor;
import me.roundaround.armorstands.mixin.KeyBindingAccessor;
import me.roundaround.armorstands.mixin.MouseAccessor;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.roundalib.asset.icon.BuiltinIcon;
import me.roundaround.roundalib.asset.icon.CustomIcon;
import me.roundaround.roundalib.client.gui.layout.IntRect;
import me.roundaround.roundalib.client.gui.widget.DrawableWidget;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractArmorStandScreen extends HandledScreen<ArmorStandScreenHandler> implements
    PassesEventsThrough {
  protected static final Identifier SELECTION_TEXTURE = new Identifier(ArmorStandsMod.MOD_ID, "selection");
  protected static final CustomIcon COPY_ICON = new CustomIcon("copy", 20);
  protected static final CustomIcon PASTE_ICON = new CustomIcon("paste", 20);

  protected final ArmorStandLayoutWidget layout = new ArmorStandLayoutWidget(this);
  protected final ArmorStandEntity armorStand;
  protected final MessageRenderer messageRenderer;

  protected IconButtonWidget activeButton;
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

  public ArmorStandEntity getArmorStand() {
    return this.armorStand;
  }

  public MessageRenderer getMessageRenderer() {
    return this.messageRenderer;
  }

  @Override
  public boolean shouldPassEvents() {
    return this.passEvents;
  }

  @Override
  public void init() {
    this.populateLayout();
    this.collectChildren();
    this.initTabNavigation();
  }

  protected void populateLayout() {
    this.initUtilityButtons();
    this.initNavigationButtons();
  }

  protected void initUtilityButtons() {
    if (!this.supportsUndoRedo) {
      return;
    }

    this.layout.topLeft.add(IconButtonWidget.builder(BuiltinIcon.HELP_18, ArmorStandsMod.MOD_ID)
        .large()
        .messageAndTooltip(this.buildHelpTooltipText())
        .build());
    this.layout.topLeft.add(IconButtonWidget.builder(COPY_ICON, ArmorStandsMod.MOD_ID)
        .large()
        .messageAndTooltip(Text.translatable("armorstands.utility.copy"))
        .onPress((button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.COPY))
        .build());
    this.layout.topLeft.add(IconButtonWidget.builder(PASTE_ICON, ArmorStandsMod.MOD_ID)
        .large()
        .messageAndTooltip(Text.translatable("armorstands.utility.paste"))
        .onPress((button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.PASTE))
        .build());
    this.layout.topLeft.add(IconButtonWidget.builder(BuiltinIcon.UNDO_18, ArmorStandsMod.MOD_ID)
        .large()
        .messageAndTooltip(Text.translatable("armorstands.utility.undo"))
        .onPress((button) -> ClientNetworking.sendUndoPacket(false))
        .build());
    this.layout.topLeft.add(IconButtonWidget.builder(BuiltinIcon.REDO_18, ArmorStandsMod.MOD_ID)
        .large()
        .messageAndTooltip(Text.translatable("armorstands.utility.redo"))
        .onPress((button) -> ClientNetworking.sendUndoPacket(true))
        .build());
  }

  private Text buildHelpTooltipText() {
    String alt = Text.translatable("armorstands.help.alt").getString();
    String inventory = Objects.requireNonNull(this.client).options.inventoryKey.getBoundKeyLocalizedText().getString();
    String left = InputUtil.fromKeyCode(GLFW.GLFW_KEY_LEFT, 0).getLocalizedText().getString();
    String right = InputUtil.fromKeyCode(GLFW.GLFW_KEY_RIGHT, 0).getLocalizedText().getString();
    String highlight = ArmorStandsClientMod.highlightArmorStandKeyBinding.getBoundKeyLocalizedText().getString();
    String control = Text.translatable("armorstands.help." + (MinecraftClient.IS_SYSTEM_MAC ? "cmd" : "ctrl"))
        .getString();
    String z = InputUtil.fromKeyCode(GLFW.GLFW_KEY_Z, 0).getLocalizedText().getString();
    String shift = Text.translatable("armorstands.help.shift").getString();
    String c = InputUtil.fromKeyCode(GLFW.GLFW_KEY_C, 0).getLocalizedText().getString();
    String v = InputUtil.fromKeyCode(GLFW.GLFW_KEY_V, 0).getLocalizedText().getString();

    return Text.translatable("armorstands.help", alt, inventory, left, right, highlight, control, z, control, shift, z,
        control, c, control, v
    );
  }

  protected void initNavigationButtons() {
    for (ScreenType screenType : ScreenType.values()) {
      IconButtonWidget navButton = IconButtonWidget.builder(screenType.getIcon(), ArmorStandsMod.MOD_ID)
          .onPress((button) -> ClientNetworking.sendRequestScreenPacket(this.getArmorStand(), screenType))
          .build();
      if (this.getScreenType() == screenType) {
        navButton.active = false;
      }
      this.layout.navRow.add(navButton);
    }
  }

  protected void collectChildren() {
    this.layout.forEachChild(this::addDrawableChild);
    this.addDrawableChild(new DrawableWidget() {
      @Override
      protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        IconButtonWidget activeButton = AbstractArmorStandScreen.this.activeButton;
        if (activeButton == null) {
          return;
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        context.drawGuiTexture(SELECTION_TEXTURE, activeButton.getX() - 2, activeButton.getY() - 2, 24, 24);
      }
    });
  }

  @Override
  protected void initTabNavigation() {
    this.layout.refreshPositions();
  }

  @Override
  protected void handledScreenTick() {
    assert this.client != null;

    // Block any inputs bound to shift so that this screen gets exclusive use
    Arrays.stream(this.client.options.allKeys)
        .filter((key) -> key.matchesKey(GLFW.GLFW_KEY_LEFT_SHIFT, 0) || key.matchesKey(GLFW.GLFW_KEY_RIGHT_SHIFT, 0))
        .map((key) -> (KeyBindingAccessor) key)
        .forEach(KeyBindingAccessor::invokeReset);

    ((InGameHudAccessor) this.client.inGameHud).invokeUpdateVignetteDarkness(this.client.getCameraEntity());

    messageRenderer.tick();
  }

  @Override
  public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    assert this.client != null;

    int adjustedMouseX = cursorLocked ? -1 : mouseX;
    int adjustedMouseY = cursorLocked ? -1 : mouseY;

    RenderSystem.enableBlend();
    ((InGameHudAccessor) this.client.inGameHud).invokeRenderVignetteOverlay(drawContext, this.client.getCameraEntity());

    super.render(drawContext, adjustedMouseX, adjustedMouseY, delta);

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
      double mouseX, double mouseY, int button, double deltaX, double deltaY
  ) {
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
  public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    if (this.cursorLocked) {
      return false;
    }

    if (IntRect.fromWidget(this.layout.navRow).contains(mouseX, mouseY)) {
      if (verticalAmount > 0) {
        goToPreviousScreen();
      } else {
        goToNextScreen();
      }
      return true;
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
    assert this.client != null;

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
        ClientNetworking.sendUndoPacket(Screen.hasShiftDown());
        return true;
      case GLFW.GLFW_KEY_C:
        if (!this.supportsUndoRedo || !Screen.hasControlDown()) {
          break;
        }
        playClickSound();
        ClientNetworking.sendUtilityActionPacket(UtilityAction.COPY);
        return true;
      case GLFW.GLFW_KEY_V:
        if (!this.supportsUndoRedo || !Screen.hasControlDown()) {
          break;
        }
        playClickSound();
        ClientNetworking.sendUtilityActionPacket(UtilityAction.PASTE);
        return true;
    }

    for (ScreenType screenType : ScreenType.values()) {
      if (screenType == this.getScreenType()) {
        continue;
      }
      if (this.client.options.hotbarKeys[screenType.getId()].matchesKey(keyCode, scanCode)) {
        playClickSound();
        ClientNetworking.sendRequestScreenPacket(this.armorStand, screenType);
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
    if (this.passEvents && (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT)) {
      unlockCursor();
      return true;
    }

    return getFocused() != null && getFocused().keyReleased(keyCode, scanCode, modifiers);
  }

  public boolean shouldHighlight(Entity entity) {
    return ArmorStandsClientMod.highlightArmorStandKeyBinding.isPressed() && entity == this.armorStand;
  }

  public boolean isCursorLocked() {
    return this.cursorLocked;
  }

  public void sendPing() {
    this.lastPing = System.currentTimeMillis();
    ClientNetworking.sendPingPacket(Objects.requireNonNull(this.client).player);
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

  protected void playClickSound() {
    Objects.requireNonNull(this.client)
        .getSoundManager()
        .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f));
  }

  protected void lockCursor() {
    assert this.client != null;

    this.cursorLocked = true;
    int x = this.client.getWindow().getWidth() / 2;
    int y = this.client.getWindow().getHeight() / 2;
    ((MouseAccessor) this.client.mouse).setX(x);
    ((MouseAccessor) this.client.mouse).setY(y);
    InputUtil.setCursorParameters(this.client.getWindow().getHandle(), InputUtil.GLFW_CURSOR_DISABLED, x, y);
  }

  protected void unlockCursor() {
    assert this.client != null;

    this.cursorLocked = false;
    int x = this.client.getWindow().getWidth() / 2;
    int y = this.client.getWindow().getHeight() / 2;
    ((MouseAccessor) this.client.mouse).setX(x);
    ((MouseAccessor) this.client.mouse).setY(y);
    InputUtil.setCursorParameters(this.client.getWindow().getHandle(), InputUtil.GLFW_CURSOR_NORMAL, x, y);
  }

  protected void goToPreviousScreen() {
    ClientNetworking.sendRequestScreenPacket(this.armorStand, getScreenType().previous());
  }

  protected void goToNextScreen() {
    ClientNetworking.sendRequestScreenPacket(this.armorStand, getScreenType().next());
  }
}
