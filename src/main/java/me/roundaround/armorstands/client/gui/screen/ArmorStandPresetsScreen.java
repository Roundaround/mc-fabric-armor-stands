package me.roundaround.armorstands.client.gui.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.roundaround.armorstands.client.gui.widget.IconButtonWidget;
import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.client.gui.widget.PresetPoseButtonWidget;
import me.roundaround.armorstands.client.util.LastUsedScreen.ScreenType;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.PosePreset;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ArmorStandPresetsScreen
    extends AbstractArmorStandScreen {
  public static final Text TITLE = Text.translatable("armorstands.screen.presets");
  public static final int U_INDEX = 4;

  private static final int CONTROL_WIDTH = 100;
  private static final int CONTROL_HEIGHT = 20;
  private static final int SCREEN_EDGE_PAD = 4;
  private static final int BETWEEN_PAD = 2;
  private static final int BUTTONS_PER_PAGE = 6;
  private static final int MAX_PAGE = MathHelper.ceil(PosePreset.values().length / (float) BUTTONS_PER_PAGE) - 1;

  private final ArrayList<PresetPoseButtonWidget> presetButtons = new ArrayList<>();

  private IconButtonWidget<ArmorStandPresetsScreen> prevPageButton;
  private IconButtonWidget<ArmorStandPresetsScreen> nextPageButton;
  private LabelWidget pageLabel;
  private int page = 0;

  public ArmorStandPresetsScreen(
      ArmorStandScreenHandler handler,
      ArmorStandEntity armorStand) {
    super(handler, TITLE, armorStand);

    this.supportsUndoRedo = true;
  }

  @Override
  public ScreenType getScreenType() {
    return ScreenType.PRESETS;
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
            ArmorStandPresetsScreen.U_INDEX),
        ScreenFactory.create(
            ArmorStandPoseScreen.TITLE,
            ArmorStandPoseScreen.U_INDEX,
            ArmorStandPoseScreen::new),
        ScreenFactory.create(
            ArmorStandInventoryScreen.TITLE,
            ArmorStandInventoryScreen.U_INDEX,
            ArmorStandInventoryScreen::new)));

    this.presetButtons.clear();

    for (int i = BUTTONS_PER_PAGE; i > 0; i--) {
      this.presetButtons.add(addSelectableChild(new PresetPoseButtonWidget(
          this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH,
          this.height - SCREEN_EDGE_PAD - (i + 1) * CONTROL_HEIGHT - i * BETWEEN_PAD,
          CONTROL_WIDTH,
          CONTROL_HEIGHT)));
    }

    this.prevPageButton = addSelectableChild(new IconButtonWidget<>(
        this.client,
        this,
        this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH,
        this.height - SCREEN_EDGE_PAD - CONTROL_HEIGHT,
        12,
        Text.translatable("armorstands.presets.previous"),
        (button) -> previousPage()));
    this.nextPageButton = addSelectableChild(new IconButtonWidget<>(
        this.client,
        this,
        this.width - SCREEN_EDGE_PAD - IconButtonWidget.WIDTH,
        this.height - SCREEN_EDGE_PAD - CONTROL_HEIGHT,
        13,
        Text.translatable("armorstands.presets.next"),
        (button) -> nextPage()));

    this.pageLabel = addDrawable(LabelWidget.builder(
        Text.translatable("armorstands.presets.page", this.page + 1, MAX_PAGE + 1),
        this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH / 2,
        this.height - SCREEN_EDGE_PAD - CONTROL_HEIGHT / 2)
        .alignedMiddle()
        .justifiedCenter()
        .build());

    setPage(0);
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    super.render(matrixStack, mouseX, mouseY, partialTicks);

    this.presetButtons.forEach((button) -> {
      button.render(matrixStack, mouseX, mouseY, partialTicks);
    });

    this.prevPageButton.render(matrixStack, mouseX, mouseY, partialTicks);
    this.nextPageButton.render(matrixStack, mouseX, mouseY, partialTicks);
  }

  private void setPage(int page) {
    this.page = page;
    List<PosePreset> presets = Arrays.asList(PosePreset.values())
        .subList(
            page * BUTTONS_PER_PAGE,
            Math.min((page + 1) * BUTTONS_PER_PAGE, PosePreset.values().length));

    for (int i = 0; i < BUTTONS_PER_PAGE; i++) {
      if (i < presets.size()) {
        this.presetButtons.get(i).setPose(presets.get(i));
        this.presetButtons.get(i).visible = true;
      } else {
        this.presetButtons.get(i).visible = false;
      }
    }

    this.prevPageButton.active = this.page > 0;
    this.nextPageButton.active = this.page < MAX_PAGE;
    this.pageLabel.setText(Text.translatable("armorstands.presets.page", this.page + 1, MAX_PAGE + 1));
  }

  private void nextPage() {
    if (this.page < MAX_PAGE) {
      setPage(this.page + 1);
    }
  }

  private void previousPage() {
    if (this.page > 0) {
      setPage(this.page - 1);
    }
  }
}
