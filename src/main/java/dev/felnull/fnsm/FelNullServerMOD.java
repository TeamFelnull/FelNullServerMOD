package dev.felnull.fnsm;

import dev.felnull.fnjl.util.FNDataUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import red.felnull.ikenainbs.NBS;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mod(FelNullServerMOD.MODID)
public class FelNullServerMOD {
    public static final String MODID = "felnullservermod";
    public static NBS hqmCompNBS;

    public FelNullServerMOD() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        ServerConfig.init();
        MinecraftForge.EVENT_BUS.register(ServerHandler.class);
        try {
            hqmCompNBS = new NBS(FNDataUtil.resourceExtractor(FelNullServerMOD.class, "data/nbs/HQMComplete.nbs"));
        } catch (IOException e) {
        }
    }

    public static Path getModFolder() {
        return Paths.get("./" + MODID);
    }
}
