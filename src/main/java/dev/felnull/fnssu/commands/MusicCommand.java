package dev.felnull.fnssu.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import dev.felnull.fnssu.music.MusicManager;

public class MusicCommand {
    public static void reg(CommandDispatcher<CommandSource> d) {
        d.register(Commands.literal("music")
                .then(Commands.literal("play").then(Commands.argument("urlorregistername", StringArgumentType.string()).executes(n -> start(n.getSource(), StringArgumentType.getString(n, "urlorregistername"), "", null)).then(Commands.argument("name", StringArgumentType.string()).executes(n -> start(n.getSource(), StringArgumentType.getString(n, "urlorregistername"), StringArgumentType.getString(n, "name"), null)))))
                .then(Commands.literal("myplay").then(Commands.argument("urlorregistername", StringArgumentType.string()).executes(n -> start(n.getSource(), StringArgumentType.getString(n, "urlorregistername"), "", n.getSource().getPlayerOrException())).then(Commands.argument("name", StringArgumentType.string()).executes(n -> start(n.getSource(), StringArgumentType.getString(n, "urlorregistername"), StringArgumentType.getString(n, "name"), n.getSource().getPlayerOrException())))))
                .then(Commands.literal("stop").then(Commands.argument("name", StringArgumentType.string()).executes(n -> stop(n.getSource(), StringArgumentType.getString(n, "name")))))
                .then(Commands.literal("speed").then(Commands.argument("name", StringArgumentType.string()).then(Commands.argument("speed", FloatArgumentType.floatArg(0.1f, 364f)).executes(n -> changeSpeed(n.getSource(), StringArgumentType.getString(n, "name"), FloatArgumentType.getFloat(n, "speed"))))))
                .then(Commands.literal("register").then(Commands.argument("name", StringArgumentType.string()).then(Commands.argument("url", StringArgumentType.string()).executes(n -> save(n.getSource(), StringArgumentType.getString(n, "url"), StringArgumentType.getString(n, "name"))))))
        );
    }

    public static int changeSpeed(CommandSource src, String name, float sp) {
        if ("all".equals(name) || MusicManager.getInstance().getPlayingNames().contains(name)) {
            if ("all".equals(name))
                for (String playingName : MusicManager.getInstance().getPlayingNames()) {
                    MusicManager.getInstance().getPlayer(playingName).setSpeed(sp);
                }
            else
                MusicManager.getInstance().getPlayer(name).setSpeed(sp);

            src.sendSuccess(new StringTextComponent("速度を" + sp + "に変更しました"), false);
        } else {
            src.sendFailure(new StringTextComponent("変更できませんでした"));
        }
        return 1;
    }

    public static int stop(CommandSource src, String name) {
        if (MusicManager.getInstance().getPlayingNames().contains(name)) {
            MusicManager.getInstance().stop(name);
            src.sendSuccess(new StringTextComponent("停止しました"), false);
        } else {
            src.sendFailure(new StringTextComponent("停止できませんでした"));
        }
        return 1;
    }

    public static int start(CommandSource src, String url, String name, ServerPlayerEntity playerEntity) {
//Collections.singleton(p_198483_1_.getSource().getPlayerOrException()
        try {
            MusicManager.getInstance().start(src.getDisplayName().getString(), src.getLevel().dimension(), src.getPosition(), url, name, playerEntity);
            src.sendSuccess(new StringTextComponent("再生開始します"), false);
        } catch (Exception ex) {
            ex.printStackTrace();
            src.sendFailure(new StringTextComponent("再生失敗 :" + ex.getLocalizedMessage()));
        }
        return 1;
    }

    public static int save(CommandSource src, String url, String name) {
        MusicManager.getInstance().addMusicChach(url, name);
        src.sendSuccess(new StringTextComponent("URLを保存しました"), false);
        return 1;
    }
}
