package me.roundaround.armorstands.client.gui.widget;

import net.minecraft.client.gui.Drawable;

public interface DrawableBuilder<T extends Drawable> {
  T build();
}
