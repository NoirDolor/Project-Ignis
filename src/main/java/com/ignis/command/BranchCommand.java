package com.ignis.command;

import com.ignis.core.BranchManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;

public class BranchCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("ignis")
                        .then(Commands.literal("branch")

                                // listing the branches
                                .executes(context -> {

                                    ServerLevel level = context.getSource().getLevel();

                                    String worldPath = level.getServer()
                                            .getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT)
                                            .toString();

                                    BranchManager manager = new BranchManager();

                                    String result = manager.listBranches(worldPath);

                                    context.getSource().sendSuccess(
                                            () -> Component.literal(result),
                                            false
                                    );

                                    return 1;
                                })


                                // making branch
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .executes(context -> {

                                            String branchName = StringArgumentType.getString(context, "name");

                                            ServerLevel level = context.getSource().getLevel();
                                            String worldPath = level.getServer()
                                                    .getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT)
                                                    .toString();

                                            BranchManager manager = new BranchManager();

                                            String result = manager.createBranch(worldPath, branchName);

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal(result),
                                                    true
                                            );

                                            return 1;
                                        })
                                )


                                // Dleting em
                                .then(Commands.literal("delete")
                                        .then(Commands.argument("name", StringArgumentType.word())
                                                .executes(context -> {

                                                    String branchName = StringArgumentType.getString(context, "name");

                                                    ServerLevel level = context.getSource().getLevel();

                                                    String worldPath = level.getServer()
                                                            .getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT)
                                                            .toString();

                                                    BranchManager manager = new BranchManager();

                                                    String result = manager.deleteBranch(worldPath, branchName);

                                                    context.getSource().sendSuccess(
                                                            () -> Component.literal(result),
                                                            true
                                                    );

                                                    return 1;
                                                })
                                        )
                                )
                        )
        );
    }
}