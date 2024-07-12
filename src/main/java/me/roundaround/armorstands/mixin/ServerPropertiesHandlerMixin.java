package me.roundaround.armorstands.mixin;

import me.roundaround.armorstands.server.ServerPropertiesWithArmorStands;
import net.minecraft.server.dedicated.AbstractPropertiesHandler;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Properties;

@Mixin(ServerPropertiesHandler.class)
public abstract class ServerPropertiesHandlerMixin extends AbstractPropertiesHandler<ServerPropertiesHandler> implements
    ServerPropertiesWithArmorStands {
  public final boolean enforceArmorStandPermissions = this.parseBoolean("enforce-armor-stand-permissions", true);

  public ServerPropertiesHandlerMixin(Properties properties) {
    super(properties);
  }

  @Override
  public boolean getEnforceArmorStandPermissions() {
    return enforceArmorStandPermissions;
  }
}
