package dev.felnull.fnsm.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.felnull.fnsm.music.MusicManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;

import java.net.URL;
import java.util.Map;
import java.util.UUID;

public class MusicCommand {
    public static void reg(CommandDispatcher<CommandSource> d) {
        d.register(Commands.literal("music")
                .then(Commands.literal("play").then(Commands.argument("name", StringArgumentType.string()).executes(n -> play(n.getSource(), StringArgumentType.getString(n, "name"), null, false, 0))
                        .then(Commands.argument("id", StringArgumentType.string()).executes(n -> play(n.getSource(), StringArgumentType.getString(n, "name"), StringArgumentType.getString(n, "id"), false, 0))
                                .then(Commands.argument("loop", IntegerArgumentType.integer(0, 10)).executes(n -> play(n.getSource(), StringArgumentType.getString(n, "name"), StringArgumentType.getString(n, "id"), false, IntegerArgumentType.getInteger(n, "loop")))))))
                .then(Commands.literal("myplay").then(Commands.argument("name", StringArgumentType.string()).executes(n -> play(n.getSource(), StringArgumentType.getString(n, "name"), null, true, 0))
                        .then(Commands.argument("id", StringArgumentType.string()).executes(n -> play(n.getSource(), StringArgumentType.getString(n, "name"), StringArgumentType.getString(n, "id"), true, 0))
                                .then(Commands.argument("loop", IntegerArgumentType.integer(0, 10)).executes(n -> play(n.getSource(), StringArgumentType.getString(n, "name"), StringArgumentType.getString(n, "id"), true, IntegerArgumentType.getInteger(n, "loop")))))))
                .then(Commands.literal("list").executes(n -> list(n.getSource())))
                .then(Commands.literal("register").then(Commands.argument("name", StringArgumentType.string()).then(Commands.argument("url", StringArgumentType.string()).executes(n -> register(n.getSource(), StringArgumentType.getString(n, "name"), StringArgumentType.getString(n, "url")))))));
    }

    public static int play(CommandSource src, String name, String id, boolean myPlay, int loopCount) {
        Vector3d pos = new Vector3d(src.getPosition().x, src.getPosition().y, src.getPosition().z);
        ResourceLocation dim = new ResourceLocation(src.getLevel().dimension().location().toString());
        ServerPlayerEntity sp = null;
        try {
            sp = src.getPlayerOrException();
        } catch (Exception ignored) {
        }
        if (sp == null && myPlay) {
            src.sendFailure(new StringTextComponent("プレイヤーを取得できませんでした"));
            return 1;
        }
        if (id == null)
            id = UUID.randomUUID().toString();
        try {
            ServerPlayerEntity finalSp = sp;
            MusicManager.getInstance().start(name, id, () -> {
                if (myPlay)
                    return finalSp.isAlive() ? finalSp.position() : pos;
                return pos;
            }, () -> {
                if (myPlay)
                    return finalSp.isAlive() ? finalSp.level.dimension().location() : dim;
                return dim;
            }, loopCount);
            src.sendSuccess(new StringTextComponent("曲を再生します"), false);
        } catch (IllegalStateException ex) {
            src.sendFailure(new StringTextComponent(ex.getMessage()));
        } catch (Exception ex) {
            src.sendFailure(new StringTextComponent("曲の再生に失敗しました: " + ex.getMessage()));
        }
        return 1;
    }

    public static int list(CommandSource src) {
        Map<String, String> urls = MusicManager.getInstance().getMusicURLs();
        src.sendSuccess(new StringTextComponent(urls.size() + "個登録されています"), false);
        urls.forEach((n, m) -> {
            String url = m;
            if (url.length() > 50)
                url = url.substring(0, 47) + "...";
            src.sendSuccess(new StringTextComponent(n + ": " + url), false);
        });
        return 1;
    }

    public static int register(CommandSource src, String name, String url) {
        try {
            URL theUrl = new URL(url);
            MusicManager.getInstance().musicURLRegister(name, url);
            src.sendSuccess(new StringTextComponent("音楽URLを登録しました"), false);
        } catch (Exception ex) {
            src.sendFailure(new StringTextComponent("音楽URLを登録に失敗しました: " + ex.getMessage()));
        }
        return 1;
    }
}
