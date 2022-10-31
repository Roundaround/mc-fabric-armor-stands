package me.roundaround.armorstands.util.actions;

import me.roundaround.armorstands.util.Clipboard;
import me.roundaround.armorstands.util.Clipboard.Entry;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class ClipboardPasteAction extends SimpleArmorStandAction<Clipboard.Entry> {
  private ClipboardPasteAction(Clipboard.Entry entry) {
    super(entry);
  }

  public static ClipboardPasteAction create(Clipboard.Entry entry) {
    return new ClipboardPasteAction(entry);
  }

  @Override
  public Text getName(ArmorStandEntity armorStand) {
    return Text.translatable("armorstands.action.paste");
  }

  @Override
  protected Entry get(ArmorStandEntity armorStand) {
    return Clipboard.Entry.everything(armorStand);
  }
}
