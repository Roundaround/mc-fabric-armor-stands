package me.roundaround.armorstands.client.gui.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import me.roundaround.armorstands.client.gui.widget.IconButtonWidget;
import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.client.gui.widget.PresetPoseButtonWidget;
import me.roundaround.armorstands.client.util.LastUsedScreen.ScreenType;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.PosePreset;
import me.roundaround.armorstands.util.PosePreset.Source;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ArmorStandPresetsScreen
    extends AbstractArmorStandScreen {
  public static final Text TITLE = Text.translatable("armorstands.screen.presets");
  public static final int U_INDEX = 4;

  private static final int CONTROL_WIDTH = 120;
  private static final int CONTROL_HEIGHT = 16;
  private static final int SCREEN_EDGE_PAD = 4;
  private static final int BETWEEN_PAD = 2;
  private static final int BUTTONS_PER_PAGE = 6;

  private final ArrayList<PresetPoseButtonWidget> presetButtons = new ArrayList<>();

  private IconButtonWidget<ArmorStandPresetsScreen> prevPageButton;
  private IconButtonWidget<ArmorStandPresetsScreen> nextPageButton;
  private LabelWidget pageLabel;
  private int page = 0;
  private Source source = Source.ALL;
  private List<PosePreset> matchingPresets = new ArrayList<>();

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
  public ScreenConstructor<?> getNextScreen() {
    return ArmorStandPoseScreen::new;
  }

  @Override
  public ScreenConstructor<?> getPreviousScreen() {
    return ArmorStandRotateScreen::new;
  }

  @Override
  public void init() {
    super.init();

    addDrawable(LabelWidget.builder(
        Text.translatable("armorstands.presets.source.label"),
        SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD - 20 - BETWEEN_PAD)
        .alignedBottom()
        .justifiedLeft()
        .shiftForPadding()
        .build());
    addDrawableChild(CyclingButtonWidget.builder(Source::getDisplayName)
        .values(Source.values())
        .initially(Source.ALL)
        .omitKeyText()
        .build(
            SCREEN_EDGE_PAD,
            this.height - SCREEN_EDGE_PAD - 20,
            CONTROL_WIDTH,
            20,
            Text.translatable("armorstands.presets.source.label"),
            (button, source) -> {
              filter(source);
            }));

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
      this.presetButtons.add(addDrawableChild(new PresetPoseButtonWidget(
          this,
          this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH,
          this.height - SCREEN_EDGE_PAD - i * CONTROL_HEIGHT - i * BETWEEN_PAD - IconButtonWidget.HEIGHT,
          CONTROL_WIDTH,
          CONTROL_HEIGHT)));
    }

    this.prevPageButton = addDrawableChild(new IconButtonWidget<>(
        this.client,
        this,
        this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH,
        this.height - SCREEN_EDGE_PAD - IconButtonWidget.HEIGHT,
        12,
        Text.translatable("armorstands.presets.previous"),
        (button) -> previousPage()));
    this.nextPageButton = addDrawableChild(new IconButtonWidget<>(
        this.client,
        this,
        this.width - SCREEN_EDGE_PAD - IconButtonWidget.WIDTH,
        this.height - SCREEN_EDGE_PAD - IconButtonWidget.HEIGHT,
        13,
        Text.translatable("armorstands.presets.next"),
        (button) -> nextPage()));

    int maxPage = MathHelper.ceil(PosePreset.values().length / (float) BUTTONS_PER_PAGE) - 1;
    this.pageLabel = addDrawable(LabelWidget.builder(
        Text.translatable("armorstands.presets.page", this.page + 1, maxPage + 1),
        this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH / 2,
        this.height - SCREEN_EDGE_PAD - IconButtonWidget.HEIGHT / 2)
        .alignedMiddle()
        .justifiedCenter()
        .build());

    updateFilters();
  }

  private void filter(Source source) {
    this.source = source;
    updateFilters();
  }

  private void updateFilters() {
    this.matchingPresets = Arrays.stream(PosePreset.values())
        .filter((preset) -> {
          return this.source.matches(preset.getSource());
        })
        .collect(Collectors.toList());

    setPage(0);
  }

  private void setPage(int page) {
    int maxPage = MathHelper.ceil(this.matchingPresets.size() / (float) BUTTONS_PER_PAGE) - 1;

    this.page = page;
    List<PosePreset> presets = this.matchingPresets
        .subList(
            page * BUTTONS_PER_PAGE,
            Math.min((page + 1) * BUTTONS_PER_PAGE, this.matchingPresets.size()));

    for (int i = 0; i < BUTTONS_PER_PAGE; i++) {
      if (i < presets.size()) {
        this.presetButtons.get(i).setPose(presets.get(i));
        this.presetButtons.get(i).visible = true;
      } else {
        this.presetButtons.get(i).visible = false;
      }
    }

    if (this.presetButtons.contains(getFocused())) {
      setFocused(this.presetButtons.get(0));
    }

    this.prevPageButton.active = this.page > 0;
    this.nextPageButton.active = this.page < maxPage;
    this.pageLabel.setText(Text.translatable("armorstands.presets.page", this.page + 1, maxPage + 1));
  }

  private void nextPage() {
    int maxPage = MathHelper.ceil(this.matchingPresets.size() / (float) BUTTONS_PER_PAGE) - 1;
    if (this.page < maxPage) {
      setPage(this.page + 1);
    }
  }

  private void previousPage() {
    if (this.page > 0) {
      setPage(this.page - 1);
    }
  }
}
