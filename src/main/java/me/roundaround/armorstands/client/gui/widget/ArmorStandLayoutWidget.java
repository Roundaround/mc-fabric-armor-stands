package me.roundaround.armorstands.client.gui.widget;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.layout.LinearLayoutWidget;
import me.roundaround.roundalib.client.gui.widget.layout.SizableLayoutWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ArmorStandLayoutWidget extends SizableLayoutWidget {
  public final LinearLayoutWidget topLeft;
  public final LinearLayoutWidget bottomLeft;
  public final LinearLayoutWidget navRow;
  public final LinearLayoutWidget topRight;
  public final LinearLayoutWidget bottomRight;

  private final Screen screen;

  public ArmorStandLayoutWidget(Screen screen) {
    super(0, 0, screen.width, screen.height);

    this.screen = screen;

    this.topLeft = LinearLayoutWidget.vertical()
        .spacing(GuiUtil.PADDING / 2)
        .alignSelfTop()
        .alignSelfLeft()
        .defaultOffAxisContentAlignStart();
    this.bottomLeft = LinearLayoutWidget.vertical()
        .spacing(GuiUtil.PADDING / 2)
        .alignSelfBottom()
        .alignSelfLeft()
        .defaultOffAxisContentAlignStart();
    this.navRow = LinearLayoutWidget.horizontal().alignSelfBottom().alignSelfCenterX().spacing(GuiUtil.PADDING / 2);
    this.topRight = LinearLayoutWidget.vertical()
        .spacing(GuiUtil.PADDING / 2)
        .alignSelfTop()
        .alignSelfRight()
        .defaultOffAxisContentAlignStart();
    this.bottomRight = LinearLayoutWidget.vertical()
        .spacing(GuiUtil.PADDING / 2)
        .alignSelfBottom()
        .alignSelfRight()
        .defaultOffAxisContentAlignEnd();
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
    this.topLeft.forEachElement(consumer);
    this.bottomLeft.forEachElement(consumer);
    this.navRow.forEachElement(consumer);
    this.topRight.forEachElement(consumer);
    this.bottomRight.forEachElement(consumer);
  }

  @Override
  public void refreshPositions() {
    this.topLeft.setPosition(GuiUtil.PADDING, GuiUtil.PADDING);
    this.bottomLeft.setPosition(GuiUtil.PADDING, this.screen.height - GuiUtil.PADDING);
    this.navRow.setPosition(this.screen.width / 2, this.screen.height - 1);
    this.topRight.setPosition(this.screen.width - GuiUtil.PADDING, GuiUtil.PADDING);
    this.bottomRight.setPosition(this.screen.width - GuiUtil.PADDING, this.screen.height - GuiUtil.PADDING);

    this.topLeft.refreshPositions();
    this.bottomLeft.refreshPositions();
    this.navRow.refreshPositions();
    this.topRight.refreshPositions();
    this.bottomRight.refreshPositions();
  }
}