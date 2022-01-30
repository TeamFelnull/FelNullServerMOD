package dev.felnull.fnsm.music;

import dev.felnull.fnnbs.NBS;
import dev.felnull.fnnbs.player.AsyncNBSPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;
import java.util.function.Supplier;

public class MusicPlayer {
    private static int numCt;
    private final UUID uuid;
    private final NBS nbs;
    private final Supplier<Vector3d> playPos;
    private final Supplier<ResourceLocation> playDim;
    private final int loopCount;
    private final int num;
    private AsyncNBSPlayer nbsPlayer;

    public MusicPlayer(UUID uuid, NBS nbs, Supplier<Vector3d> playPos, Supplier<ResourceLocation> playDim, int loopCount) {
        this.uuid = uuid;
        this.nbs = nbs;
        this.playPos = playPos;
        this.playDim = playDim;
        this.loopCount = loopCount;
        this.num = numCt++;
    }

    public void start(boolean loop) {
        if (nbsPlayer == null) {
            nbsPlayer = new AsyncNBSPlayer(nbs, (iInstrument, volume, pitch, stereo) -> {
                SoundEvent event = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(iInstrument.getSoundName()));
                ring(event, volume, pitch);
            });
            nbsPlayer.setForcedLoop(loop);
            nbsPlayer.setMaxLoopCount(loopCount);
            nbsPlayer.setPlayThreadName("Music Player " + num);
            nbsPlayer.playStart();
        }
    }

    public void stop() {
        if (nbsPlayer != null) {
            nbsPlayer.playStop();
            nbsPlayer = null;
        }
    }

    public boolean isPlaying() {
        if (nbsPlayer != null)
            return nbsPlayer.isPlaying();
        return false;
    }

    private void ring(SoundEvent soundEvent, float volume, float pitch) {
        if (soundEvent == null) return;
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        if (server != null) {
            server.submit(() -> {
                ServerWorld world = getWorld(server);
                if (world != null) {
                    Vector3d p = playPos.get();
                    world.playSound(null, p.x, p.y, p.z, soundEvent, SoundCategory.MASTER, volume, pitch);
                }
            });
        }
    }

    private ServerWorld getWorld(MinecraftServer server) {
        for (ServerWorld level : server.getAllLevels()) {
            if (level.dimension().location().equals(playDim.get()))
                return level;
        }
        return null;
    }

}
