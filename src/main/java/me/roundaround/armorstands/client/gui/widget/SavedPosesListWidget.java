package me.roundaround.armorstands.client.gui.widget;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.client.util.PoseStorage;
import me.roundaround.armorstands.util.SavedPose;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class SavedPosesListWidget extends ElementListWidget<SavedPosesListWidget.Entry> {
  private static final int ITEM_HEIGHT = 25;

  private final Screen screen;

  public SavedPosesListWidget(
      MinecraftClient client,
      Screen screen,
      int width,
      int height,
      int left,
      int y1,
      int y2) {
    super(client, width, height, y1, y2, ITEM_HEIGHT);

    this.screen = screen;

    setLeftPos(left);
  }

  public void setPoses(Map<UUID, SavedPose> poses) {
    this.clearEntries();
    for (Map.Entry<UUID, SavedPose> entry : poses.entrySet()) {
      this.addEntry(new Entry(entry.getKey(), entry.getValue()));
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
    private final UUID key;
    private final ButtonWidget selectButton;
    private final IconButtonWidget editButton;
    private final TextFieldWidget nameField;
    private final IconButtonWidget saveButton;
    private final IconButtonWidget deleteButton;
    private final IconButtonWidget cancelButton;

    private boolean editing = false;

    public Entry(UUID key, SavedPose pose) {
      this.key = key;

      this.nameField = new TextFieldWidget(
          SavedPosesListWidget.this.client.textRenderer,
          0,
          0,
          20,
          20,
          Text.translatable("armorstands.saved.name"));
      this.nameField.setText(pose.getName());

      this.saveButton = new IconButtonWidget(
          SavedPosesListWidget.this.client,
          0,
          0,
          IconButtonWidget.SAVE_ICON,
          Text.translatable("armorstands.saved.save"),
          (button) -> {
            PoseStorage.rename(this.key, this.nameField.getText());
            this.editing = false;
          });

      this.deleteButton = new IconButtonWidget(
          SavedPosesListWidget.this.client,
          0,
          0,
          IconButtonWidget.DELETE_ICON,
          Text.translatable("armorstands.saved.delete"),
          (button) -> {
            PoseStorage.remove(this.key);
            SavedPosesListWidget.this.removeEntry(this);
          });

      this.cancelButton = new IconButtonWidget(
          SavedPosesListWidget.this.client,
          0,
          0,
          IconButtonWidget.CANCEL_ICON,
          Text.translatable("armorstands.saved.cancel"),
          (button) -> {
            this.editing = false;
            SavedPosesListWidget.this.screen.setFocused(null);
          });

      this.selectButton = new ButtonWidget(
          0,
          0,
          20,
          20,
          Text.literal(pose.getName()),
          (button) -> {
            ClientNetworking.sendSetPosePacket(pose);
          });

      this.editButton = new IconButtonWidget(
          SavedPosesListWidget.this.client,
          0,
          0,
          IconButtonWidget.EDIT_ICON,
          Text.translatable("armorstands.saved.edit"),
          (button) -> {
            this.editing = true;
            SavedPosesListWidget.this.screen.setFocused(this.nameField);
          });
    }

    @Override
    public List<? extends Element> children() {
      if (editing) {
        return List.of(this.cancelButton, this.nameField, this.saveButton, this.deleteButton);
      }
      return List.of(this.selectButton, this.editButton);
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
      if (editing) {
        return List.of(this.cancelButton, this.nameField, this.saveButton, this.deleteButton);
      }
      return List.of(this.selectButton, this.editButton);
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
      if (this.editing) {
        renderEditingState(
            matrixStack,
            index,
            y,
            x,
            entryWidth,
            entryHeight,
            mouseX,
            mouseY,
            hovered,
            partialTicks);
      } else {
        renderNormalState(matrixStack,
            index,
            y,
            x,
            entryWidth,
            entryHeight,
            mouseX,
            mouseY,
            hovered,
            partialTicks);
      }
    }

    private void renderNormalState(
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
      this.selectButton.x = x;
      this.selectButton.y = y;
      this.selectButton.setWidth(entryWidth - IconButtonWidget.WIDTH - 4);
      this.selectButton.render(matrixStack, mouseX, mouseY, partialTicks);

      this.editButton.x = x + entryWidth - IconButtonWidget.WIDTH;
      this.editButton.y = y;
      this.editButton.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void renderEditingState(
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
    }
  }
}
