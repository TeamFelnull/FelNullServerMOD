package dev.felnull.fnsm;

import dev.felnull.fnjl.util.FNDataUtil;
import dev.felnull.fnnbs.NBS;
import dev.felnull.fnsm.discord.FNSMDiscord;
import dev.felnull.fnsm.handler.NotificationDiscordHandler;
import dev.felnull.fnsm.handler.ServerHandler;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mod(FelNullServerMOD.MODID)
public class FelNullServerMOD {
    public static final String MODID = "felnullservermod";
    public static NBS hqmCompNBS;
    public static NBS worldOfTono;
    public static NBS godHand;

    public FelNullServerMOD() {
        ServerConfig.init();
        FNSMDiscord.init();
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        FNSMDiscord.setStatus(OnlineStatus.IDLE, Activity.watching("サーバー初期読み込み中"));
        MinecraftForge.EVENT_BUS.register(ServerHandler.class);
        MinecraftForge.EVENT_BUS.register(NotificationDiscordHandler.class);
        try {
            hqmCompNBS = new NBS(FNDataUtil.resourceExtractor(FelNullServerMOD.class, "data/nbs/HQMComplete.nbs"));
            worldOfTono = new NBS(FNDataUtil.resourceExtractor(FelNullServerMOD.class, "data/nbs/world_of_tono.nbs"));
            godHand = new NBS(FNDataUtil.resourceExtractor(FelNullServerMOD.class, "data/nbs/God_Hand.nbs"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Path getModFolder() {
        return Paths.get("./" + MODID);
    }
}
