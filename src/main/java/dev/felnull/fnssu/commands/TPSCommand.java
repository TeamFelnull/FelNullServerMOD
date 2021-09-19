package dev.felnull.fnssu.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.text.DecimalFormat;

public class TPSCommand {
    private static final DecimalFormat TIME_FORMATTER = new DecimalFormat("########0.000");

    public static void reg(CommandDispatcher<CommandSource> d) {
        d.register(Commands.literal("tps").executes(n -> showTPS(n.getSource())));
    }

    public static int showTPS(CommandSource src) {
        src.sendSuccess(new StringTextComponent("現在のディメンションのTPS: ").append(TIME_FORMATTER.format(getTPS(src.getLevel()))), false);

        double avgs = 0;
        int cnt = 0;

        for (ServerWorld dim : src.getServer().getAllLevels()) {
            cnt++;
            avgs += getTPS(dim);
        }
        avgs /= cnt;
        src.sendSuccess(new StringTextComponent("現在平均のTPS: ").append(TIME_FORMATTER.format(avgs)), false);

        return 1;
    }

    private static double getTPS(ServerWorld world) {
        long[] times = world.getServer().getTickTime(world.dimension());
        double worldTickTime = mean(times) * 1.0E-6D;
        double worldTPS = Math.min(1000.0 / worldTickTime, 20);
        return worldTPS;
    }

    private static long mean(long[] values) {
        long sum = 0L;
        for (long v : values)
            sum += v;
        return sum / values.length;
    }
}
