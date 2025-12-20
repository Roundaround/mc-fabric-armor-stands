package me.roundaround.armorstands.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.server.ArmorStandUsers;
import net.minecraft.command.CommandSource;
import net.minecraft.command.DefaultPermissions;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.stream.Stream;

public class ArmorStandsCommand {
  private static final SimpleCommandExceptionType ADD_FAILED_EXCEPTION =
      new SimpleCommandExceptionType(Text.translatable(
      "armorstands.commands.add.failed"));
  private static final SimpleCommandExceptionType REMOVE_FAILED_EXCEPTION =
      new SimpleCommandExceptionType(Text.translatable(
      "armorstands.commands.remove.failed"));
  private static final SimpleCommandExceptionType RELOAD_FAILED_EXCEPTION =
      new SimpleCommandExceptionType(Text.translatable(
      "armorstands.commands.reload.failed"));

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    LiteralArgumentBuilder<ServerCommandSource> baseCommand = CommandManager.literal(ArmorStandsMod.MOD_ID)
        .requires(source -> source.getPermissions().hasPermission(DefaultPermissions.GAMEMASTERS));

    LiteralArgumentBuilder<ServerCommandSource> addSub = CommandManager.literal("add")
        .then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile())
            .suggests((context, builder) -> {
              PlayerManager playerManager = context.getSource().getServer().getPlayerManager();
              Stream<String> playerNames = playerManager.getPlayerList()
                  .stream()
                  .filter((player) -> !ArmorStandUsers.contains(player.getPlayerConfigEntry()))
                  .map((player) -> player.getGameProfile().name());
              return CommandSource.suggestMatching(playerNames, builder);
            })
            .executes((context) -> executeAdd(
                context.getSource(),
                GameProfileArgumentType.getProfileArgument(context, "targets")
            )));

    LiteralArgumentBuilder<ServerCommandSource> removeSub = CommandManager.literal("remove")
        .then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile())
            .suggests((context, builder) -> CommandSource.suggestMatching(
                ArmorStandUsers.listNames(context.getSource()
                    .getServer()), builder
            ))
            .executes((context) -> executeRemove(
                context.getSource(),
                GameProfileArgumentType.getProfileArgument(context, "targets")
            )));

    LiteralArgumentBuilder<ServerCommandSource> reloadSub = CommandManager.literal("reload")
        .executes((context) -> executeReload(context.getSource()));

    LiteralArgumentBuilder<ServerCommandSource> finalCommand = baseCommand.then(addSub).then(removeSub).then(reloadSub);

    dispatcher.register(finalCommand);
  }

  private static int executeAdd(ServerCommandSource source, Collection<PlayerConfigEntry> targets)
      throws CommandSyntaxException {
    int added = 0;

    for (PlayerConfigEntry target : targets) {
      if (ArmorStandUsers.contains(target)) {
        continue;
      }

      ArmorStandUsers.add(target);
      source.sendFeedback(() -> Text.translatable("armorstands.commands.add.success", target.name()), true);
      added++;
    }

    if (added == 0) {
      throw ADD_FAILED_EXCEPTION.create();
    }

    return added;
  }

  private static int executeRemove(ServerCommandSource source, Collection<PlayerConfigEntry> targets)
      throws CommandSyntaxException {
    int removed = 0;

    for (PlayerConfigEntry target : targets) {
      if (!ArmorStandUsers.contains(target)) {
        continue;
      }

      ArmorStandUsers.remove(target);
      source.sendFeedback(() -> Text.translatable("armorstands.commands.remove.success", target.name()), true);
      removed++;
    }

    if (removed == 0) {
      throw REMOVE_FAILED_EXCEPTION.create();
    }

    return removed;
  }

  private static int executeReload(ServerCommandSource source) throws CommandSyntaxException {
    try {
      ArmorStandUsers.reload();
    } catch (Exception exception) {
      ArmorStandsMod.LOGGER.warn("Failed to reload armor stand users: ", exception);
      throw RELOAD_FAILED_EXCEPTION.create();
    }

    source.sendFeedback(() -> Text.translatable("armorstands.commands.reload.success"), true);
    return 1;
  }
}
