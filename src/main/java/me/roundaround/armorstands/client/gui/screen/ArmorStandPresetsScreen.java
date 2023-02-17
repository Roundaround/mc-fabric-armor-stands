package me.roundaround.armorstands.client.gui.screen;

import java.util.List;

import me.roundaround.armorstands.client.gui.widget.NavigationButton;
import me.roundaround.armorstands.client.gui.widget.PresetPosesListWidget;
import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.PosePreset;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ArmorStandPresetsScreen
    extends AbstractArmorStandScreen {
  public static final Text TITLE = Text.translatable("armorstands.screen.presets");
  public static final int U_INDEX = 4;

  private static final int PADDING = 4;
  private static final int HEADER_HEIGHT = 20;
  private static final int FOOTER_HEIGHT = NavigationButton.HEIGHT + PADDING + NAV_BUTTON_BOTTOM_PADDING;

  private final ArmorStandEntity previewStand;

  private PresetPosesListWidget list;
  private CyclingButtonWidget<Equipment> equipmentButton;

  public ArmorStandPresetsScreen(
      ArmorStandScreenHandler handler,
      ArmorStandEntity armorStand) {
    super(handler, TITLE, armorStand);

    this.supportsUndoRedo = true;
    this.passEvents = false;

    this.previewStand = new ArmorStandEntity(
        EntityType.ARMOR_STAND,
        this.armorStand.world);

    for (ArmorStandFlag flag : ArmorStandFlag.values()) {
      if (flag == ArmorStandFlag.GRAVITY) {
        flag.setValue(this.previewStand, true);
        continue;
      }
      flag.setValue(this.previewStand, flag.getValue(this.armorStand));
    }

    setEquipmentType(Equipment.ACTUAL);
  }

  public void setSelectedPose(PosePreset pose) {
    pose.toPose().apply(this.armorStand);
    pose.toPose().apply(this.previewStand);
    this.previewStand.tick();
  }

  public void setEquipmentType(Equipment equipment) {
    switch (equipment) {
      case ACTUAL:
        this.previewStand.equipStack(EquipmentSlot.HEAD,
            this.armorStand.getEquippedStack(EquipmentSlot.HEAD).copy());
        this.previewStand.equipStack(EquipmentSlot.CHEST,
            this.armorStand.getEquippedStack(EquipmentSlot.CHEST).copy());
        this.previewStand.equipStack(EquipmentSlot.LEGS,
            this.armorStand.getEquippedStack(EquipmentSlot.LEGS).copy());
        this.previewStand.equipStack(EquipmentSlot.FEET,
            this.armorStand.getEquippedStack(EquipmentSlot.FEET).copy());

        this.previewStand.equipStack(EquipmentSlot.MAINHAND,
            this.armorStand.getEquippedStack(EquipmentSlot.MAINHAND).copy());
        this.previewStand.equipStack(EquipmentSlot.OFFHAND,
            this.armorStand.getEquippedStack(EquipmentSlot.OFFHAND).copy());
        break;
      case ARMORED:
        this.previewStand.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
        this.previewStand.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
        this.previewStand.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
        this.previewStand.equipStack(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));

        this.previewStand.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.previewStand.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
        break;
      case CHARACTER:
        this.previewStand.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.PLAYER_HEAD));
        this.previewStand.equipStack(EquipmentSlot.CHEST, ItemStack.EMPTY);
        this.previewStand.equipStack(EquipmentSlot.LEGS, ItemStack.EMPTY);
        this.previewStand.equipStack(EquipmentSlot.FEET, ItemStack.EMPTY);

        this.previewStand.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.APPLE));
        this.previewStand.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        break;
      case NONE:
      default:
        this.previewStand.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
        this.previewStand.equipStack(EquipmentSlot.CHEST, ItemStack.EMPTY);
        this.previewStand.equipStack(EquipmentSlot.LEGS, ItemStack.EMPTY);
        this.previewStand.equipStack(EquipmentSlot.FEET, ItemStack.EMPTY);

        this.previewStand.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        this.previewStand.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
    }

    this.previewStand.tick();
  }

  @Override
  public boolean shouldPause() {
    return true;
  }

  @Override
  public void init() {
    super.init();

    this.list = new PresetPosesListWidget(
        client,
        this,
        MathHelper.floor(this.width / 2f - 2 * PADDING),
        this.height,
        0,
        HEADER_HEIGHT,
        this.height - FOOTER_HEIGHT);
    addSelectableChild(this.list);

    this.equipmentButton = CyclingButtonWidget.builder(Equipment::getDisplayName)
        .values(Equipment.values())
        .initially(Equipment.ACTUAL)
        .build(
            this.width * 3 / 4 - this.width / 6,
            this.height - FOOTER_HEIGHT - PADDING - 20,
            this.width / 3,
            20,
            Text.translatable("armorstands.presets.equipment"),
            (button, value) -> setEquipmentType(value));
    addSelectableChild(this.equipmentButton);

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
            ArmorStandPresetsScreen.U_INDEX),
        ScreenFactory.create(
            ArmorStandPoseScreen.TITLE,
            ArmorStandPoseScreen.U_INDEX,
            ArmorStandPoseScreen::new),
        ScreenFactory.create(
            ArmorStandInventoryScreen.TITLE,
            ArmorStandInventoryScreen.U_INDEX,
            ArmorStandInventoryScreen::new)));
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderBackgroundTexture(0);

    this.list.render(matrixStack, mouseX, mouseY, partialTicks);
    this.equipmentButton.render(matrixStack, mouseX, mouseY, partialTicks);

    int size = MathHelper.floor(Math.min(
        this.width / 2 - 4 * PADDING,
        (this.height - HEADER_HEIGHT - FOOTER_HEIGHT - 20 - 5 * PADDING) / 2.5f));

    int x = this.width * 3 / 4;
    int y = this.height - FOOTER_HEIGHT - 5 * PADDING - 20;

    InventoryScreen.drawEntity(
        x,
        y,
        size,
        2f * (this.width / 2 - mouseX),
        2f * (y - 50 - mouseY),
        this.previewStand);

    super.render(matrixStack, mouseX, mouseY, partialTicks);
  }

  public static enum Equipment {
    ACTUAL("actual"),
    NONE("none"),
    ARMORED("armored"),
    CHARACTER("character");

    private final String id;

    private Equipment(String id) {
      this.id = id;
    }

    public Text getDisplayName() {
      return Text.translatable("armorstands.presets.equipment." + this.id);
    }

    public static Equipment next(Equipment current) {
      return values()[(current.ordinal() + 1) % values().length];
    }
  }
}
