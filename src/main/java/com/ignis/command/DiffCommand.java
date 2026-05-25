package com.ignis.command;

import com.ignis.core.DiffManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;

public class DiffCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("ignis")
                        .then(Commands.literal("diff")

                                // Usage 1: /ignis diff <commitA> <commitB>
                                // Compares two committed snapshots directly
                                .then(Commands.argument("commitA", StringArgumentType.word())
                                        .then(Commands.argument("commitB", StringArgumentType.word())
                                                .executes(context -> {
                                                    String commitA = StringArgumentType.getString(context, "commitA");
                                                    String commitB = StringArgumentType.getString(context, "commitB");

                                                    ServerLevel level = context.getSource().getLevel();
                                                    String worldPath = level.getServer()
                                                            .getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT)
                                                            .toString();

                                                    DiffManager manager = new DiffManager();
                                                    String result = manager.diff(worldPath, commitA, commitB);

                                                    context.getSource().sendSuccess(
                                                            () -> Component.literal(result),
                                                            false
                                                    );
                                                    return 1;
                                                })
                                        )

                                        // Usage 2: /ignis diff <commitId>
                                        // Compares a commit snapshot against the current live world
                                        .executes(context -> {
                                            String commitId = StringArgumentType.getString(context, "commitA");

                                            ServerLevel level = context.getSource().getLevel();
                                            String worldPath = level.getServer()
                                                    .getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT)
                                                    .toString();

                                            DiffManager manager = new DiffManager();
                                            String result = manager.diffWithCurrent(worldPath, commitId);

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal(result),
                                                    false
                                            );
                                            return 1;
                                        })
                                )
                        )
        );
    }
}