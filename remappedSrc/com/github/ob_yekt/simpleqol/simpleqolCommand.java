package com.github.ob_yekt.simpleqol;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.permission.Permission;
import net.minecraft.command.permission.PermissionLevel;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class simpleqolCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("simpleqol")
                .requires(source -> source.getPermissions().hasPermission(new Permission.Level(PermissionLevel.MODERATORS)))
                .then(CommandManager.literal("daylength")
                        .then(CommandManager.literal("get")
                                .executes(ctx -> {
                                    long minutes = TimeController.getDayTicks() / 20 / 60;
                                    ctx.getSource().sendFeedback(() -> Text.literal("Day length: " + minutes + " minutes."), false);
                                    return 1;
                                }))
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument("minutes", IntegerArgumentType.integer(1, 1440))
                                        .executes(ctx -> {
                                            int minutes = IntegerArgumentType.getInteger(ctx, "minutes");
                                            TimeController.setDayTicks(minutes * 20L * 60L, ctx.getSource().getServer());
                                            ctx.getSource().sendFeedback(() -> Text.literal("Set day length to " + minutes + " minutes."), false);
                                            return 1;
                                        }))))
                .then(CommandManager.literal("nightlength")
                        .then(CommandManager.literal("get")
                                .executes(ctx -> {
                                    long minutes = TimeController.getNightTicks() / 20 / 60;
                                    ctx.getSource().sendFeedback(() -> Text.literal("Night length: " + minutes + " minutes."), false);
                                    return 1;
                                }))
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument("minutes", IntegerArgumentType.integer(1, 1440))
                                        .executes(ctx -> {
                                            int minutes = IntegerArgumentType.getInteger(ctx, "minutes");
                                            TimeController.setNightTicks(minutes * 20L * 60L, ctx.getSource().getServer());
                                            ctx.getSource().sendFeedback(() -> Text.literal("Set night length to " + minutes + " minutes."), false);
                                            return 1;
                                        }))))
        );
    }
}