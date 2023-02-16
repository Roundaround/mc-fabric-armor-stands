package me.roundaround.armorstands.client.gui.widget;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.util.PosePreset;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;

public class PoseListWidget extends ElementListWidget<PoseListWidget.Entry> {
  private static final int ITEM_HEIGHT = 25;

  public PoseListWidget(
      MinecraftClient minecraftClient,
      int width,
      int height,
      int left,
      int y1,
      int y2) {
    super(minecraftClient, width, height, y1, y2, ITEM_HEIGHT);
    setLeftPos(left);

    setPoses(ImmutableList.copyOf(PosePreset.values()));
  }

  public void setPoses(Collection<PosePreset> poses) {
    this.clearEntries();
    for (PosePreset pose : poses) {
      this.addEntry(new PresetEntry(pose));
    }
  }

  @Override
  protected int getScrollbarPositionX() {
    return this.left + this.width - 6;
  }

  @Override
  public int getRowWidth() {
    return this.width - (Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4)) > 0 ? 18 : 12);
  }

  public abstract class Entry extends ElementListWidget.Entry<Entry> {
  }

  public class PresetEntry extends Entry {
    private final ButtonWidget button;

    public PresetEntry(PosePreset pose) {
      this.button = new ButtonWidget(
          0,
          0,
          PoseListWidget.this.getRowWidth(),
          20,
          pose.getLabel(),
          (button) -> {
            ClientNetworking.sendSetPosePacket(pose);
          });
    }

    @Override
    public List<? extends Element> children() {
      return List.of(this.button);
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
      return List.of(this.button);
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
      this.button.x = x;
      this.button.y = y;
      this.button.render(matrixStack, mouseX, mouseY, partialTicks);
    }
  }
}
