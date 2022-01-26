package dev.felnull.fnsm.commands;

import com.mojang.brigadier.CommandDispatcher;
import dev.felnull.fnjl.util.FNURLUtil;
import dev.felnull.fnsm.music.MusicManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import red.felnull.ikenainbs.NBS;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

public class MusicCommand {
    public static void reg(CommandDispatcher<CommandSource> d) {
        d.register(Commands.literal("music").executes(n -> play(n.getSource())));
    }

    public static int play(CommandSource src) {
        src.sendSuccess(new StringTextComponent("test"), false);
        try {
            Vector3d pos = src.getPosition();
            ResourceLocation dim = src.getLevel().dimension().location();
            UUID id = MusicManager.getInstance().start(new NBS(FNURLUtil.getStream(new URL("https://cdn.discordapp.com/attachments/935404229873504346/935404276321239060/HQMComplete.nbs"))), () -> pos, () -> dim);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
