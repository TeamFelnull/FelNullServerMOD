package red.felnull.fnssu.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import red.felnull.fnssu.oldmusic.MusicManager;
import red.felnull.fnssu.oldmusic.NBSMusicData;

public class MusicCommand {
    public static void reg(CommandDispatcher<CommandSource> d) {
        d.register(Commands.literal("music")
                .then(Commands.argument("url", StringArgumentType.string()).executes(n -> start(n.getSource(), StringArgumentType.getString(n, "url"), ""))
                        .then(Commands.argument("name", StringArgumentType.string()).executes(n -> start(n.getSource(), StringArgumentType.getString(n, "url"), StringArgumentType.getString(n, "name"))))));

        d.register(Commands.literal("regmusic")
                .then(Commands.argument("url", StringArgumentType.string()).then(Commands.argument("name", StringArgumentType.string()).executes(n -> save(n.getSource(), StringArgumentType.getString(n, "url"), StringArgumentType.getString(n, "name"))))));

        d.register(Commands.literal("spmusic").then(Commands.argument("speed", IntegerArgumentType.integer(1, 100)).executes(n -> changeSpeed(n.getSource(), IntegerArgumentType.getInteger(n, "speed")))));

    }

    public static int changeSpeed(CommandSource src, int sp) {
        src.sendSuccess(new StringTextComponent("速度を" + sp + "に変更しました"), false);
        NBSMusicData.speed = sp;
        return 1;
    }

    public static int start(CommandSource src, String url, String name) {
        try {
            MusicManager.getInstance().start(src.getDisplayName().getString(), src.getLevel().dimension(), src.getPosition(), url, name);
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
