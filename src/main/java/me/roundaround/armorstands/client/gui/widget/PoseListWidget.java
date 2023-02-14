package me.roundaround.armorstands.client.gui.widget;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import me.roundaround.armorstands.client.gui.ArmorStandState;
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
  private static final int BUTTON_WIDTH = 100;

  private final ArmorStandState state;

  public PoseListWidget(
      MinecraftClient minecraftClient,
      ArmorStandState state,
      int x,
      int y,
      int width,
      int height) {
    super(minecraftClient, width, height, y, y + height, ITEM_HEIGHT);
    this.state = state;

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
  public int getRowWidth() {
    return 140;
  }

  public class Entry extends ElementListWidget.Entry<Entry> {
    private final ButtonWidget button;

    public Entry(PosePreset pose) {
      this.button = new ButtonWidget(
          0,
          0,
          BUTTON_WIDTH,
          20,
          pose.getLabel(),
          (button) -> {
            ClientNetworking.sendSetPosePacket(
                PoseListWidget.this.state.getArmorStand(),
                PosePreset.ATTENTION);
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
      this.button.y = y;
      this.button.render(matrixStack, mouseX, mouseY, partialTicks);
    }
  }
}
