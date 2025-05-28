package me.roundaround.armorstands.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.ArmorStandsClientMod;
import me.roundaround.armorstands.client.gui.MessageRenderer;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.mixin.ArmorStandEntityAccessor;
import me.roundaround.armorstands.mixin.InGameHudAccessor;
import me.roundaround.armorstands.mixin.MouseAccessor;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.roundalib.client.gui.icon.BuiltinIcon;
import me.roundaround.armorstands.roundalib.client.gui.icon.CustomIcon;
import me.roundaround.armorstands.roundalib.client.gui.layout.linear.LinearLayoutWidget;
import me.roundaround.armorstands.roundalib.client.gui.util.GuiUtil;
import me.roundaround.armorstands.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.armorstands.roundalib.client.gui.widget.drawable.FrameWidget;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractArmorStandScreen extends HandledScreen<ArmorStandScreenHandler> implements
    PassesEventsThrough {
  protected static final CustomIcon COPY_ICON = new CustomIcon("copy", 20);
  protected static final CustomIcon PASTE_ICON = new CustomIcon("paste", 20);
  protected static final int BACKGROUND_COLOR = GuiUtil.genColorInt(0f, 0f, 0f, 0.7f);
  protected static final int ELEMENT_HEIGHT = 16;

  protected final ArmorStandLayoutWidget layout = new ArmorStandLayoutWidget(this);
  protected final ArmorStandEntity armorStand;
  protected final MessageRenderer messageRenderer;

  protected LinearLayoutWidget utilRow;
  protected LinearLayoutWidget navRow;
  protected IconButtonWidget activePageButton;
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
    this.collectElements();
    this.initTabNavigation();
  }

  protected MinecraftClient getClient() {
    return Objects.requireNonNull(this.client);
  }

  protected ClientPlayerEntity getPlayer() {
    return this.getClient().player;
  }

  protected void populateLayout() {
    this.initUtilityButtons();
    this.initNavigationButtons();
  }

  protected void initUtilityButtons() {
    if (!this.supportsUndoRedo) {
      return;
    }

    this.utilRow = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING / 2);
    this.utilRow.add(IconButtonWidget.builder(BuiltinIcon.HELP_18, ArmorStandsMod.MOD_ID)
        .vanillaSize()
        .messageAndTooltip(this.buildHelpTooltipText())
        .build());
    this.utilRow.add(IconButtonWidget.builder(COPY_ICON, ArmorStandsMod.MOD_ID)
        .vanillaSize()
        .messageAndTooltip(Text.translatable("armorstands.utility.copy"))
        .onPress((button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.COPY))
        .build());
    this.utilRow.add(IconButtonWidget.builder(PASTE_ICON, ArmorStandsMod.MOD_ID)
        .vanillaSize()
        .messageAndTooltip(Text.translatable("armorstands.utility.paste"))
        .onPress((button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.PASTE))
        .build());
    this.utilRow.add(IconButtonWidget.builder(BuiltinIcon.UNDO_18, ArmorStandsMod.MOD_ID)
        .vanillaSize()
        .messageAndTooltip(Text.translatable("armorstands.utility.undo"))
        .onPress((button) -> ClientNetworking.sendUndoPacket(false))
        .build());
    this.utilRow.add(IconButtonWidget.builder(BuiltinIcon.REDO_18, ArmorStandsMod.MOD_ID)
        .vanillaSize()
        .messageAndTooltip(Text.translatable("armorstands.utility.redo"))
        .onPress((button) -> ClientNetworking.sendUndoPacket(true))
        .build());
    this.layout.topLeft.add(this.utilRow);
  }

  private Text buildHelpTooltipText() {
    String alt = Text.translatable("armorstands.help.alt").getString();
    String inventory = this.getClient().options.inventoryKey.getBoundKeyLocalizedText().getString();
    String esc = InputUtil.fromKeyCode(GLFW.GLFW_KEY_ESCAPE, 0).getLocalizedText().getString();
    String highlight = ArmorStandsClientMod.highlightArmorStandKeyBinding.getBoundKeyLocalizedText().getString();
    String control = Text.translatable("armorstands.help." + (MinecraftClient.IS_SYSTEM_MAC ? "cmd" : "ctrl"))
        .getString();
    String z = InputUtil.fromKeyCode(GLFW.GLFW_KEY_Z, 0).getLocalizedText().getString();
    String y = InputUtil.fromKeyCode(GLFW.GLFW_KEY_Y, 0).getLocalizedText().getString();
    String c = InputUtil.fromKeyCode(GLFW.GLFW_KEY_C, 0).getLocalizedText().getString();
    String v = InputUtil.fromKeyCode(GLFW.GLFW_KEY_V, 0).getLocalizedText().getString();

    return Text.translatable(
        "armorstands.help",
        alt,
        inventory,
        esc,
        ScreenType.values().length,
        highlight,
        control,
        z,
        control,
        y,
        control,
        c,
        control,
        v
    );
  }

  protected void initNavigationButtons() {
    this.navRow = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING / 2);

    for (ScreenType screenType : ScreenType.values()) {
      IconButtonWidget navButton = IconButtonWidget.builder(screenType.getIcon(), ArmorStandsMod.MOD_ID)
          .vanillaSize()
          .disableIconDim()
          .messageAndTooltip(screenType.getDisplayName())
          .onPress((button) -> ClientNetworking.sendRequestScreenPacket(this.getArmorStand(), screenType))
          .build();
      if (this.getScreenType() == screenType) {
        navButton.active = false;
        this.activePageButton = navButton;
      }
      this.navRow.add(navButton);
    }

    this.layout.topRight.add(this.navRow);

    if (this.activePageButton != null) {
      this.layout.nonPositioned.add(new FrameWidget(this.activePageButton));
    }
  }

  protected void collectElements() {
    this.layout.forEachChild(this::addDrawableChild);
  }

  @Override
  protected void initTabNavigation() {
    this.layout.refreshPositions();
  }

  @Override
  protected void handledScreenTick() {
    assert this.client != null;

    ((InGameHudAccessor) this.client.inGameHud).invokeUpdateVignetteDarkness(this.client.getCameraEntity());

    this.messageRenderer.tick();
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    assert this.client != null;

    int adjustedMouseX = cursorLocked ? -1 : mouseX;
    int adjustedMouseY = cursorLocked ? -1 : mouseY;

    RenderSystem.enableBlend();
    ((InGameHudAccessor) this.client.inGameHud).invokeRenderVignetteOverlay(context, this.client.getCameraEntity());

    super.render(context, adjustedMouseX, adjustedMouseY, delta);

    this.messageRenderer.render(context);
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

    if (isDragging() && getFocused() != null && button == 0) {
      setDragging(false);
      return getFocused().mouseReleased(mouseX, mouseY, button);
    }

    setDragging(false);
    return hoveredElement(mouseX, mouseY).filter((element) -> element.mouseReleased(mouseX, mouseY, button))
        .isPresent();
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    if (this.cursorLocked) {
      return false;
    }

    if (this.navRow != null && this.navRow.getBounds().contains(mouseX, mouseY)) {
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
        GuiUtil.playClickSound();
        goToPreviousScreen();
        return true;
      case GLFW.GLFW_KEY_RIGHT:
        if (!Screen.hasControlDown()) {
          break;
        }
        GuiUtil.playClickSound();
        goToNextScreen();
        return true;
      case GLFW.GLFW_KEY_Z:
        if (!this.supportsUndoRedo || !Screen.hasControlDown()) {
          break;
        }
        GuiUtil.playClickSound();
        ClientNetworking.sendUndoPacket(false);
        return true;
      case GLFW.GLFW_KEY_Y:
        if (!this.supportsUndoRedo || !Screen.hasControlDown()) {
          break;
        }
        GuiUtil.playClickSound();
        ClientNetworking.sendUndoPacket(true);
        return true;
      case GLFW.GLFW_KEY_C:
        if (!this.supportsUndoRedo || !Screen.hasControlDown()) {
          break;
        }
        GuiUtil.playClickSound();
        ClientNetworking.sendUtilityActionPacket(UtilityAction.COPY);
        return true;
      case GLFW.GLFW_KEY_V:
        if (!this.supportsUndoRedo || !Screen.hasControlDown()) {
          break;
        }
        GuiUtil.playClickSound();
        ClientNetworking.sendUtilityActionPacket(UtilityAction.PASTE);
        return true;
    }

    for (ScreenType screenType : ScreenType.values()) {
      if (screenType == this.getScreenType()) {
        continue;
      }
      if (this.client.options.hotbarKeys[screenType.getIndex()].matchesKey(keyCode, scanCode)) {
        GuiUtil.playClickSound();
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

  protected static Text getCurrentPosText(Entity entity) {
    String xStr = String.format("%.2f", entity.getX());
    String yStr = String.format("%.2f", entity.getY());
    String zStr = String.format("%.2f", entity.getZ());
    return Text.translatable("armorstands.current.position", xStr, yStr, zStr);
  }

  protected static Text getCurrentBlockPosText(Entity entity) {
    BlockPos pos = entity.getBlockPos();
    return Text.translatable("armorstands.current.block", pos.getX(), pos.getY(), pos.getZ());
  }

  protected static Text getCurrentRotationText(Entity entity) {
    float currentRotation = entity.getYaw();
    return Text.translatable(
        "armorstands.current.rotation",
        String.format(Locale.ROOT, "%.1f", MathHelper.wrapDegrees(currentRotation))
    );
  }

  protected static Text getCurrentFacingText(Entity entity) {
    return getFacingText(Direction.fromRotation(entity.getYaw()));
  }

  protected static Text getFacingText(Direction facing) {
    String towardsI18n = switch (facing) {
      case NORTH -> "negZ";
      case SOUTH -> "posZ";
      case WEST -> "negX";
      default -> "posX";
    };
    Text towards = Text.translatable("armorstands.current.facing." + towardsI18n);
    return Text.translatable("armorstands.current.facing", facing.toString(), towards.getString());
  }
}
