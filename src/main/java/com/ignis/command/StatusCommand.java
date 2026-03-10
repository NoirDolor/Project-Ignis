package com.ignis.command;

import com.ignis.core.CommitManager;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;

public class StatusCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("ignis")
                        .then(Commands.literal("status")
                                .executes(context -> {

                                    ServerLevel level = context.getSource().getLevel();

                                    String worldPath = level.getServer()
                                            .getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT)
                                            .toString();

                                    CommitManager manager = new CommitManager();

                                    String status = manager.getStatus(worldPath);

                                    context.getSource().sendSuccess(
                                            () -> Component.literal(status),
                                            false
                                    );

                                    return 1;
                                })
                        )
        );
    }
}