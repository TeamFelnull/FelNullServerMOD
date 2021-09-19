package dev.felnull.fnssu.music;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;
import dev.felnull.fnssu.util.ServerUtils;
import red.felnull.ikenainbs.Layer;
import red.felnull.ikenainbs.NoteData;

import java.util.function.Supplier;

public class MusicPlayer extends Thread {
    private final MusicSource source;
    private final Supplier<RegistryKey<World>> dimension;
    private final Supplier<Vector3d> pos;
    private float speed = 1;
    private boolean stop;
    private int tick;

    public MusicPlayer(MusicSource source, Supplier<RegistryKey<World>> dimension, Supplier<Vector3d> pos) {
        this.source = source;
        this.dimension = dimension;
        this.pos = pos;
    }

    @Override
    public void run() {
        if (stop)
            return;
        for (int i = 0; i < source.getTickCont(); i++) {
            for (Layer layer : source.getNBS().getLayers().values()) {
                if (!layer.getNoteData().containsKey(tick))
                    continue;
                NoteData data = layer.getNoteData().get(tick);
                float volume = layer.getVolume() / 100f;
                playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(data.getInstrumentType().getRegistryName116())), volume, (float) Math.pow(2.0D, (double) (data.getKey() - 45) / 12.0D));
            }
            try {
                long tickSpeed = (long) (1000f / ((float) source.getNBS().getTempo() / 100f));
                sleep((long) ((float) tickSpeed * speed));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tick++;
            if (stop)
                return;
        }
    }

    private void playSound(SoundEvent soundEvent, float volume, float pitch) {
        RegistryKey<World> dim = dimension.get();
        Vector3d posi = pos.get();

        if (dim == null || posi == null)
            return;

        ServerUtils.getMinecraftServer().execute(() -> {
            ServerWorld world = ServerUtils.getMinecraftServer().getLevel(dim);
            world.playSound(null, posi.x, posi.y, posi.z, soundEvent, SoundCategory.MASTER, volume, pitch);
        });
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void stoped() {
        this.stop = true;
    }
}
