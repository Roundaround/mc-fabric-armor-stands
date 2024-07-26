package me.roundaround.armorstands.client.gui.screen;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.gui.widget.PresetPoseButtonWidget;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.PosePreset;
import me.roundaround.armorstands.util.PosePreset.Category;
import me.roundaround.armorstands.util.PosePreset.Source;
import me.roundaround.roundalib.asset.icon.BuiltinIcon;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.linear.LinearLayoutWidget;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class ArmorStandPresetsScreen extends AbstractArmorStandScreen {
  private static final int CONTROL_WIDTH = 120;
  private static final int CONTROL_HEIGHT = 16;
  private static final int BUTTONS_PER_PAGE = 6;

  private final ArrayList<PresetPoseButtonWidget> presetButtons = new ArrayList<>();

  private IconButtonWidget prevPageButton;
  private IconButtonWidget nextPageButton;
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
  protected void populateLayout() {
    super.populateLayout();

    this.initBottomLeft();
    this.initBottomRight();
  }

  @Override
  protected void collectElements() {
    this.updateFilters();
    super.collectElements();
  }

  private void initBottomLeft() {
    this.layout.bottomLeft.spacing(3 * GuiUtil.PADDING);

    LinearLayoutWidget first = LinearLayoutWidget.vertical()
        .spacing(GuiUtil.PADDING / 2)
        .defaultOffAxisContentAlignStart();
    first.add(LabelWidget.builder(this.textRenderer, Text.translatable("armorstands.presets.source.label")).build());
    first.add(CyclingButtonWidget.builder(Source::getDisplayName)
        .values(Source.getSources())
        .initially(Source.ALL)
        .omitKeyText()
        .build(0, 0, CONTROL_WIDTH, CONTROL_HEIGHT, Text.translatable("armorstands.presets.source.label"),
            (button, source) -> filter(source)
        ));
    this.layout.bottomLeft.add(first);

    LinearLayoutWidget second = LinearLayoutWidget.vertical()
        .spacing(GuiUtil.PADDING / 2)
        .defaultOffAxisContentAlignStart();
    second.add(LabelWidget.builder(this.textRenderer, Text.translatable("armorstands.presets.category.label")).build());
    second.add(CyclingButtonWidget.builder(Category::getDisplayName)
        .values(Category.getCategories())
        .initially(Category.ALL)
        .omitKeyText()
        .build(0, 0, CONTROL_WIDTH, CONTROL_HEIGHT, Text.translatable("armorstands.presets.category.label"),
            (button, category) -> filter(category)
        ));
    this.layout.bottomLeft.add(second);
  }

  private void initBottomRight() {
    for (int i = BUTTONS_PER_PAGE; i > 0; i--) {
      PresetPoseButtonWidget button = this.layout.bottomRight.add(
          new PresetPoseButtonWidget(CONTROL_WIDTH, CONTROL_HEIGHT));
      this.presetButtons.add(button);
    }

    LinearLayoutWidget pagination = LinearLayoutWidget.horizontal()
        .spacing(GuiUtil.PADDING / 2)
        .defaultOffAxisContentAlignCenter();
    this.prevPageButton = pagination.add(IconButtonWidget.builder(BuiltinIcon.PREV_18, ArmorStandsMod.MOD_ID)
        .vanillaSize()
        .messageAndTooltip(Text.translatable("armorstands.presets.previous"))
        .onPress((button) -> previousPage())
        .build());

    int maxPage =
        MathHelper.ceil(PosePreset.getPresets(this.source, this.category).size() / (float) BUTTONS_PER_PAGE) - 1;
    this.pageLabel = pagination.add(LabelWidget.builder(this.textRenderer,
            Text.translatable("armorstands.presets.page", this.page + 1, maxPage + 1)
        )
        .alignTextCenterX()
        .build(), (parent, self) -> self.setWidth(CONTROL_WIDTH - 2 * IconButtonWidget.SIZE_V - GuiUtil.PADDING));

    this.nextPageButton = pagination.add(IconButtonWidget.builder(BuiltinIcon.NEXT_18, ArmorStandsMod.MOD_ID)
        .vanillaSize()
        .messageAndTooltip(Text.translatable("armorstands.presets.next"))
        .onPress((button) -> nextPage())
        .build());

    this.layout.bottomRight.add(pagination);
  }

  @Override
  public boolean mouseScrolled(
      double mouseX, double mouseY, double horizontalAmount, double verticalAmount
  ) {
    if (this.layout.bottomRight.getBounds().contains(mouseX, mouseY)) {
      if (verticalAmount < 0) {
        nextPage();
      } else {
        previousPage();
      }
      return true;
    }
    return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
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
    List<PosePreset> presets = this.matchingPresets.subList(page * BUTTONS_PER_PAGE,
        Math.min((page + 1) * BUTTONS_PER_PAGE, this.matchingPresets.size())
    );

    for (int i = 0; i < BUTTONS_PER_PAGE; i++) {
      if (i < presets.size()) {
        this.presetButtons.get(i).setPose(presets.get(i));
        this.presetButtons.get(i).visible = true;
      } else {
        this.presetButtons.get(i).visible = false;
      }
    }

    if (!this.presetButtons.contains(this.getFocused())) {
      this.setFocused(this.presetButtons.getFirst());
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
