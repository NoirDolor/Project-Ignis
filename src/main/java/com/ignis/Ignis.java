package com.ignis;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import com.ignis.command.InitCommand;
import com.ignis.command.CommitCommand;


@Mod(Ignis.MODID)
public class Ignis {
    public static final String MODID = "ignis";

    public Ignis() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        InitCommand.register(event.getDispatcher());
        CommitCommand.register(event.getDispatcher());
    }



}