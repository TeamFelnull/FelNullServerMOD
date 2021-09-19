package dev.felnull.fnssu;

import dev.felnull.fnssu.handler.ServerHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(FelNullServerSideUtil.MODID)
public class FelNullServerSideUtil {
    public static final Logger LOGGER = LogManager.getLogger(FelNullServerSideUtil.class);
    public static final String MODID = "felnullserversideutil";

    public FelNullServerSideUtil() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(ServerHandler.class);
    }
}
