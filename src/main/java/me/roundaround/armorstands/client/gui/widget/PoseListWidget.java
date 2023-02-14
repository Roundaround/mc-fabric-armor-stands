package me.roundaround.armorstands.client.gui.widget;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.PosePreset;
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
      int x,
      int y,
      int width,
      int height) {
    super(minecraftClient, width, height, y, y + height, ITEM_HEIGHT);

    setLeftPos(x);
    setRenderBackground(false);
    setRenderHeader(false, 0);
    setRenderHorizontalShadows(false);

    setPoses(ImmutableList.copyOf(PosePreset.values()));
  }

  public void setPoses(Collection<PosePreset> poses) {
    this.clearEntries();
    for (PosePreset pose : poses) {
      this.addEntry(new Entry(pose));
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

  public class Entry extends ElementListWidget.Entry<Entry> {
    private final ButtonWidget button;

    public Entry(PosePreset pose) {
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
