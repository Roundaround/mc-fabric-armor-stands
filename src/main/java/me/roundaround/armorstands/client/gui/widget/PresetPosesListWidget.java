package me.roundaround.armorstands.client.gui.widget;

import java.util.List;

import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.util.PosePreset;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;

public class PresetPosesListWidget extends ElementListWidget<PresetPosesListWidget.Entry> {
  private static final int ITEM_HEIGHT = 25;

  public PresetPosesListWidget(
      MinecraftClient client,
      int width,
      int height,
      int left,
      int y1,
      int y2) {
    super(client, width, height, y1, y2, ITEM_HEIGHT);

    setLeftPos(left);
    setPoses();
  }

  public void setPoses() {
    this.clearEntries();
    for (PosePreset pose : PosePreset.values()) {
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
          20,
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
