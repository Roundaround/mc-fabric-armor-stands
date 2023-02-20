package me.roundaround.armorstands.client.gui.screen;

import java.util.List;

import me.roundaround.armorstands.client.gui.widget.NavigationButton;
import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ArmorStandPoseScreen
    extends AbstractArmorStandScreen {
  public static final Text TITLE = Text.translatable("armorstands.screen.pose");
  public static final int U_INDEX = 3;

  private static final int PADDING = 4;
  private static final int HEADER_HEIGHT = 20;
  private static final int FOOTER_HEIGHT = NavigationButton.HEIGHT + PADDING + NAV_BUTTON_BOTTOM_PADDING;

  private final ArmorStandEntity previewStand;

  public ArmorStandPoseScreen(
      ArmorStandScreenHandler handler,
      ArmorStandEntity armorStand) {
    super(handler, TITLE, armorStand);

    this.supportsUndoRedo = true;
    this.passEvents = false;

    this.previewStand = initializePreviewStand();
  }

  private ArmorStandEntity initializePreviewStand() {
    ArmorStandEntity previewStand = new ArmorStandEntity(
        EntityType.ARMOR_STAND,
        this.armorStand.world);

    for (ArmorStandFlag flag : ArmorStandFlag.values()) {
      if (flag == ArmorStandFlag.GRAVITY) {
        flag.setValue(previewStand, true);
        continue;
      }
      flag.setValue(previewStand, flag.getValue(armorStand));
    }

    previewStand.equipStack(EquipmentSlot.HEAD,
        this.armorStand.getEquippedStack(EquipmentSlot.HEAD).copy());
    previewStand.equipStack(EquipmentSlot.CHEST,
        this.armorStand.getEquippedStack(EquipmentSlot.CHEST).copy());
    previewStand.equipStack(EquipmentSlot.LEGS,
        this.armorStand.getEquippedStack(EquipmentSlot.LEGS).copy());
    previewStand.equipStack(EquipmentSlot.FEET,
        this.armorStand.getEquippedStack(EquipmentSlot.FEET).copy());

    previewStand.equipStack(EquipmentSlot.MAINHAND,
        this.armorStand.getEquippedStack(EquipmentSlot.MAINHAND).copy());
    previewStand.equipStack(EquipmentSlot.OFFHAND,
        this.armorStand.getEquippedStack(EquipmentSlot.OFFHAND).copy());

    previewStand.tick();

    return previewStand;
  }

  @Override
  public boolean shouldPause() {
    return true;
  }

  @Override
  public void init() {
    super.init();

    initNavigationButtons(List.of(
        ScreenFactory.create(
            ArmorStandUtilitiesScreen.TITLE,
            ArmorStandUtilitiesScreen.U_INDEX,
            ArmorStandUtilitiesScreen::new),
        ScreenFactory.create(
            ArmorStandMoveScreen.TITLE,
            ArmorStandMoveScreen.U_INDEX,
            ArmorStandMoveScreen::new),
        ScreenFactory.create(
            ArmorStandRotateScreen.TITLE,
            ArmorStandRotateScreen.U_INDEX,
            ArmorStandRotateScreen::new),
        ScreenFactory.create(
            ArmorStandPresetsScreen.TITLE,
            ArmorStandPresetsScreen.U_INDEX,
            ArmorStandPresetsScreen::new),
        ScreenFactory.create(
            ArmorStandPoseScreen.TITLE,
            ArmorStandPoseScreen.U_INDEX),
        ScreenFactory.create(
            ArmorStandInventoryScreen.TITLE,
            ArmorStandInventoryScreen.U_INDEX,
            ArmorStandInventoryScreen::new)));
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderBackgroundTexture(0);

    int size = MathHelper.floor(Math.min(
        this.width / 2 - 4 * PADDING,
        (this.height - HEADER_HEIGHT - FOOTER_HEIGHT - 3 * PADDING) / 2.5f));

    int x = this.width * 3 / 4;
    int y = this.height - FOOTER_HEIGHT - 3 * PADDING;

    InventoryScreen.drawEntity(
        x,
        y,
        size,
        2f * (this.width / 2 - mouseX),
        2f * (y - 50 - mouseY),
        this.previewStand);

    super.render(matrixStack, mouseX, mouseY, partialTicks);
  }
}
