package me.roundaround.armorstands.mixin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.roundaround.armorstands.client.ArmorStandsClientMod;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
  private static final Pattern PATTERN = Pattern.compile("I am a doofus!?", Pattern.CASE_INSENSITIVE);

  @Inject(method = "sendChatMessage(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
  public void sendChatMessage(String message, Text preview, CallbackInfo info) {
    if (!ArmorStandsClientMod.isAprilFirst()) {
      return;
    }

    Matcher matcher = PATTERN.matcher(message);
    if (!matcher.find()) {
      return;
    }

    if (!ArmorStandsClientMod.isTheDoofusNugget((ClientPlayerEntity) (Object) this)) {
      return;
    }

    ((ClientPlayerEntity) (Object) this).sendMessage(Text.literal("Indeed, you are a doofus!"), true);
    ArmorStandsClientMod.admit();
    info.cancel();
  }
}
