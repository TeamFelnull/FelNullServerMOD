package dev.felnull.fnsm.music;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.registries.ForgeRegistries;
import red.felnull.ikenainbs.Layer;
import red.felnull.ikenainbs.NBS;
import red.felnull.ikenainbs.NoteData;

import java.util.UUID;
import java.util.function.Supplier;

public class MusicPlayer {
    private final UUID uuid;
    private final NBS nbs;
    private final Supplier<Vector3d> playPos;
    private final Supplier<ResourceLocation> playDim;
    private PlayThread playThread;
    private boolean stop;

    public MusicPlayer(UUID uuid, NBS nbs, Supplier<Vector3d> playPos, Supplier<ResourceLocation> playDim) {
        this.uuid = uuid;
        this.nbs = nbs;
        this.playPos = playPos;
        this.playDim = playDim;
    }

    public void start() {
        if (playThread == null) {
            playThread = new PlayThread();
            playThread.start();
        }
    }

    public void stop() {
        if (playThread != null) {
            if (!stop)
                playThread.interrupt();
            playThread = null;
        }
    }

    private class PlayThread extends Thread {
        private int tick;

        @Override
        public void run() {
            while (!isInterrupted()) {
                for (Layer layer : nbs.getLayers().values()) {
                    if (!layer.getNoteData().containsKey(tick))
                        continue;
                    NoteData data = layer.getNoteData().get(tick);
                    float volume = layer.getVolume() / 100f;
                    ring(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(data.getInstrumentType().getRegistryName116())), volume, (float) Math.pow(2.0D, (double) (data.getKey() - 45) / 12.0D));
                }
                long tickSpeed = (long) (1000f / ((float) nbs.getTempo() / 100f));
                try {
                    sleep((long) ((float) 100));
                } catch (InterruptedException e) {
                    break;
                }
                tick++;
                if (nbs.getLength() < tick)
                    break;
            }
            stop = true;
            MusicManager.getInstance().stop(uuid);
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
}
