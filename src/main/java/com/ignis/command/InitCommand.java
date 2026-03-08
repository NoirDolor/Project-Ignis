package com.ignis.command;

import com.ignis.core.RepositoryManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;

public class InitCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("ignis")
                        .then(Commands.literal("init")
                                .executes(context -> {

                                    ServerLevel level = context.getSource().getLevel();
                                    String worldPath = level.getServer().getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT).toString();

                                    RepositoryManager repo = new RepositoryManager();
                                    repo.initRepository(worldPath);

                                    context.getSource().sendSuccess(() ->
                                            Component.literal("Ignis repository initialized."), true);

                                    return 1;
                                })
                        )
        );
    }
}