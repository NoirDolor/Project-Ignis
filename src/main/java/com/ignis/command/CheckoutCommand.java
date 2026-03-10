package com.ignis.command;

import com.ignis.core.BranchManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;

public class CheckoutCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("ignis")
                        .then(Commands.literal("checkout")
                                .then(Commands.argument("branch", StringArgumentType.word())
                                        .executes(context -> {

                                            String branchName = StringArgumentType.getString(context, "branch");

                                            ServerLevel level = context.getSource().getLevel();
                                            String worldPath = level.getServer()
                                                    .getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT)
                                                    .toString();

                                            BranchManager manager = new BranchManager();

                                            String result = manager.checkoutBranch(worldPath, branchName);

                                            context.getSource().sendSuccess(() ->
                                                    Component.literal(result), true);

                                            return 1;
                                        })
                                )
                        )
        );
    }
}