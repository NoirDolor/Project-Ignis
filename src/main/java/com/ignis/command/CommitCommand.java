package com.ignis.command;

import com.ignis.core.CommitManager;
import com.ignis.core.RepositoryValidator;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;

public class CommitCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("ignis")
                        .then(Commands.literal("commit")
                                .then(Commands.argument("message", StringArgumentType.greedyString())
                                        .executes(context -> {

                                            String message = StringArgumentType.getString(context, "message");

                                            ServerLevel level = context.getSource().getLevel();
                                            String worldPath = level.getServer()
                                                    .getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT)
                                                    .toString();

                                            if (!RepositoryValidator.isRepository(worldPath)) {

                                                context.getSource().sendFailure(
                                                        Component.literal("Ignis repository not initialized. Run /ignis init first.")
                                                );

                                                return 0;
                                            }

                                            // add this bit in other commands too , just not nowww!! ;)
                                            if (message.trim().isEmpty()) {

                                                context.getSource().sendFailure(
                                                        Component.literal("Commit message cannot be empty.")
                                                );

                                                return 0;
                                            }

                                            CommitManager manager = new CommitManager();
                                            manager.createCommit(worldPath, message);

                                            context.getSource().sendSuccess(() ->
                                                    Component.literal("Ignis commit created."), true);

                                            return 1;
                                        })
                                )
                        )
        );
    }
}