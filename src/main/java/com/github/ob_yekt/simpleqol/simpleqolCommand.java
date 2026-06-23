package com.github.ob_yekt.simpleqol;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;


public class simpleqolCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
                dispatcher.register(
                        Commands.literal("simpleqol")
                                .requires(source -> source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.MODERATORS)))
                                .then(Commands.literal("reload")
                                        .executes(ctx -> {
                                            // Calls your existing logic to read the file from disk
                                            ConfigManager.load();
                                            ctx.getSource().sendSuccess(() -> Component.literal("§a simpleqol configuration reloaded from disk!"), true);
                                            return 1;
                                        }))
                .then(Commands.literal("daylength")
                        .then(Commands.literal("get")
                                .executes(ctx -> {
                                    long minutes = TimeController.getDayTicks() / 20 / 60;
                                    ctx.getSource().sendSuccess(() -> Component.literal("Day length: " + minutes + " minutes."), false);
                                    return 1;
                                }))
                        .then(Commands.literal("set")
                                .then(Commands.argument("minutes", IntegerArgumentType.integer(1, 1440))
                                        .executes(ctx -> {
                                            int minutes = IntegerArgumentType.getInteger(ctx, "minutes");
                                            TimeController.setDayTicks(minutes * 20L * 60L, ctx.getSource().getServer());
                                            ctx.getSource().sendSuccess(() -> Component.literal("Set day length to " + minutes + " minutes."), false);
                                            return 1;
                                        }))))
                .then(Commands.literal("nightlength")
                        .then(Commands.literal("get")
                                .executes(ctx -> {
                                    long minutes = TimeController.getNightTicks() / 20 / 60;
                                    ctx.getSource().sendSuccess(() -> Component.literal("Night length: " + minutes + " minutes."), false);
                                    return 1;
                                }))
                        .then(Commands.literal("set")
                                .then(Commands.argument("minutes", IntegerArgumentType.integer(1, 1440))
                                        .executes(ctx -> {
                                            int minutes = IntegerArgumentType.getInteger(ctx, "minutes");
                                            TimeController.setNightTicks(minutes * 20L * 60L, ctx.getSource().getServer());
                                            ctx.getSource().sendSuccess(() -> Component.literal("Set night length to " + minutes + " minutes."), false);
                                            return 1;
                                        }))))
        );
    }
}