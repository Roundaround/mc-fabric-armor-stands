package me.roundaround.armorstands.client.gui.screen;

import java.util.ArrayList;
import java.util.List;

import me.roundaround.armorstands.client.gui.widget.IconButtonWidget;
import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.client.gui.widget.PresetPoseButtonWidget;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.PosePreset;
import me.roundaround.armorstands.util.PosePreset.Category;
import me.roundaround.armorstands.util.PosePreset.Source;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ArmorStandPresetsScreen extends AbstractArmorStandScreen {
  private static final int CONTROL_WIDTH = 120;
  private static final int CONTROL_HEIGHT = 16;
  private static final int BUTTONS_PER_PAGE = 6;

  private final ArrayList<PresetPoseButtonWidget> presetButtons = new ArrayList<>();

  private IconButtonWidget<ArmorStandPresetsScreen> prevPageButton;
  private IconButtonWidget<ArmorStandPresetsScreen> nextPageButton;
  private LabelWidget pageLabel;
  private int page = 0;
  private Source source = Source.ALL;
  private Category category = Category.ALL;
  private List<PosePreset> matchingPresets = new ArrayList<>();

  public ArmorStandPresetsScreen(ArmorStandScreenHandler handler) {
    super(handler, ScreenType.PRESETS.getDisplayName());
    this.supportsUndoRedo = true;
  }

  @Override
  public ScreenType getScreenType() {
    return ScreenType.PRESETS;
  }

  @Override
  protected void initStart() {
    super.initStart();

    this.presetButtons.clear();
  }

  @Override
  protected void initRight() {
    super.initRight();

    addLabel(LabelWidget.builder(
        Text.translatable("armorstands.presets.source.label"),
        this.width - SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD
            - (BUTTONS_PER_PAGE + 2) * CONTROL_HEIGHT
            - (BUTTONS_PER_PAGE + 3) * BETWEEN_PAD
            - IconButtonWidget.HEIGHT
            - CONTROL_HEIGHT
            - LabelWidget.HEIGHT_WITH_PADDING
            - 3 * BETWEEN_PAD)
        .alignedBottom()
        .justifiedRight()
        .shiftForPadding()
        .build());
    addDrawableChild(CyclingButtonWidget.builder(Source::getDisplayName)
        .values(Source.getSources())
        .initially(Source.ALL)
        .omitKeyText()
        .build(
            this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH,
            this.height - SCREEN_EDGE_PAD
                - (BUTTONS_PER_PAGE + 2) * CONTROL_HEIGHT
                - (BUTTONS_PER_PAGE + 2) * BETWEEN_PAD
                - IconButtonWidget.HEIGHT
                - CONTROL_HEIGHT
                - LabelWidget.HEIGHT_WITH_PADDING
                - 3 * BETWEEN_PAD,
            CONTROL_WIDTH,
            CONTROL_HEIGHT,
            Text.translatable("armorstands.presets.source.label"),
            (button, source) -> {
              filter(source);
            }));

    addLabel(LabelWidget.builder(
        Text.translatable("armorstands.presets.category.label"),
        this.width - SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD
            - (BUTTONS_PER_PAGE + 2) * CONTROL_HEIGHT
            - (BUTTONS_PER_PAGE + 3) * BETWEEN_PAD
            - IconButtonWidget.HEIGHT)
        .alignedBottom()
        .justifiedRight()
        .shiftForPadding()
        .build());
    addDrawableChild(CyclingButtonWidget.builder(Category::getDisplayName)
        .values(Category.getCategories())
        .initially(Category.ALL)
        .omitKeyText()
        .build(
            this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH,
            this.height - SCREEN_EDGE_PAD
                - (BUTTONS_PER_PAGE + 2) * CONTROL_HEIGHT
                - (BUTTONS_PER_PAGE + 2) * BETWEEN_PAD
                - IconButtonWidget.HEIGHT,
            CONTROL_WIDTH,
            CONTROL_HEIGHT,
            Text.translatable("armorstands.presets.category.label"),
            (button, category) -> {
              filter(category);
            }));

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

    int maxPage = MathHelper.ceil(
        PosePreset.getPresets(this.source, this.category).size() / (float) BUTTONS_PER_PAGE) - 1;
    this.pageLabel = addLabel(LabelWidget.builder(
        Text.translatable("armorstands.presets.page", this.page + 1, maxPage + 1),
        this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH / 2,
        this.height - SCREEN_EDGE_PAD - IconButtonWidget.HEIGHT / 2)
        .alignedMiddle()
        .justifiedCenter()
        .build());
  }

  @Override
  protected void initEnd() {
    updateFilters();
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    if (isMouseOverList(mouseX, mouseY)) {
      if (amount < 0) {
        nextPage();
      } else {
        previousPage();
      }
      return true;
    }
    return super.mouseScrolled(mouseX, mouseY, amount);
  }

  private void filter(Source source) {
    this.source = source;
    updateFilters();
  }

  private void filter(Category category) {
    this.category = category;
    updateFilters();
  }

  private void updateFilters() {
    this.matchingPresets = PosePreset.getPresets(this.source, this.category);
    setPage(0);
  }

  private void setPage(int page) {
    int maxPage = Math.max(0, MathHelper.ceil(this.matchingPresets.size() / (float) BUTTONS_PER_PAGE) - 1);

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

  private boolean isMouseOverList(double mouseX, double mouseY) {
    return mouseX >= this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH
        && mouseX < this.width - SCREEN_EDGE_PAD
        && mouseY >= this.height
            - SCREEN_EDGE_PAD
            - IconButtonWidget.HEIGHT
            - BUTTONS_PER_PAGE * CONTROL_HEIGHT
            - (BUTTONS_PER_PAGE + 1) * BETWEEN_PAD
        && mouseY < this.height
            - SCREEN_EDGE_PAD
            - IconButtonWidget.HEIGHT;
  }
}
