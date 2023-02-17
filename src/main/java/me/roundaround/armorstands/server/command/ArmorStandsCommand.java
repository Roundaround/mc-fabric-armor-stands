package me.roundaround.armorstands.server.command;

import java.util.Collection;
import java.util.stream.Stream;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.server.ArmorStandUsers;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class ArmorStandsCommand {
  private static final SimpleCommandExceptionType ADD_FAILED_EXCEPTION = new SimpleCommandExceptionType(
      Text.translatable("armorstands.commands.add.failed"));
  private static final SimpleCommandExceptionType REMOVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(
      Text.translatable("armorstands.commands.remove.failed"));
  private static final SimpleCommandExceptionType RELOAD_FAILED_EXCEPTION = new SimpleCommandExceptionType(
      Text.translatable("armorstands.commands.reload.failed"));

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    LiteralArgumentBuilder<ServerCommandSource> baseCommand = CommandManager.literal(ArmorStandsMod.MOD_ID)
        .requires(source -> source.isExecutedByPlayer()
            && (source.hasPermissionLevel(2) || source.getServer().isSingleplayer()));

    LiteralArgumentBuilder<ServerCommandSource> addSub = CommandManager
        .literal("add")
        .then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile())
            .suggests((context, builder) -> {
              PlayerManager playerManager = context.getSource().getServer().getPlayerManager();
              Stream<String> playerNames = playerManager.getPlayerList()
                  .stream()
                  .filter((player) -> !ArmorStandUsers.isExplicitlyListed(player))
                  .map((player) -> player.getGameProfile().getName());
              return CommandSource.suggestMatching(playerNames, builder);
            })
            .executes((context) -> executeAdd(context.getSource(),
                GameProfileArgumentType.getProfileArgument(context, "targets"))));

    LiteralArgumentBuilder<ServerCommandSource> removeSub = CommandManager
        .literal("remove")
        .then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile())
            .suggests((context, builder) -> {
              return CommandSource.suggestMatching(ArmorStandUsers.getNames(), builder);
            })
            .executes((context) -> executeRemove(context.getSource(),
                GameProfileArgumentType.getProfileArgument(context, "targets"))));

    LiteralArgumentBuilder<ServerCommandSource> reloadSub = CommandManager
        .literal("reload")
        .executes((context) -> executeReload(context.getSource()));

    LiteralArgumentBuilder<ServerCommandSource> finalCommand = baseCommand
        .then(addSub)
        .then(removeSub)
        .then(reloadSub);

    dispatcher.register(finalCommand);
  }

  private static int executeAdd(ServerCommandSource source, Collection<GameProfile> targets)
      throws CommandSyntaxException {
    Whitelist whitelist = ArmorStandUsers.WHITELIST;
    int added = 0;

    for (GameProfile profile : targets) {
      if (whitelist.isAllowed(profile)) {
        continue;
      }

      WhitelistEntry entry = new WhitelistEntry(profile);
      whitelist.add(entry);
      source.sendFeedback(Text.translatable("armorstands.commands.add.success", Texts.toText(profile)), true);
      added++;
    }

    if (added == 0) {
      throw ADD_FAILED_EXCEPTION.create();
    }

    return added;
  }

  private static int executeRemove(ServerCommandSource source, Collection<GameProfile> targets)
      throws CommandSyntaxException {
    Whitelist whitelist = ArmorStandUsers.WHITELIST;
    int removed = 0;

    for (GameProfile profile : targets) {
      if (!whitelist.isAllowed(profile)) {
        continue;
      }

      WhitelistEntry entry = new WhitelistEntry(profile);
      whitelist.remove(entry);
      source.sendFeedback(Text.translatable("armorstands.commands.remove.success", Texts.toText(profile)), true);
      removed++;
    }

    if (removed == 0) {
      throw REMOVE_FAILED_EXCEPTION.create();
    }

    return removed;
  }

  private static int executeReload(ServerCommandSource source) throws CommandSyntaxException {
    try {
      ArmorStandUsers.WHITELIST.load();
    } catch (Exception exception) {
      ArmorStandsMod.LOGGER.warn("Failed to reload armor stand users: ", exception);
      throw RELOAD_FAILED_EXCEPTION.create();
    }

    source.sendFeedback(Text.translatable("armorstands.commands.reload.success"), true);
    return 1;
  }
}
