package me.roundaround.armorstands.client.gui.widget;

import java.util.Optional;

import me.roundaround.armorstands.client.gui.screen.ArmorStandPresetsScreen;
import me.roundaround.armorstands.util.PosePreset;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class PresetPosesListWidget extends AlwaysSelectedEntryListWidget<PresetPosesListWidget.Entry> {
  private static final int ITEM_HEIGHT = 19;

  private final ArmorStandPresetsScreen screen;

  public PresetPosesListWidget(
      MinecraftClient client,
      ArmorStandPresetsScreen screen,
      int width,
      int height,
      int left,
      int y1,
      int y2) {
    super(client, width, height, y1, y2, ITEM_HEIGHT);
    this.screen = screen;

    setLeftPos(left);
    setPoses();
  }

  public void setPoses() {
    this.clearEntries();
    for (PosePreset pose : PosePreset.values()) {
      this.addEntry(new Entry(pose));
    }
  }

  public Optional<PosePreset> getSelectedAsOptional() {
    return Optional.ofNullable(getSelectedOrNull())
        .map((entry) -> entry.pose);
  }

  @Override
  public boolean isFocused() {
    return this.screen.getFocused() == this;
  }

  @Override
  public void setSelected(Entry entry) {
    super.setSelected(entry);
    this.screen.setSelectedPose(entry.pose);
  }

  @Override
  protected int getScrollbarPositionX() {
    return this.left + this.width - 6;
  }

  @Override
  public int getRowWidth() {
    return this.width - (Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4)) > 0 ? 18 : 12);
  }

  @Override
  protected int getMaxPosition() {
    return super.getMaxPosition() + 4;
  }

  public class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {
    private final PosePreset pose;

    public Entry(PosePreset pose) {
      this.pose = pose;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
      PresetPosesListWidget.this.setSelected(this);
      return false;
    }

    @Override
    public void render(
        MatrixStack matrixStack,
        int index,
        int y,
        int x,
        int entryWidth,
        int entryHeight,
        int mouseX,
        int mouseY,
        boolean hovered,
        float partialTicks) {
      Text label = this.pose.getLabel();
      PresetPosesListWidget.this.client.textRenderer.draw(
          matrixStack,
          label,
          x + 6,
          y + MathHelper.ceil(entryHeight - PresetPosesListWidget.this.client.textRenderer.fontHeight) / 2,
          0xFFFFFF);
    }

    @Override
    public Text getNarration() {
      return this.pose.getLabel();
    }
  }
}
